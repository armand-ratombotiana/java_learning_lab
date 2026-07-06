package com.algo.lab22;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tic-Tac-Toe game with AI using minimax/alpha-beta.
 * Board is 3x3; 'X' is maximizing, 'O' is minimizing.
 */
public class TicTacToe {

    private static final int SIZE = 3;
    private final char[][] board;
    private char currentPlayer;

    public TicTacToe() {
        board = new char[SIZE][SIZE];
        for (char[] row : board) Arrays.fill(row, ' ');
        currentPlayer = 'X';
    }

    public TicTacToe(TicTacToe other) {
        this.board = new char[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(other.board[i], 0, this.board[i], 0, SIZE);
        }
        this.currentPlayer = other.currentPlayer;
    }

    public List<int[]> getAvailableMoves() {
        List<int[]> moves = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == ' ') moves.add(new int[]{i, j});
            }
        }
        return moves;
    }

    public boolean makeMove(int row, int col) {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE || board[row][col] != ' ') return false;
        board[row][col] = currentPlayer;
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
        return true;
    }

    public char checkWinner() {
        for (int i = 0; i < SIZE; i++) {
            if (board[i][0] != ' ' && board[i][0] == board[i][1] && board[i][1] == board[i][2]) return board[i][0];
            if (board[0][i] != ' ' && board[0][i] == board[1][i] && board[1][i] == board[2][i]) return board[0][i];
        }
        if (board[0][0] != ' ' && board[0][0] == board[1][1] && board[1][1] == board[2][2]) return board[0][0];
        if (board[0][2] != ' ' && board[0][2] == board[1][1] && board[1][1] == board[2][0]) return board[0][2];
        return ' ';
    }

    public boolean isDraw() {
        if (checkWinner() != ' ') return false;
        for (char[] row : board) {
            for (char c : row) {
                if (c == ' ') return false;
            }
        }
        return true;
    }

    public boolean isGameOver() {
        return checkWinner() != ' ' || isDraw();
    }

    public double evaluate() {
        char winner = checkWinner();
        if (winner == 'X') return 10;
        if (winner == 'O') return -10;
        return 0;
    }

    public List<TicTacToe> generateMoves() {
        List<TicTacToe> moves = new ArrayList<>();
        for (int[] m : getAvailableMoves()) {
            TicTacToe next = new TicTacToe(this);
            next.makeMove(m[0], m[1]);
            moves.add(next);
        }
        return moves;
    }

    public void aiMove() {
        if (isGameOver()) return;
        TicTacToe best = AlphaBetaPruning.bestMove(this, 9, currentPlayer == 'X',
            TicTacToe::generateMoves, TicTacToe::evaluate);
        if (best != null) {
            for (int i = 0; i < SIZE; i++) {
                System.arraycopy(best.board[i], 0, this.board[i], 0, SIZE);
            }
            this.currentPlayer = best.currentPlayer;
        }
    }

    public char[][] getBoard() {
        char[][] copy = new char[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, SIZE);
        }
        return copy;
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (char[] row : board) {
            sb.append('|');
            for (char c : row) sb.append(c).append('|');
            sb.append('\n');
        }
        return sb.toString();
    }
}
