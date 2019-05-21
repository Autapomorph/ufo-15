package ufo_lander;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class LandingArea 
{
    public int x;
    public int y;
    
    private int speedX;
    
    public int landingAreaImgWidth;
    
    private int landingAreaLeftMoveBorder;
    private int landingAreaRightMoveBorder;
    
    private Random random;
    
    public boolean visible = true;
    
    private BufferedImage landingAreaImg;
    private BufferedImage triangleImg;
    
    public LandingArea()
    {
        initialize();
        loadContent();
    }
    
    private void initialize()
    {   
        x = (int)(GameManager.frameWidth * 0.37);
        y = (int)(GameManager.frameHeight * 0.88);
        
        speedX = 5;
        
        random = new Random();
        
        landingAreaLeftMoveBorder = (int)(GameManager.frameWidth - (GameManager.frameWidth - 10));
        landingAreaRightMoveBorder = (int)(GameManager.frameWidth - 10);
    }
    
    private void loadContent()
    {
        try
        {
            triangleImg = ImageIO.read(getClass().getResourceAsStream("/res/drawable/triangle.png"));
            landingAreaImg = ImageIO.read(getClass().getResourceAsStream("/res/drawable/landing_area.png"));
            landingAreaImgWidth = landingAreaImg.getWidth();
        }
        catch (IOException | IllegalArgumentException ex) 
        { 
            System.err.println("Ошибка считывания ресурсов посадочной платформы");
            Logger.getLogger(LandingArea.class.getName()).log(Level.SEVERE, null, ex);
            
            JOptionPane.showConfirmDialog(null, "Ошибка считывания ресурсов посадочной платформы\nПриложение будет закрыто",
                    "Ошибка", JOptionPane.PLAIN_MESSAGE);
            System.exit(1);
        }
    }
    
    public final void resetLandingArea()
    {
        if (GameManager.gameMode == GameManager.GameMode.BEER || GameManager.gameMode == GameManager.GameMode.HARD)
            x = random.nextInt((GameManager.frameWidth - landingAreaImgWidth) - 15) + 15;
    }
    
    public void update()
    {
        if (GameManager.gameMode == GameManager.GameMode.BEER || GameManager.gameMode == GameManager.GameMode.HARD)
        {
            if (x + landingAreaImgWidth < landingAreaRightMoveBorder - 5)
                x += speedX;
            else
            {
                speedX *= -1;
                x += speedX;
            } 

            if (landingAreaLeftMoveBorder +5 < x)
                x += speedX;
            else
            {
                speedX *= -1;
                x += speedX;
            }
        }
    }
    
    public void paint(Graphics2D g2d, int y)
    {
        if (visible)
            g2d.drawImage(landingAreaImg, x, y, null);
        else
            g2d.drawImage(triangleImg, 
                    x + landingAreaImgWidth / 2 - triangleImg.getWidth() / 2,
                    GameManager.frameHeight - triangleImg.getHeight() - 10, 
                    null);
    }
}