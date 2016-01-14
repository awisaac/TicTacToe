/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.AbstractAction;
import javax.swing.JButton;

/**
 *
 * @author Drew
 * TTT Button is JButton that also holds its position on the board
 */
class TTTButton extends JButton {
    
    int x;
    int y;
    TTTGame game;
    
    TTTButton(int i, int j, TTTGame gam)
    {
        x = i;
        y = j;
        game = gam;
        setFont(getFont().deriveFont((float)(getWidth())));

        addComponentListener(new ComponentListener() {
            
            public void componentResized(ComponentEvent e)
            {
                setFont(getFont().deriveFont(Math.min((float)(getWidth() / 2), (float)(getHeight() / 2))));
            }
            
            public void componentHidden(ComponentEvent e){};
            public void componentShown(ComponentEvent e){};
            public void componentMoved(ComponentEvent e){};         
            
        });
        
        addActionListener( (ActionEvent e) -> {
            ChooseButton();
            game.ButtonChosen((TTTButton)e.getSource());  
        });
    }
    
    public void ChooseButton()
    {
        if (game.CurrentPlayer() == TTTGame.player.X)
        {
            setText("X");
        }
        else 
        {
            setText("O");
        }

        setEnabled(false);
    }
    
    public int GetX()
    {
        return x;
    }
    
    public int GetY()
    {
        return y;
    }
}

