package com.networking.tcp-udp-deep-dive;

import java.util.ArrayList;
import java.util.List;

public class CongestionControl {
    public enum LossType { TIMEOUT, TRIPLE_DUP_ACK }
    public enum Algorithm { RENO, CUBIC, BBR }

    public static class CongestionState {
        private double cwnd;
        private double ssthresh;
        private final int mss;
        private final Algorithm algo;
        private final List<Double> cwndHistory = new ArrayList<>();
        private double cubicWmax;
        private double cubicK;
        private long cubicLastLossTime;
        private boolean inFastRecovery;

        public CongestionState(int initialCwnd, int initialSsthresh, int mss, Algorithm algo) {
            this.cwnd = initialCwnd;
            this.ssthresh = initialSsthresh;
            this.mss = mss;
            this.algo = algo;
            this.cubicWmax = initialCwnd;
            this.cubicLastLossTime = System.nanoTime();
        }

        public double getCwnd() { return cwnd; }
        public double getSsthresh() { return ssthresh; }
        public int getMss() { return mss; }
        public Algorithm getAlgo() { return algo; }
        public List<Double> getCwndHistory() { return cwndHistory; }

        public void addHistory() { cwndHistory.add(cwnd); }

        public void onAckReceived(int ackedBytes) {
            addHistory();
            double ackedMss = ackedBytes / (double) mss;
            switch (algo) {
                case RENO -> handleRenoAck(ackedMss);
                case CUBIC -> handleCubicAck();
                case BBR -> handleBbrAck();
            }
        }

        private void handleRenoAck(double ackedMss) {
            if (cwnd < ssthresh) {
                cwnd = Math.min(cwnd + ackedMss, ssthresh);
            } else {
                cwnd += ackedMss * ackedMss / cwnd;
            }
        }

        private void handleCubicAck() {
            if (cwnd < ssthresh) {
                cwnd = Math.min(cwnd + 1, ssthresh);
                return;
            }
            double t = (System.nanoTime() - cubicLastLossTime) / 1_000_000_000.0;
            double target = cubicWmax + 0.4 * Math.pow(t - cubicK, 3);
            cwnd = Math.max(cwnd + 0.1, target);
        }

        private void handleBbrAck() {
            cwnd += 1.0;
        }

        public void onLossDetected(LossType type) {
            addHistory();
            switch (algo) {
                case RENO, CUBIC -> handleLossBased(type);
                case BBR -> handleBbrLoss(type);
            }
        }

        private void handleLossBased(LossType type) {
            if (type == LossType.TIMEOUT) {
                ssthresh = Math.max(cwnd / 2, 2);
                cwnd = 10;
                inFastRecovery = false;
            } else {
                ssthresh = Math.max(cwnd / 2, 2);
                if (algo == Algorithm.CUBIC) {
                    cubicWmax = cwnd;
                    cubicK = Math.cbrt(cubicWmax * 0.3 / 0.4);
                    cubicLastLossTime = System.nanoTime();
                }
                cwnd = ssthresh;
            }
        }

        private void handleBbrLoss(LossType type) {
            ssthresh = Math.max(cwnd * 0.7, 2);
            cwnd = ssthresh;
        }

        @Override
        public String toString() {
            return String.format("cwnd=%.1f ssthresh=%.1f [%s]", cwnd, ssthresh, algo);
        }
    }

    public static void simulate() {
        System.out.println("=== Congestion Control Simulation (Reno) ===");
        CongestionState state = new CongestionState(10, 100, 1460, Algorithm.RENO);
        for (int rtt = 1; rtt <= 20; rtt++) {
            state.onAckReceived(1460);
            System.out.printf("RTT %2d: %s%n", rtt, state);
            if (rtt == 10) {
                System.out.println(">> Loss event (triple dup ACK)");
                state.onLossDetected(LossType.TRIPLE_DUP_ACK);
            }
        }
    }

    public static void main(String[] args) {
        simulate();
    }
}
