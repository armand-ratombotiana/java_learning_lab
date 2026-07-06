package com.algo.lab22;

import java.util.List;
import java.util.function.Function;

/**
 * Generic Minimax algorithm for perfect-information games.
 * Evaluates game states assuming optimal play from both players.
 * Time: O(b^d), Space: O(d) where b = branching factor, d = depth
 */
public class Minimax {

    private Minimax() {}

    public static <S> S bestMove(S state, int depth, boolean maximizing,
                                  Function<S, List<S>> moveGen,
                                  Function<S, Double> evaluator) {
        if (depth == 0 || moveGen.apply(state).isEmpty()) return state;
        List<S> moves = moveGen.apply(state);
        S best = null;
        double bestValue = maximizing ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        for (S move : moves) {
            double value = minimax(move, depth - 1, !maximizing, moveGen, evaluator);
            if (maximizing && value > bestValue) {
                bestValue = value;
                best = move;
            } else if (!maximizing && value < bestValue) {
                bestValue = value;
                best = move;
            }
        }
        return best;
    }

    public static <S> double minimax(S state, int depth, boolean maximizing,
                                      Function<S, List<S>> moveGen,
                                      Function<S, Double> evaluator) {
        if (depth == 0 || moveGen.apply(state).isEmpty()) {
            return evaluator.apply(state);
        }
        List<S> moves = moveGen.apply(state);
        if (maximizing) {
            double value = Double.NEGATIVE_INFINITY;
            for (S move : moves) {
                value = Math.max(value, minimax(move, depth - 1, false, moveGen, evaluator));
            }
            return value;
        } else {
            double value = Double.POSITIVE_INFINITY;
            for (S move : moves) {
                value = Math.min(value, minimax(move, depth - 1, true, moveGen, evaluator));
            }
            return value;
        }
    }
}
