package com.algo.lab31;

public class SkiRental {

    public static int deterministic(int buyCost, int totalDays) {
        int rented = 0;
        int day;
        for (day = 1; day < buyCost && day <= totalDays; day++) {
            rented++;
        }
        if (day <= totalDays) {
            rented += buyCost;
        }
        return rented;
    }

    public static double optimalOffline(int buyCost, int totalDays) {
        return Math.min(totalDays, buyCost);
    }

    public static double competitiveRatio(int buyCost) {
        int worstDays = buyCost;
        double algo = deterministic(buyCost, worstDays);
        double opt = optimalOffline(buyCost, worstDays);
        return algo / opt;
    }
}
