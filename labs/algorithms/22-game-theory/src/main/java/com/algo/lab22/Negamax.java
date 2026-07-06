package com.algo.lab22;

import java.util.List;
import java.util.function.Function;

/**
 * Negamax variant of minimax.
 * Simplifies implementation using the negamax identity: max(a,b) = -min(-a,-b).
 * Time: O(b^d), Space: O(d)
 */
public class Negamax {

    private Negamax() {}

    public static <S> S bestMove(S state, int depth, int color,
                                  Function<S, List<S>> moveGen,
                                  Function<S, Double> evaluator) {
        if (depth == 0 || moveGen.apply(state).isEmpty()) return state;
        List<S> moves = moveGen.apply(state);
        S best = null;
        double bestValue = Double.NEGATIVE_INFINITY;
        for (S move : moves) {
            double value = negamax(move, depth - 1, -color, moveGen, evaluator);
            value = -value;
            if (value > bestValue) {
                bestValue = value;
                best = move;
            }
        }
        return best;
    }

    public static <S> double negamax(S state, int depth, int color,
                                      Function<S, List<S>> moveGen,
                                      Function<S, Double> evaluator) {
        if (depth == 0 || moveGen.apply(state).isEmpty()) {
            return color * evaluator.apply(state);
        }
        double value = Double.NEGATIVE_INFINITY;
        for (S move : moveGen.apply(state)) {
            double score = -negamax(move, depth - 1, -color, moveGen, evaluator);
            value = Math.max(value, score);
        }
        return value;
    }
}
