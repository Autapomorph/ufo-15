package ufo_lander;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Window extends JFrame
{
    private Window()
    {
        setTitle("UFO Lander");
        setSize(800, 600);
        setResizable(false);
        setLocationRelativeTo(null);

        try 
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } 
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) 
        {
            Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(new GameManager());
        setVisible(true);
    }
    
    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(new Runnable() 
        {
            @Override
            public void run() 
            {
                new Window();
            }
        });
    }  
}