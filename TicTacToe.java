/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import static javax.swing.SwingUtilities.invokeLater;

/**
 *
 * @author Drew
 */
public class TicTacToe {

    /**
     * @param args the command line arguments
     */
    
    private static JFrame frame;
    private static int dim = 3;
    private static JMenuItem menuSize;
    private static boolean humanvhuman;
    private static boolean compFirst;
    private static TTTGame game;
    public static StatPanel stats;
    
    public static void main(String[] args) {
        
        invokeLater(() -> {
            build();
        });
    }
    
    public static void build()
    {
        game = new TTTGame(dim, humanvhuman, compFirst);
        stats = new StatPanel();
        
        frame = new JFrame("Tic Tac Toe");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
           
            @Override
            public void windowClosing(WindowEvent we) {               
              
                if (CheckSave("Exit")) {
                    frame.dispose();
                    System.exit(0);                    
                }                
            } 
        });        
        
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("File");
        JMenu menuSettings = new JMenu("Settings");
        JMenu menuHelp = new JMenu("Help");
        
        JMenuItem menuNew = new JMenuItem("New");
        menuNew.addActionListener((ActionEvent e) -> {
            
            if (CheckSave("New")) {
                resetPane();
            }            
        });        
        
        JMenuItem menuOpen = new JMenuItem("Open");
        menuOpen.addActionListener((ActionEvent e) -> {
            
            if (CheckSave("Open")) {
                OpenFile();
            }
        });       
        
        JMenuItem menuSave = new JMenuItem("Save");
        menuSave.addActionListener((ActionEvent e) -> {           
            
            SaveFile();            
        }); 
        
        JMenuItem menuClose = new JMenuItem("Close");
        menuClose.addActionListener((ActionEvent e) -> {           
                                    
            if (CheckSave("Close")) {
                frame.dispose();
                System.exit(0);
            }
        }); 
        
        JCheckBoxMenuItem menuCompFirst = new JCheckBoxMenuItem("Computer Goes First");
        menuCompFirst.addActionListener((ActionEvent e) -> {

            if (CheckSave("Computer First")) {
                compFirst = !compFirst;
                resetPane();
            }
            else {
                menuCompFirst.setSelected(compFirst);
            }            
        });       
        
        JRadioButtonMenuItem menuCompPlayer = new JRadioButtonMenuItem("Human vs. Computer");                
        JRadioButtonMenuItem menuHumanPlayer = new JRadioButtonMenuItem("Human vs. Human");
        
        menuCompPlayer.setSelected(true);
        menuCompPlayer.addActionListener((ActionEvent e) -> {
            
            if (CheckSave("Human vs. Computer")) {                
                humanvhuman = false;
                resetPane();
            }
            else {
                menuHumanPlayer.setSelected(true);
            }                        
        });

        menuHumanPlayer.addActionListener((ActionEvent e) -> {
            
            if (CheckSave("Human vs. Human")) {

                humanvhuman = true;
                compFirst = false;
                menuCompFirst.setSelected(false);
                resetPane();
            }
            else {
                menuCompPlayer.setSelected(true);
            }            
        });        
        
        menuSize = new JMenuItem("Size: " + dim + " x " + dim );       
        menuSize.addActionListener((ActionEvent e) -> {
            
            if (CheckSave("Board Size")) {
                SizePanel sPanel = new SizePanel(dim);
                dim = sPanel.showDialog();            
                resetPane();
            }
        });
        
        JMenuItem menuHelpInfo = new JMenuItem("Instructions");
        menuHelpInfo.addActionListener((ActionEvent e) -> {
           JOptionPane.showMessageDialog(null, "Try to form a row, diagonal, or column of either Xs or Os.", "Instructions", JOptionPane.INFORMATION_MESSAGE);            
        });       
        
        JMenuItem menuAbout = new JMenuItem("About");
        menuAbout.addActionListener((ActionEvent e) -> {
           JOptionPane.showMessageDialog(null, "Written by Andrew Isaac for COMP 585.", "About", JOptionPane.INFORMATION_MESSAGE);            
        });
        
        JMenuItem menuStats = new JMenuItem("Statistics");
        menuStats.addActionListener((ActionEvent e) -> {
            
            stats = new StatPanel();
            stats.showDialog();
        });
        
        menuFile.add(menuNew);
        menuFile.add(menuOpen);
        menuFile.add(menuSave);
        menuFile.add(menuClose);
        
        ButtonGroup group = new ButtonGroup();
        group.add(menuHumanPlayer);
        group.add(menuCompPlayer);
        
        menuSettings.add(menuHumanPlayer);
        menuSettings.add(menuCompPlayer);
        menuSettings.addSeparator();
        menuSettings.add(menuCompFirst);
        menuSettings.add(menuSize);
                
        menuHelp.add(menuHelpInfo);
        menuHelp.add(menuAbout);
        menuHelp.add(menuStats);
        
        menuBar.add(menuFile);
        menuBar.add(menuSettings);
        menuBar.add(menuHelp);
        
        frame.setJMenuBar(menuBar);
                       
        frame.setSize(640,730);
        frame.setMinimumSize(new Dimension(300,300));
        
        TTTPanel panel = new TTTPanel(dim, new Dimension(300,300), game);
        game.SetPanel(panel);
        frame.setContentPane(panel);
        frame.setVisible(true);
    }
    
    public static void resetPane()
    {     
        game = new TTTGame(dim, humanvhuman, compFirst);
        TTTPanel panel = new TTTPanel(dim, frame.getContentPane().getSize(), game);
        game.SetPanel(panel);
        frame.setContentPane(panel);
        menuSize.setText("Size: " + dim + " x " + dim);
        if (compFirst) {
            game.ComputersTurn();
        }
        frame.pack();
    }
    
    // Reads and parses file, sets positions on board, and starts game
    public static boolean OpenFile() {
        
        JFileChooser fc = new JFileChooser();
        int result = fc.showOpenDialog(frame);
        
        if (result == JFileChooser.CANCEL_OPTION) {
            return false;
        }
        
        File fileChosen = fc.getSelectedFile();               
        
        try {
            Scanner scan = new Scanner(fileChosen);
            
            if (scan.hasNextInt()) {
                dim = scan.nextInt();
            }
                       
            game = new TTTGame(dim, humanvhuman, compFirst);
            TTTPanel panel = new TTTPanel(dim, frame.getContentPane().getSize(), game);
            game.SetPanel(panel);
            frame.setContentPane(panel);
            menuSize.setText("Size: " + dim + " x " + dim);
                        
            if (scan.hasNext()) {
                
                String cPlayer = scan.next();
                
                if (cPlayer.equals("X")) {                
                    game.SetCurrentPlayer(TTTGame.player.X);  
                }
                else {
                    game.SetCurrentPlayer(TTTGame.player.O);                    
                }
            }            
            
            for (int i = 0; i < dim; i++) {
                for (int j = 0; j < dim; j++) {
                    if (scan.hasNext()) {
                        
                        String posVal = scan.next();
                        
                        if (posVal.equals("X")) {
                            game.positions[i][j] = TTTGame.player.X;
                            panel.buttons[i][j].setText("X");
                            panel.buttons[i][j].setEnabled(false);
                        }
                        else if (posVal.equals("O")) {
                            game.positions[i][j] = TTTGame.player.O;
                            panel.buttons[i][j].setText("O");
                            panel.buttons[i][j].setEnabled(false);
                        }
                        else if (posVal.equals("E")) {
                            game.positions[i][j] = TTTGame.player.EMPTY;                            
                        }                        
                    }
                }
            }
            
            // Open a completed game
            if (game.draw(game.positions) || game.win(game.positions)) {
                for (int i = 0; i < dim; i++) {
                    for (int j = 0; j < dim; j++) {
                        panel.buttons[i][j].setEnabled(false);
                    }
                }
            }
        }
        catch (Exception e) {
            System.out.println(e);
            return false;
        }
        
        return true;
    }
    
    // Saves file based on board positions, game settings, and game state
    public static boolean SaveFile() {
        
        JFileChooser fc = new JFileChooser();
        int result = fc.showSaveDialog(frame);
        
        if (result == JFileChooser.CANCEL_OPTION) {
            return false;
        }
        
        File fileChosen = fc.getSelectedFile();        
        
        try {
            
            PrintWriter pWriter = new PrintWriter(fileChosen);
            
            pWriter.println(dim);
            
            if (game.CurrentPlayer() == TTTGame.player.X) {
                pWriter.println("X");
            }
            else {
                pWriter.println("O");
            }                    
            
            for (int i = 0; i < dim; i++) {
                for (int j = 0; j < dim; j++) {
                    if (game.positions[i][j] == TTTGame.player.X) {
                        pWriter.println("X");
                    }
                    else if (game.positions[i][j] == TTTGame.player.O) {
                        pWriter.println("O");
                    }
                    else {
                        pWriter.println("E");
                    }
                }
            }
            
            pWriter.close();
            game.SetSaveFlag();
        }
        catch (Exception e) {
            System.out.println(e);
            return false;
        }
        
        return true;
    }
    
    public static boolean CheckSave(String title) {
        
        if (!game.GetSaveFlag()) {

            int result = JOptionPane.showConfirmDialog(null, "Do you want to save game?", title, JOptionPane.YES_NO_CANCEL_OPTION); 

            if (result == JOptionPane.YES_OPTION) {                       
                if (SaveFile()) {
                    return true;
                }
                else {
                    return false;
                }
            }

            if (result == JOptionPane.NO_OPTION) {
                return true;
            }

            else {
                return false;
            }            
        }
        
        return true;
    }
}

