package com.dsacademy.lab32.dlx;

public interface ExactCoverProblem {

    boolean[][] getExactCoverMatrix();

    int[] getRowCounts();

    String describeSolution(int[] rows);
}
