/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import javax.swing.JPanel;

/**
 *
 * @author Drew
 */
class TTTPanel extends JPanel {
    
    int dim;
    TTTButton[][] buttons;
    
    public TTTPanel(int d, Dimension size, TTTGame game)
    {
        dim = d;        
        buttons = new TTTButton[dim][dim];       
        LayoutManager layout = new GridLayout(dim,dim);
        setLayout(layout);
   
        for (int i = 0; i < dim; i++){
            for (int j = 0; j < dim; j++)
            {
                buttons[i][j] = new TTTButton(i, j, game);                
                add(buttons[i][j]);             
            }
        }
        
        setPreferredSize(size);
    }
    
    public TTTButton[][] GetButtons()
    {
        return buttons;
    }    
}
