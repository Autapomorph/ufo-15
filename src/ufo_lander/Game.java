package ufo_lander;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import static ufo_lander.GameManager.gameMode;

public class Game
{
    private UFO playerUFO;   
    private LandingArea landingArea;
    
    private BufferedImage backgroundImg;   
    private BufferedImage backgroundSkyImg;
    private BufferedImage redBorderImg;
    
    private Font fontMainMenu = null;
    
    public Game()
    {
        GameManager.gameState = GameManager.GameState.CONTENT_LOADING;
        start();
    }
    
    private void start()
    {
        Thread threadForInitGame = new Thread() 
        {
            @Override
            public void run()
            {
                initialize();
                loadContent();     
                GameManager.gameState = GameManager.GameState.PLAYING;
            }
        };
        threadForInitGame.start();
    }
    
    private void initialize()
    {
        playerUFO = new UFO();
        landingArea  = new LandingArea();
    }
    
    private void loadContent()
    {
        try 
        {
            InputStream fontInputStream = new BufferedInputStream(getClass().getResourceAsStream("/res/fonts/1979_regular.ttf"));
            fontMainMenu = Font.createFont(Font.TRUETYPE_FONT, fontInputStream).deriveFont(Font.PLAIN, 16); 
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
            backgroundImg = ImageIO.read(getClass().getResourceAsStream("/res/drawable/background.jpg"));
            backgroundSkyImg = ImageIO.read(getClass().getResourceAsStream("/res/drawable/background_sky.jpg"));
            redBorderImg = ImageIO.read(getClass().getResourceAsStream("/res/drawable/red_border.png"));
        }
        catch (IOException | IllegalArgumentException ex) 
        {
            System.out.println("Ошибка считывания ресурсов фона");
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            
            JOptionPane.showConfirmDialog(null, "Ошибка считывания ресурсов фона\nПриложение будет закрыто",
                    "Ошибка", JOptionPane.PLAIN_MESSAGE);
            System.exit(1);
        }
    }
    
    public void restartGame()
    {
        playerUFO.resetUFO();
        landingArea.resetLandingArea();
    }
    
    public void gameMode()
    {
        switch (gameMode)
        {
            case EASY:
                playerUFO.speedAccelerating = 2;
                playerUFO.speedFalling = 1;
                break;
               
            case NORMAL:
                playerUFO.speedAccelerating = 16;
                playerUFO.speedFalling = 8;
                break;
             
            case HARD:

                playerUFO.speedAccelerating = 32;
                playerUFO.speedFalling = 16;
                break;
                
            case BEER:
                playerUFO.speedAccelerating = 64;
                playerUFO.speedFalling = 32;
                break;
        }
    }
    
    public void update(long gameTime, Point mousePosition)
    {
        playerUFO.update();
        landingArea.update();

        if(playerUFO.ufoGameY + playerUFO.ufoImgHeight - 15 > landingArea.y)
        {
            if((playerUFO.ufoX + 15 > landingArea.x) && (playerUFO.ufoX + playerUFO.ufoImgWidth - 15 < landingArea.x + landingArea.landingAreaImgWidth))
            {
                if(playerUFO.speedY <= playerUFO.maxLandingVSpeed && Math.abs(playerUFO.speedX) <= playerUFO.maxLandingHSpeed)
                    playerUFO.landed = true;
                else
                    playerUFO.crashed = true;
            }
            else
                playerUFO.crashed = true;
                
            GameManager.gameState = GameManager.GameState.GAMEOVER;
        }
    }
    
    public void paint(Graphics2D g2d, Point mousePosition)
    {
        int min = 0, max = 0;
        if (playerUFO.ufoGameY <= 0)
        {
            g2d.drawImage(backgroundImg, 0, -playerUFO.ufoGameY, GameManager.frameWidth, GameManager.frameHeight, null);
            g2d.drawImage(backgroundSkyImg, 0, -playerUFO.ufoGameY-570, GameManager.frameWidth, GameManager.frameHeight, null);
            
            if (playerUFO.ufoGameY <= -570)
            {
                max = (Math.abs(playerUFO.ufoGameY) / 570) * 570;
                min = max + 570;

                if (playerUFO.ufoGameY > -min && playerUFO.ufoGameY < -max)
                {
                    g2d.drawImage(backgroundSkyImg, 0, -max-playerUFO.ufoGameY, GameManager.frameWidth, GameManager.frameHeight, null);
                    g2d.drawImage(backgroundSkyImg, 0, -min-playerUFO.ufoGameY, GameManager.frameWidth, GameManager.frameHeight, null);
                }
            }
               
            if (playerUFO.ufoGameY  >= -100)
            {
                landingArea.visible = true;
                landingArea.paint(g2d, landingArea.y - playerUFO.ufoGameY);
            }
            else
            {
                landingArea.visible = false;
                landingArea.paint(g2d, 0);
            } 
        }
        else
        {
            g2d.drawImage(backgroundImg, 0, 0, GameManager.frameWidth, GameManager.frameHeight, null);
            landingArea.visible = true;
            landingArea.paint(g2d, landingArea.y);
        }
   
        playerUFO.paint(g2d);
    }
    
    
    public void paintGameOver(Graphics2D g2d, Point mousePosition, long gameTime)
    {
        String str = "";
        
        paint(g2d, mousePosition);
        
        g2d.setFont(fontMainMenu);
        g2d.drawString("Press R to restart", GameManager.frameWidth / 2 - 120, GameManager.frameHeight / 3 + 45);
        
        if(playerUFO.landed)
        {
            for (int i=9; i>5; i--)
                str += String.valueOf(gameTime).toCharArray()[String.valueOf(gameTime).length() - i];
            
            g2d.drawString(
                "You have successfully landed in " +
                gameTime / GameManager.SECNANO +
                "." + str + " seconds",
                GameManager.frameWidth / 2 - 313,
                GameManager.frameHeight / 3
            );
        }
        else
        {
            g2d.setColor(Color.RED);
            g2d.drawString("You have crashed the UFO", GameManager.frameWidth / 2 - 175, GameManager.frameHeight / 3);
            g2d.drawImage(redBorderImg, 0, 0, GameManager.frameWidth, GameManager.frameHeight, null);
        }
    }    
}