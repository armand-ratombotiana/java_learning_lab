package com.algo.lab22;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameTheoryTest {

    @Test
    void testTicTacToeInitialBoard() {
        TicTacToe game = new TicTacToe();
        assertEquals(9, game.getAvailableMoves().size());
        assertFalse(game.isGameOver());
    }

    @Test
    void testTicTacToeWinner() {
        TicTacToe game = new TicTacToe();
        game.makeMove(0, 0);
        game.makeMove(1, 0);
        game.makeMove(0, 1);
        game.makeMove(1, 1);
        game.makeMove(0, 2);
        assertEquals('X', game.checkWinner());
        assertTrue(game.isGameOver());
    }

    @Test
    void testTicTacToeDraw() {
        TicTacToe game = new TicTacToe();
        int[][] moves = {{0,0},{0,1},{0,2},{1,0},{1,2},{1,1},{2,0},{2,2},{2,1}};
        for (int[] m : moves) game.makeMove(m[0], m[1]);
        assertTrue(game.isDraw());
    }

    @Test
    void testTicTacToeInvalidMove() {
        TicTacToe game = new TicTacToe();
        game.makeMove(0, 0);
        assertFalse(game.makeMove(0, 0));
    }

    @Test
    void testTicTacToeAIMove() {
        TicTacToe game = new TicTacToe();
        game.makeMove(0, 0);
        game.makeMove(1, 1);
        game.aiMove();
        assertFalse(game.isGameOver());
    }

    @Test
    void testNimWinningPosition() {
        assertTrue(NimSolver.isWinningPosition(new int[]{1, 2, 3}));
        assertFalse(NimSolver.isWinningPosition(new int[]{0, 0, 0}));
        assertFalse(NimSolver.isWinningPosition(new int[]{3, 5, 6}));
    }

    @Test
    void testNimFindWinningMove() {
        int[] heaps = {1, 2, 3};
        int[] move = NimSolver.findWinningMove(heaps);
        assertNotNull(move);
        int[] result = NimSolver.makeMove(heaps, move[0], move[1]);
        assertFalse(NimSolver.isWinningPosition(result));
    }

    @Test
    void testNimLosingPosition() {
        assertNull(NimSolver.findWinningMove(new int[]{3, 5, 6}));
    }

    @Test
    void testNimOptimalMove() {
        int[] move = NimSolver.optimalMove(new int[]{0, 0, 5});
        assertNotNull(move);
        assertEquals(2, move[0]);
        assertTrue(move[1] > 0);
    }

    @Test
    void testNimEmptyHeaps() {
        assertNull(NimSolver.optimalMove(new int[]{0, 0, 0}));
    }

    @Test
    void testMinimaxBasic() {
        boolean result = Minimax.minimax(0, 3, true,
            n -> n < 3 ? List.of(n + 1, n + 2) : List.of(),
            n -> n >= 3 ? 1.0 : 0.0) > 0;
        assertTrue(result);
    }

    @Test
    void testAlphaBetaBasic() {
        boolean result = AlphaBetaPruning.alphaBeta(0, 3, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, true,
            n -> n < 3 ? List.of(n + 1, n + 2) : List.of(),
            n -> n >= 3 ? 1.0 : 0.0) > 0;
        assertTrue(result);
    }

    @Test
    void testNegamaxBasic() {
        boolean result = Negamax.negamax(0, 3, 1,
            n -> n < 3 ? List.of(n + 1, n + 2) : List.of(),
            n -> n >= 3 ? 1.0 : 0.0) > 0;
        assertTrue(result);
    }
}
