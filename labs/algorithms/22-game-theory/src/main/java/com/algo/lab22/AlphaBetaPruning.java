package com.algo.lab22;

import java.util.List;
import java.util.function.Function;

/**
 * Minimax with Alpha-Beta Pruning.
 * Prunes branches that cannot affect the final decision.
 * Time: O(b^(d/2)) with perfect ordering, Space: O(d)
 */
public class AlphaBetaPruning {

    private AlphaBetaPruning() {}

    public static <S> S bestMove(S state, int depth, boolean maximizing,
                                  Function<S, List<S>> moveGen,
                                  Function<S, Double> evaluator) {
        if (depth == 0 || moveGen.apply(state).isEmpty()) return state;
        List<S> moves = moveGen.apply(state);
        S best = null;
        double bestValue = maximizing ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        for (S move : moves) {
            double value = alphaBeta(move, depth - 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, !maximizing, moveGen, evaluator);
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

    public static <S> double alphaBeta(S state, int depth, double alpha, double beta,
                                        boolean maximizing,
                                        Function<S, List<S>> moveGen,
                                        Function<S, Double> evaluator) {
        if (depth == 0 || moveGen.apply(state).isEmpty()) {
            return evaluator.apply(state);
        }
        List<S> moves = moveGen.apply(state);
        if (maximizing) {
            double value = Double.NEGATIVE_INFINITY;
            for (S move : moves) {
                value = Math.max(value, alphaBeta(move, depth - 1, alpha, beta, false, moveGen, evaluator));
                alpha = Math.max(alpha, value);
                if (alpha >= beta) break;
            }
            return value;
        } else {
            double value = Double.POSITIVE_INFINITY;
            for (S move : moves) {
                value = Math.min(value, alphaBeta(move, depth - 1, alpha, beta, true, moveGen, evaluator));
                beta = Math.min(beta, value);
                if (alpha >= beta) break;
            }
            return value;
        }
    }
}
