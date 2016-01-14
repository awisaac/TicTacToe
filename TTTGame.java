/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 *
 * @author Drew
 */
class TTTGame {
    
    private boolean humanvhuman;
    private boolean gameStarted;
    private boolean compFirst;
    private player currentPlayer;
    private boolean gameOver;
    private boolean saved;
    private int size;
    private TTTPanel panel;
    private TTTButton[][] buttons;
    public player[][] positions;
    private enum outcome { WIN, LOSS, DRAW };
    public enum player { EMPTY, X, O, };
    
    public TTTGame(int dim, boolean hvh, boolean first)
    {
        currentPlayer = player.X;
        gameStarted = false;
        gameOver = false;
        saved = true;
        compFirst = first;
        humanvhuman = hvh;
        size = dim;
        positions = new player[dim][dim];
        
        for (int i = 0; i < size; i++ ) {
            for (int j = 0; j < size; j++) {
                positions[i][j] = player.EMPTY;
            }
        }        
    }
    
    public boolean IsGameStarted()
    {
        return gameStarted;
    }
    
    public boolean IsGameOver() {
        return gameOver;
    }
    
    // Current player to make move
    public player CurrentPlayer()
    {        
        return currentPlayer;           
    }
    
    public void SetCurrentPlayer(player val) {
        currentPlayer = val;
    }
    
    // Matches front end to back end
    public void SetPanel(TTTPanel pane)
    {
        panel = pane;
        buttons = panel.GetButtons();
    }
    
    public void SetSaveFlag() {
        saved = true;
    }
    
    public boolean GetSaveFlag() {
        return saved;
    }
    
    // Updates back end with front end action
    public void ButtonChosen(TTTButton button)
    {
        if (currentPlayer == player.X) {
            positions[button.GetX()][button.GetY()] = player.X;
        }
        
        else
        {
            positions[button.GetX()][button.GetY()] = player.O;
        }
        
        if (!gameStarted) {
            gameStarted = true;
            TicTacToe.stats.tries++;
            TicTacToe.stats.SaveStats();
        }
        
        gameOver = CheckEndGame();
        saved = gameOver;
        
        if (currentPlayer == player.X) {
            currentPlayer = player.O;
        }
        else {
            currentPlayer = player.X;
        }
                
        // computer's turn
        if (!humanvhuman && !gameOver)
        {            
            ComputersTurn();             
        }        
    }
    
    public void ComputersTurn() {
        
        // computer player does it stuff in a separate thread        
        Thread t1 = new Thread() {
            public void run() {
                ComputerMove(System.currentTimeMillis() + 100);
            }  
        };
        
        // Makes button non-interactable during computer's turn
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (buttons[i][j].getActionListeners().length > 0) {
                    buttons[i][j].removeActionListener(buttons[i][j].getActionListeners()[0]);
                }
            }
        }
        
        Timer resetButtons = new Timer(110, (ActionEvent e) -> {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (positions[i][j] == player.EMPTY) {
                        buttons[i][j].addActionListener( (ActionEvent ev) -> {
                            ((TTTButton)ev.getSource()).ChooseButton();
                            ButtonChosen((TTTButton)ev.getSource());
                        });                              
                    }
                }
            }
        });
        
        resetButtons.setRepeats(false);
        resetButtons.start(); 
        t1.start();       
    }           
    
    public boolean CheckEndGame() {        
        
        if (win(positions)) {
            
            String playerString;
            
            if (currentPlayer == player.X) {
                playerString = "X";
            }
            else {
                playerString = "O";
            }
            
            int result;            
            result = JOptionPane.showConfirmDialog(null, playerString + " wins! Do you want to start new game?", "Win!", JOptionPane.YES_NO_OPTION);
            
            if (!humanvhuman && !compFirst && currentPlayer == player.X) {
                TicTacToe.stats.wins++;
                TicTacToe.stats.SaveStats();
            }
            
            else if (!humanvhuman && !compFirst && currentPlayer == player.O) {
                TicTacToe.stats.losses++;
                TicTacToe.stats.SaveStats();
            }
            
            else if (!humanvhuman && compFirst && currentPlayer == player.O) {
                TicTacToe.stats.wins++;
                TicTacToe.stats.SaveStats();
            }
            
            else if (!humanvhuman && compFirst & currentPlayer == player.X) {
                TicTacToe.stats.losses++;
                TicTacToe.stats.SaveStats();
            }
            
            if (result == JOptionPane.YES_OPTION) {
                TicTacToe.resetPane();
            }
            
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    buttons[i][j].setEnabled(false);                    
                }
            }
            
            return true;
        }
        
        if (draw(positions)) {

            int result;
            
            result = JOptionPane.showConfirmDialog(null, "Game drawn. Do you want to start new game?", "Draw", JOptionPane.YES_NO_OPTION);
            
            if (!humanvhuman) {
                TicTacToe.stats.draws++;
                TicTacToe.stats.SaveStats();
            }            
            
            if (result == JOptionPane.YES_OPTION) {
                TicTacToe.resetPane();
            }
            
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    buttons[i][j].setEnabled(false);                    
                }
            }
            
            return true;
        }
        
        return false;
    }    
    
    // Builds MCTS and selects best move
    private void ComputerMove(long time)
    {
        Node root = new Node(-1,-1);       
        
        // find child with best record by building MCTS
        Node bestNode = SearchTree(root, positions, time);        
        
        if (currentPlayer == player.X) {
            positions[bestNode.X][bestNode.Y] = player.X;
        }
        else {
            positions[bestNode.X][bestNode.Y] = player.O;
        }
        
        if (!gameStarted) {
            gameStarted = true;
            TicTacToe.stats.tries++;
            TicTacToe.stats.SaveStats();
        }
        
        buttons[bestNode.X][bestNode.Y].ChooseButton();
        gameOver = CheckEndGame();
        saved = gameOver;
        
        if (currentPlayer == player.X) {
            currentPlayer = player.O;
        }
        else {
            currentPlayer = player.X;
        }       
    }
    
    // Search for best node to play
    private Node SearchTree(Node n, player[][] board, long time) {
 
        Random randGen = new Random(System.currentTimeMillis());        
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == player.EMPTY) {
                    
                    Node child = new Node(i, j);                                  
                    n.children.add(child);
                    child.parent = n;                                     
                }
            }
        }      
                
        while (System.currentTimeMillis() < time) {
        
            player[][] copyboard = new player[size][size];
            
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    copyboard[i][j] = board[i][j];
                }
            }            
            
            Node randChild = n.children.get(randGen.nextInt(n.children.size()));
            copyboard[randChild.X][randChild.Y] = currentPlayer;
            Simulate(randChild, copyboard, currentPlayer, randGen);           
        }
        
        // Return best performing       
        Node best = n.children.get(0);
                                   
        for (int i = 1; i < n.children.size(); i++) {       
                   
            Node test = n.children.get(i);
            
            if (best.GetScore() < test.GetScore()) {
               best = test;
            }
        }      
               
        return best;
    }
    
    public void Simulate(Node expanded, player[][] copyboard, player cPlayer, Random randGen) {
        
        ArrayList<Pair> empties = new ArrayList<Pair>();
        boolean win = win(copyboard) && cPlayer == currentPlayer;
        boolean loss = win(copyboard) && cPlayer != currentPlayer;
        boolean draw = draw(copyboard);
                               
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (copyboard[i][j] == player.EMPTY) {
                    empties.add(new Pair(i,j));
                }
             }
        }
        
        // Score the expanded node if a win or the first child if a loss        
        while (!win && !draw && !loss) {            
            
            int rand = randGen.nextInt(empties.size());
            Pair next = empties.get(rand);            
            
            // next player's turn
            if (cPlayer == player.X) {
                cPlayer = player.O;
            }
            else {
                cPlayer = player.X;
            }            
            
            if (cPlayer == player.X) {
                copyboard[next.x][next.y] = player.X;
            }
            else {
                copyboard[next.x][next.y] = player.O;
            }           
            
            empties.remove(next);
            
            win = win(copyboard) && cPlayer == currentPlayer;
            loss = win(copyboard) && cPlayer != currentPlayer;
            draw = draw(copyboard);           
        }
               
        // either a win from expanded or its a draw
        if (win) {
            expanded.IncrementScore(empties.size());
        }
        
        else if (loss) {
            expanded.DecrementScore(empties.size());
        }
        
        else if (draw) {
            expanded.IncrementTries();
        }        
        
    }    
        
    public boolean win(player[][] board) {
        
        for (int i = 0; i < size; i++) {
            
            // winning row
            int j = 0;           
            while (board[i][j] != player.EMPTY && board[i][j] == board[i][j + 1]) {
                
                j++;                
                if (j == size - 1) {
                    return true;
                }
            }        
            
            // winning col
            j = 0;            
            while (board[j][i] != player.EMPTY && board[j][i] == board[j + 1][i]) {
                
                j++;                
                if (j == size - 1) {
                    return true;
                }
            }            
        }
        
        // winning diagonals
        int i = 0;
        while (board[i][i] != player.EMPTY && board[i][i] == board[i + 1][i + 1]) {
            i++;
            if (i == size - 1) {
                return true;
            }
        }
        
        i = 0;
        while (board[i][size - i - 1] != player.EMPTY && board[i][size - i - 1] == board[i + 1][size - i - 2]) {
            i++;
            if (i == size - 1) {
                return true;
            }
        }
        
        return false;       
    }
    
    // Declares draw if it would be impossible for either player to win
    public boolean draw(player[][] board) {
         
        boolean boardFilled = true;
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == player.EMPTY) {
                    boardFilled = false;
                }
            }
        }
        
        if (boardFilled) {
            return true;
        }
        
        for (int i = 0; i < size; i++) {
            
            // Check each row
            int j = 0;
            while (j < size && board[i][j] == player.EMPTY) {
                j++;
            }
            
            // Empty row - could still be a win
            if (j == size) {
                return false;
            }
            
            int temp = j;            
            if (board[i][temp] == player.X) {
                while (temp < size && board[i][temp] != player.O) {
                    temp++;
                }
                                
                // Row exists with Xs and no Os - could still be a win
                if (temp == size) {
                    return false;
                }
            }                              
            
            temp = j;            
            if (board[i][temp] == player.O) {
                while (temp < size && board[i][temp] != player.X) {
                    temp++;
                }

                // Row exists with Os and no Xs - could still be a win
               if (temp == size) {
                   return false;
               }
            }

            // Check each col
            j = 0;
            while (j < size && board[j][i] == player.EMPTY) {
                j++;
            }
            
            // Empty col - could still be a win
            if (j == size) {
                return false;
            }
            
            temp = j;
            if (board[temp][i] == player.X) {
                while (temp < size && board[temp][i] != player.O) {
                    temp++;
                }
                
                // Col exists with Xs and no Os - could still be a win
                if (temp == size) {
                    return false;
                }
            }
            
            temp = j;           
            if (board[temp][i] == player.O) {
                while (temp < size && board[temp][i] != player.X) {
                    temp++;
                }
                
                // Cols exists with Os and no Xs - could still be a win
                if (temp == size) {
                    return false;
                }              
            }            
        }
        
        // winning diagonals
        int i = 0;
        while (i < size && board[i][i] == player.EMPTY) {
            i++;
        }
        
        // empty diagonal
        if (i == size) {
            return false;
        }
        
        int temp = i;        
        if (board[temp][temp] == player.X) {
            while (temp < size && board[temp][temp] != player.O) {
                temp++;
            }
                        
            // diagonal with just Xs and empties, could still be a win
            if (temp == size) {
                return false;
            }
        }
        
        temp = i;
        if (board[temp][temp] == player.O) {
            while (temp < size && board[temp][temp] != player.X) {
                temp++;
            }
        }
        
        // diagonal with just Os and empties, could still be a win
        if (temp == size) {
            return false;
        }        
        
        i = 0;
        while (i < size && board[i][size - i - 1] == player.EMPTY) {
            i++;
        }
        
        if (i == size) {
            return false;
        }
        
        temp = i;
        if (board[temp][size - temp - 1] == player.X) {
            while (temp < size && board[temp][size - temp - 1] != player.O) {
                temp++;
            }            
        }
        
        if (temp == size) {
            return false;
        }
        
        temp = i;
        if (board[temp][size - temp - 1] == player.O) {
            while (temp < size && board[temp][size - temp - 1] != player.X) {
                temp++;
            }            
        }
                
        if (temp == size) {
            return false;
        }         
        
        return true;
    } 
}

class Node {
    
    private float tries;  
    private float wins;
    public int X;
    public int Y;
    public ArrayList<Node> children;
    public Node parent;
    
    public Node(int xPos, int yPos)
    {  
        X = xPos;
        Y = yPos;        
        wins = 0;
        tries = 0;             
        children = new ArrayList<>();
    }
    
    // Gets set if node is expanded and results in a win
    public void IncrementScore(int immediacy) {
                               
            wins += immediacy;
            tries++;
    }
    
    public void DecrementScore(int immediacy) {
    
            wins -= immediacy;
            tries++;
    }
    
    public void IncrementTries() {
        tries++;
    }
   
    // Inherits either max / min child
    public float GetScore() {        
        return wins / tries;
    }
}

class Pair {
    
    public int x, y;
    
    public Pair(int a, int b) {
        x = a;
        y = b;
    }    
}

