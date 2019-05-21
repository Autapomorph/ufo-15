package ufo_lander;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;

public abstract class Canvas extends JPanel implements KeyListener, MouseListener
{
    private static boolean[] keyboardState;
    private static boolean[] mouseState;
    
    public Canvas() 
    {
        keyboardState = new boolean[525];
        mouseState = new boolean[3];
                
        setDoubleBuffered(true);
        setFocusable(true);
        setBackground(Color.BLACK);
        setListeners();
    }
    
    public abstract void paint(Graphics2D g2d);  
    
    private void setListeners()
    {
        addKeyListener(this);
        addMouseListener(this);
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g;        
        super.paintComponent(g2d);        
        paint(g2d);
    }
    
    public static boolean keyboardKeyState(int key)
    {
        return keyboardState[key];
    }
    
    @Override
    public void keyPressed(KeyEvent e) 
    {
        keyboardState[e.getKeyCode()] = true;
    }
    
    @Override
    public void keyReleased(KeyEvent e)
    {
        keyboardState[e.getKeyCode()] = false;
        keyReleasedFramework(e);
    }
    
    @Override
    public void keyTyped(KeyEvent e) { }
    
    public abstract void keyReleasedFramework(KeyEvent e);
        
    
    
    public static boolean mouseButtonState(int button)
    {
        return mouseState[button - 1];
    }
    
    private void mouseKeyStatus(MouseEvent e, boolean status)
    {
        if(e.getButton() == MouseEvent.BUTTON1)
            mouseState[0] = status;
        else if(e.getButton() == MouseEvent.BUTTON2)
            mouseState[1] = status;
        else if(e.getButton() == MouseEvent.BUTTON3)
            mouseState[2] = status;
    }
    
    @Override
    public void mousePressed(MouseEvent e)
    {
        mouseKeyStatus(e, true);
    }
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
        mouseKeyStatus(e, false);
    }
    
    @Override
    public void mouseClicked(MouseEvent e) { }
    @Override
    public void mouseEntered(MouseEvent e) { }
    @Override
    public void mouseExited(MouseEvent e) { }
}