/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 *
 * @author Drew
 */
class SizePanel extends JPanel{    
   
    int choice;
    JDialog dialog;
    
    SizePanel(int dim)
    {       
        choice = dim;
        dialog = new JDialog();
        setLayout(new GridLayout(5,2));
        JRadioButton[] buttons = new JRadioButton[8];
        ButtonGroup sizeGroup = new ButtonGroup();
        
        for (int i = 3; i <= 10; i++)
        {
            buttons[i - 3] = new JRadioButton(i + " x " + i);
            add(buttons[i - 3]);
            sizeGroup.add(buttons[i - 3]);
        }
        
        buttons[dim - 3].setSelected(true);
        
        JButton okButton = new JButton("OK");
        add(okButton);
        okButton.addActionListener((ActionEvent e) -> {
            // find selected radio button and set value and close            
            for (int i = 3; i <= 10; i++)
            {
                if (buttons[i - 3].isSelected())
                {
                    choice = i;
                }
            }
            
            dialog.dispose();            
        });
        
        JButton cancelButton = new JButton("Cancel");
        add(cancelButton);
        cancelButton.addActionListener((ActionEvent e) -> {            
            dialog.dispose();
        });
    }
    
    public int showDialog() {
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setPreferredSize(new Dimension(170,150));
        dialog.setResizable(false);
        dialog.setModal(true);
        dialog.setTitle("Choose Size");
        dialog.setContentPane(this);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        
        return choice;        
    }  
}
