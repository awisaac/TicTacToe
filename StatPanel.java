/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author Drew
 */
public class StatPanel extends JPanel {
    
    int wins = 0;
    int losses = 0;
    int draws = 0;
    int tries = 0;
    JDialog dialog;
    
    StatPanel() {
        
        try {
            File sFile = new File("stats.txt");
            sFile.createNewFile();        
            
            Scanner fileScanner = new Scanner(sFile);
            boolean fileError = false;
            
            if (fileScanner.hasNextInt()) {
                wins = fileScanner.nextInt();
            }
            else {
                wins = 0;
                losses = 0;
                draws = 0;
                tries = 0;
                fileError = true;
            }
            
            if (fileScanner.hasNextInt() && !fileError) {
                losses = fileScanner.nextInt();
            }
            else {
                wins = 0;
                losses = 0;
                draws = 0;
                tries = 0;
                fileError = true;
            }
            
            if (fileScanner.hasNextInt() && !fileError) {
                draws = fileScanner.nextInt();
            }
            else {
                wins = 0;
                losses = 0;
                draws = 0;
                tries = 0;
                fileError = true;
            }
            
            if (fileScanner.hasNextInt() && !fileError) {
                tries = fileScanner.nextInt();
            }
            else {
                wins = 0;
                losses = 0;
                draws = 0;
                tries = 0;
            }
            
            fileScanner.close();
        }
        catch(Exception e) {
            System.out.println(e);
        }
        
        float winPerc = 0;
        float lossPerc = 0;
        float drawPerc = 0;        
        
        if (tries > 0) {
            winPerc = (float)wins * 100 / tries;
            lossPerc = (float)losses * 100 / tries;
            drawPerc = (float)draws * 100 / tries;
        }
               
        setLayout(new GridLayout(3,2));

              
        JLabel winLabel = new JLabel("Wins: " + wins + " (" + String.format("%.2f", winPerc) + "%)");
        winLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel lossesLabel = new JLabel("Losses: " + losses + " (" + String.format("%.2f", lossPerc) + "%)");
        lossesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel drawLabel = new JLabel("Draws: " + draws + " (" + String.format("%.2f", drawPerc) + "%)");
        drawLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel triesLabel = new JLabel("Attempts: " + tries);
        triesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        add(winLabel);     
        add(lossesLabel);
        add(drawLabel);
        add(triesLabel);
        
        JButton okButton = new JButton("OK");
        okButton.addActionListener((ActionEvent e) -> {
                
            // Just close
            dialog.dispose();            
        });
        
        add(okButton);
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener((ActionEvent e) -> {
                
            // Clear stats and close
            wins = 0;
            draws = 0;
            losses = 0;
            tries = 0;           
            SaveStats();
            dialog.dispose();            
        });
        
        add(clearButton);
    }
    
    public void showDialog() {
        
        dialog = new JDialog();
        dialog.setModal(true);        
        dialog.setPreferredSize(new Dimension(250, 100));
        dialog.setResizable(false);
        dialog.setTitle("Statistics");
        dialog.setContentPane(this);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);          
    }
    
    public void SaveStats() {
        
        try {
            
            File sFile = new File("stats.txt");
            sFile.createNewFile();
            PrintWriter pWriter = new PrintWriter(sFile);
            
            pWriter.println(wins);
            pWriter.println(losses);
            pWriter.println(draws);
            pWriter.println(tries);
            
            pWriter.close();
        }
        catch (Exception e) {
            System.out.println(e);
        }       
    }
}
