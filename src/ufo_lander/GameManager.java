package ufo_lander;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class GameManager extends Canvas
{        
    public static int frameWidth;
    public static int frameHeight;

    public static final long SECNANO = 1000000000L;
    public static final long SECMILLI = 1000000L;
    
    public int gameFPS = 16;
    public long gameUpdatePeriod;
    
    public static enum GameState {STARTING, VISUALIZING, CONTENT_LOADING, MAIN_MENU, OPTIONS, HELP, PLAYING, GAMEOVER, DESTROYED}
    public static enum GameMode {EASY, NORMAL, HARD, BEER}
    public static GameState gameState;
    public static GameMode gameMode;
    
    private long gameTime;
    private long lastTime;
    
    private BufferedImage ufoLanderMenuImg;
    
    private Font fontGame = null;
    
    private Game game;
    
    public GameManager() 
    {
        super();
        gameState = GameState.VISUALIZING;
        gameMode = GameMode.EASY;
        start();
    }
  
    private void start()
    {
        Thread gameThread = new Thread() {
            @Override
            public void run(){
                gameLoop();
            }
        };
        gameThread.start();
    }
    
    private void loadContent()
    {         
        try 
        {
            InputStream fontInputStream = new BufferedInputStream(getClass().getResourceAsStream("/res/fonts/1979_regular.ttf"));
            fontGame = Font.createFont(Font.TRUETYPE_FONT, fontInputStream);
        }
        catch (FontFormatException | IOException | NullPointerException ex) 
        {
            System.err.println("Ошибка считывания шрифтов");
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            
            JOptionPane.showConfirmDialog(null, "Ошибка считывания шрифтов\nПриложение будет закрыто",
                    "Ошибка", JOptionPane.PLAIN_MESSAGE); 
            System.exit(1);
        }
        
        try
        {
            ufoLanderMenuImg = ImageIO.read(getClass().getResourceAsStream("/res/drawable/background.jpg"));
        }
        catch (IOException | IllegalArgumentException ex) 
        {  
            System.err.println("Ошибка считывания ресурса меню");  
            Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, null, ex);
            
            JOptionPane.showConfirmDialog(null, "Ошибка считывания ресурса меню\nПриложение будет закрыто",
                    "Ошибка", JOptionPane.PLAIN_MESSAGE);   
            System.exit(1);
        }
    }
    
    private void gameLoop()
    {
        long visualizingTime = 0, lastVisualizingTime = System.nanoTime();
        long beginTime, timeTaken, timeLeft;
        
        while (true)
        {            
            beginTime = System.nanoTime();
            
            switch (gameState)
            {
                case PLAYING:
                    gameTime += System.nanoTime() - lastTime; 
                    game.update(gameTime, mousePosition()); 
                    lastTime = System.nanoTime();
                    break;
                
                case STARTING:
                    loadContent();
                    gameState = GameState.MAIN_MENU;
                    break;
                    
                case VISUALIZING:
                    if(this.getWidth() > 1 && visualizingTime > SECNANO)
                    {
                        frameWidth = this.getWidth();
                        frameHeight = this.getHeight();
                        gameState = GameState.STARTING;
                    }
                    else
                    {
                        visualizingTime += System.nanoTime() - lastVisualizingTime;
                        lastVisualizingTime = System.nanoTime();
                    }
                break;
            }
            
            repaint();
            
            timeTaken = System.nanoTime() - beginTime;
            timeLeft = (gameUpdatePeriod - timeTaken) / SECMILLI;

            if (timeLeft < 10) 
                timeLeft = 10;
            try 
            {
                 Thread.sleep(timeLeft);
            } 
            catch (InterruptedException ex) 
            { 
                System.err.println("Ошибка при выполнении. Поток прерван");
                Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, null, ex);
                
                JOptionPane.showConfirmDialog(null, "Ошибка при выполнении. Поток прерван\nПриложение будет закрыто",
                    "Ошибка", JOptionPane.PLAIN_MESSAGE);
                System.exit(1);
            }
        }
    }
    
    @Override
    public void paint(Graphics2D g2d) 
    {    
        switch (gameState)
        {
            case PLAYING:
                game.paint(g2d, mousePosition());
                break;
                
            case GAMEOVER:
                game.paintGameOver(g2d, mousePosition(), gameTime);
                break;
                
            case MAIN_MENU:
                g2d.drawImage(ufoLanderMenuImg, 0, 0, frameWidth, frameHeight, null);
                
                g2d.setColor(Color.WHITE);
                fontGame = fontGame.deriveFont(Font.PLAIN, 33);
                g2d.setFont(fontGame);
                g2d.drawString("UFO LANDER", frameWidth / 2 - 149, frameHeight / 2 - 200);
                
                fontGame = fontGame.deriveFont(Font.PLAIN, 16);
                g2d.setFont(fontGame);
                
                g2d.drawString("Play", frameWidth / 2 - 335, frameHeight / 2 + 20);
                g2d.drawString("Options", frameWidth / 2 - 335, frameHeight / 2 + 50);
                g2d.drawString("Help", frameWidth / 2 - 335, frameHeight / 2 + 80);
                g2d.drawString("Exit", frameWidth / 2 - 335, frameHeight / 2 + 110);
                   
                g2d.drawString("Press SPACE to start the game", frameWidth / 2 - 205, frameHeight / 2 + 280);
                break;
                
            case OPTIONS:  
                g2d.setColor(Color.WHITE);
                fontGame = fontGame.deriveFont(Font.PLAIN, 16);
                g2d.setFont(fontGame);
                
                g2d.drawImage(ufoLanderMenuImg, 0, 0, frameWidth, frameHeight, null);
                g2d.drawString("Choose difficulty", frameWidth / 2 - 350, frameHeight / 2 - 20);
                g2d.drawString("Easy", frameWidth / 2 - 335, frameHeight / 2 + 20);
                g2d.drawString("Normal", frameWidth / 2 - 335, frameHeight / 2 + 50);
                g2d.drawString("Hard", frameWidth / 2 - 335, frameHeight / 2 + 80);
                g2d.drawString("Beer", frameWidth / 2 - 335, frameHeight / 2 + 110);
                break;
            
            case HELP:
                g2d.setColor(Color.WHITE);
                fontGame = fontGame.deriveFont(Font.PLAIN, 16);
                g2d.setFont(fontGame);
                
                g2d.drawImage(ufoLanderMenuImg, 0, 0, frameWidth, frameHeight, null);
                g2d.drawString("Help", frameWidth / 2 - 350, frameHeight / 2 - 20);
                g2d.drawString("Use A/D/SPACE keys to control UFO", frameWidth / 2 - 335, frameHeight / 2 + 20);
                g2d.drawString("Use R key to restart game", frameWidth / 2 - 335, frameHeight / 2 + 50);
                g2d.drawString("Press ESCAPE to return back to Main Menu", frameWidth / 2 - 335, frameHeight / 2 + 80);
                break;
             
            case CONTENT_LOADING:
                g2d.setColor(Color.WHITE);
                fontGame = fontGame.deriveFont(Font.PLAIN, 16);
                g2d.setFont(fontGame);
                
                g2d.drawString("LOADING...", frameWidth / 2 - 50, frameHeight / 2);
                break;
        }
    }

    private void newGame()
    {
        gameUpdatePeriod = SECNANO / gameFPS;
        
        gameTime = 0;
        lastTime = System.nanoTime();
        
        game = new Game();
    }
    
    private void restartGame()
    {
        gameTime = 0;
        lastTime = System.nanoTime();
        
        game.restartGame();
        
        gameState = GameState.PLAYING;
    }
    
    private Point mousePosition()
    {
        try
        {
            Point mousePos = this.getMousePosition();
            
            if(mousePos != null)
                return this.getMousePosition();
            else
                return new Point(0, 0);
        }
        catch (Exception ex)
        {
            return new Point(0, 0);
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
        switch (gameState)
        {
            case MAIN_MENU:
                if(e.getButton() == MouseEvent.BUTTON1)
                {
                    if (mousePosition().x >= 60 && mousePosition().x <= 120 && mousePosition().y >= 290 && mousePosition().y <= 305)
                        newGame();
                    
                    if (mousePosition().x >= 60 && mousePosition().x <= 150 && mousePosition().y >= 320 && mousePosition().y <= 335)
                        gameState = GameState.OPTIONS;
                    
                    if (mousePosition().x >= 60 && mousePosition().x <= 120 && mousePosition().y >= 350 && mousePosition().y <= 365)
                        gameState = GameState.HELP;
                    
                    if (mousePosition().x >= 60 && mousePosition().x <= 110 && mousePosition().y >= 380 && mousePosition().y <= 395)
                        System.exit(0);
                }
                break;
            
            case OPTIONS:
                if(e.getButton() == MouseEvent.BUTTON1)
                {
                    if (mousePosition().x >= 60 && mousePosition().x <= 120 && mousePosition().y >= 290 && mousePosition().y <= 305)
                    {
                        gameMode = GameMode.EASY;
                        gameState = GameState.MAIN_MENU;
                    }
                        
                    if (mousePosition().x >= 60 && mousePosition().x <= 150 && mousePosition().y >= 320 && mousePosition().y <= 335)
                    {
                        gameMode = GameMode.NORMAL;
                        gameState = GameState.MAIN_MENU;
                    }
                    
                    if (mousePosition().x >= 60 && mousePosition().x <= 130 && mousePosition().y >= 350 && mousePosition().y <= 365)
                    {
                        gameMode = GameMode.HARD;
                        gameState = GameState.MAIN_MENU;
                    }
                    
                    if (mousePosition().x >= 60 && mousePosition().x <= 125 && mousePosition().y >= 380 && mousePosition().y <= 395)
                    {
                        gameMode = GameMode.BEER;
                        gameState = GameState.MAIN_MENU;
                    }
                }
                break;
                
            case GAMEOVER:
                System.out.println("MP x:" + mousePosition().x + "\tMP y:" + mousePosition().y);
        }
    }
    
    @Override
    public void keyReleasedFramework(KeyEvent e) 
    {
        switch (gameState)
        {
            case MAIN_MENU:
                if(e.getKeyCode() == KeyEvent.VK_SPACE)
                    newGame();
                break;
                
            case OPTIONS:
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    gameState = GameState.MAIN_MENU;
                break;
            
            case HELP:
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    gameState = GameState.MAIN_MENU;
                break;
            
            case PLAYING:
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    gameState = GameState.MAIN_MENU;
                if(e.getKeyCode() == KeyEvent.VK_R)
                    restartGame();
                break;
                
            case GAMEOVER:
                if(e.getKeyCode() == KeyEvent.VK_R)
                    restartGame();
                
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    gameState = GameState.MAIN_MENU;
                break;
        }
    }   
}