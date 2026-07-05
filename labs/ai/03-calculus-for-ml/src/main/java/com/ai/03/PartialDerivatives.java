package com.ai03;

public class PartialDerivatives {

    public static double partialDerivative(
            java.util.function.Function<double[], Double> f, double[] x, int wrt, double h) {
        double[] xPlus = x.clone();
        double[] xMinus = x.clone();
        xPlus[wrt] += h;
        xMinus[wrt] -= h;
        return (f.apply(xPlus) - f.apply(xMinus)) / (2 * h);
    }

    public static double[] gradient(
            java.util.function.Function<double[], Double> f, double[] x, double h) {
        double[] grad = new double[x.length];
        for (int i = 0; i < x.length; i++)
            grad[i] = partialDerivative(f, x, i, h);
        return grad;
    }

    public static double[][] hessian(
            java.util.function.Function<double[], Double> f, double[] x, double h) {
        int n = x.length;
        double[][] H = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double[] xpp = x.clone(); xpp[i] += h; xpp[j] += h;
                double[] xpm = x.clone(); xpm[i] += h; xpm[j] -= h;
                double[] xmp = x.clone(); xmp[i] -= h; xmp[j] += h;
                double[] xmm = x.clone(); xmm[i] -= h; xmm[j] -= h;
                H[i][j] = (f.apply(xpp) - f.apply(xpm) - f.apply(xmp) + f.apply(xmm)) / (4 * h * h);
            }
        }
        return H;
    }

    public static void main(String[] args) {
        System.out.println("=== Partial Derivatives & Hessian Demo ===");
        java.util.function.Function<double[], Double> f = v -> v[0] * v[0] + 3 * v[0] * v[1] + v[1] * v[1];
        double[] x = {1.0, 2.0};
        double[] grad = gradient(f, x, 1e-5);
        System.out.println("f(x,y)=x^2+3xy+y^2 at (1,2)");
        System.out.println("Gradient: [" + grad[0] + ", " + grad[1] + "]");
        double[][] H = hessian(f, x, 1e-5);
        System.out.println("Hessian:");
        for (double[] row : H)
            System.out.println("  [" + row[0] + ", " + row[1] + "]");
    }
}
