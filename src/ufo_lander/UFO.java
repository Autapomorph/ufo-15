package ufo_lander;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class UFO 
{
    private Random random;
 
    public int ufoX;
    public int ufoY;
    public int ufoGameY;
    private int ufoGameHeight;
    
    public int ufoImgWidth;
    public int ufoImgHeight;
    
    private int exhaustX;
    private int exhaustY;
    
    public int speedX;
    public int speedY;
    
    public int speedAccelerating;
    public int speedFalling;
    
    private int maxHSpeed;
    
    public int maxLandingVSpeed;
    public int maxLandingHSpeed;
    
    public boolean landed;
    public boolean crashed;
    
    private boolean ignition;
    private boolean inverseExhaust;
            
    private BufferedImage ufoImage;
    private BufferedImage ufoImageCrashedExplosion;
    private BufferedImage exhaustImage;
    private BufferedImage exhaustInversedImage;
    
    private Font fontGameUI = null;
    
    public UFO()
    {
        initialize();
        loadContent();
        resetUFO();
    }
    
    private void initialize()
    {
        random = new Random();
        maxHSpeed = 98;
        gameMode();
    }
    
    private void gameMode()
    {
        switch (GameManager.gameMode)
        {
            case EASY:
                speedAccelerating = 2;
                speedFalling = 1;
                maxLandingVSpeed = 10;
                maxLandingHSpeed = 5;
                break;
               
            case NORMAL:
                speedAccelerating = 8;
                speedFalling = 4;
                maxLandingVSpeed = 10;
                maxLandingHSpeed = 5;
                break;
             
            case HARD:
                speedAccelerating = 16;
                speedFalling = 8;
                maxLandingVSpeed = 16;
                maxLandingHSpeed = 5;
                break;
                
            case BEER:
                speedAccelerating = 32;
                speedFalling = 16;
                maxLandingVSpeed = 32;
                maxLandingHSpeed = 5;
                break;
        }
    }
    
    private void loadContent()
    {
        try 
        {
            InputStream fontInputStream = new BufferedInputStream(getClass().getResourceAsStream("/res/fonts/1979_regular.ttf"));
            fontGameUI = Font.createFont(Font.TRUETYPE_FONT, fontInputStream).deriveFont(Font.PLAIN, 13); 
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
            ufoImage = ImageIO.read(getClass().getResourceAsStream("/res/drawable/ufo.png"));
            ufoImageCrashedExplosion = ImageIO.read(getClass().getResourceAsStream("/res/drawable/ufo_crashed.png"));
        
            exhaustImage = ImageIO.read(getClass().getResourceAsStream("/res/drawable/exhaust.png"));
            exhaustInversedImage = ImageIO.read(getClass().getResourceAsStream("/res/drawable/exhaust_inversed.png"));
            
            ufoImgWidth = ufoImage.getWidth();
            ufoImgHeight = ufoImage.getHeight();
        }
        catch (IOException | IllegalArgumentException ex) 
        {
            System.err.println("Ошибка считывания ресурсов UFO");
            Logger.getLogger(UFO.class.getName()).log(Level.SEVERE, null, ex);
            
            JOptionPane.showConfirmDialog(null, "Ошибка считывания ресурсов UFO\nПриложение будет закрыто",
                    "Ошибка", JOptionPane.PLAIN_MESSAGE);  
            System.exit(1);
        }
    }
    
    public final void resetUFO()
    {
        gameMode();
        landed = false;
        crashed = false;
        ignition = false;
        inverseExhaust = false;
        
        ufoX = random.nextInt(GameManager.frameWidth - ufoImgWidth);
        ufoY = 10;
        ufoGameY = ufoY;
        
        exhaustX = ufoX + ufoImgWidth/2 - 12;
        exhaustY = ufoY + ufoImgHeight - 30;
        
        speedX = 0;
        speedY = 0;
    }
    
    public void update()
    {
        inverseExhaust = !inverseExhaust;
        
        if(Canvas.keyboardKeyState(KeyEvent.VK_SPACE))
        {
            speedY -= speedAccelerating;
            ignition = true;
        }          
        else
        {
            speedY += speedFalling;
            ignition = false; 
        }
        
        if (GameManager.gameMode != GameManager.GameMode.BEER)
        {
            if(Canvas.keyboardKeyState(KeyEvent.VK_A))
            {
                if (Math.abs(speedX) <= maxHSpeed)
                    speedX -= speedAccelerating;
            }         
            else if(speedX < 0)
                speedX += speedFalling;

            if(Canvas.keyboardKeyState(KeyEvent.VK_D))
            {
                if (Math.abs(speedX) <= maxHSpeed)
                    speedX += speedAccelerating;
            }    
            else if(speedX > 0)
                speedX -= speedFalling;
        }
        else
        {
            if(Canvas.keyboardKeyState(KeyEvent.VK_D))
            {
                if (Math.abs(speedX) <= maxHSpeed)
                    speedX -= speedAccelerating;
            }         
            else if(speedX < 0)
                speedX += speedFalling;

            if(Canvas.keyboardKeyState(KeyEvent.VK_A))
            {
                if (Math.abs(speedX) <= maxHSpeed)
                    speedX += speedAccelerating;
            }    
            else if(speedX > 0)
                speedX -= speedFalling;
        }
        
        ufoX += speedX;
        ufoY += speedY;
        ufoGameY += speedY;
        ufoGameHeight = -ufoGameY + GameManager.frameHeight - (int)(GameManager.frameHeight * 0.3);
        
        exhaustX += speedX;
        exhaustY += speedY;
        
        if (ufoGameY <= 0)
        {
            ufoY = 0;
            exhaustY = ufoY + ufoImgHeight - 30;
            
            ufoGameY += speedY;
        }
        else
            ufoGameY = ufoY;
        
        if (ufoX + ufoImgWidth <= 0)
        {
            ufoX = GameManager.frameWidth;
            exhaustX = ufoX + ufoImgWidth/2 - 12;
            return;
        }
        
        if (ufoX >= GameManager.frameWidth)
        {
            ufoX = 0 - ufoImgWidth;
            exhaustX = ufoX + ufoImgWidth/2 - 12;
        }
    }
    
    public void paint(Graphics2D g2d)
    {
        if(landed)
        {
            g2d.drawImage(ufoImage, ufoX, ufoY, null);
        }
        else if(crashed)
        {
            g2d.drawImage(ufoImageCrashedExplosion, ufoX - ufoImage.getWidth()/2, 530 - ufoImageCrashedExplosion.getHeight(), null);
        }
        else
        {
            if(ignition)
            {
                if (inverseExhaust)
                    g2d.drawImage(exhaustInversedImage, exhaustX, exhaustY, null);
                else
                    g2d.drawImage(exhaustImage, exhaustX, exhaustY, null);           
            }
            
            g2d.drawImage(ufoImage, ufoX, ufoY, null);
        }
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(fontGameUI);
        
        g2d.drawString("UFO coordinates: " + ufoX + ": " + ufoGameHeight, 5, 15);
        g2d.drawString("Vert speed: " + speedY, 5, 30);
        g2d.drawString("Hort speed: " + speedX, 5, 45);
    }
}