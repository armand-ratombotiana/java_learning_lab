# Code Deep Dive: Algebra Implementations in Java

## 1. Linear Equation Solver

```java
package com.mathacademy.algebra;

public class LinearEquationSolver {
    
    public static double solveLinear(double a, double b) {
        if (a == 0) {
            if (b == 0) {
                throw new ArithmeticException("Infinite solutions");
            }
            throw new ArithmeticException("No solution");
        }
        return -b / a;
    }
    
    public static double[] solveLinearSystem(double a1, double b1, double c1, 
                                              double a2, double b2, double c2) {
        double det = a1 * b2 - a2 * b1;
        
        if (det == 0) {
            throw new ArithmeticException("No unique solution");
        }
        
        double x = (c1 * b2 - c2 * b1) / det;
        double y = (a1 * c2 - a2 * c1) / det;
        
        return new double[]{x, y};
    }
    
    public static double[][] solveCramer(double[][] coefficients, double[] constants) {
        int n = constants.length;
        double detMain = determinant(coefficients);
        
        if (detMain == 0) {
            throw new ArithmeticException("No unique solution");
        }
        
        double[] solution = new double[n];
        
        for (int i = 0; i < n; i++) {
            double[][] modified = copyMatrix(coefficients);
            for (int j = 0; j < n; j++) {
                modified[j][i] = constants[j];
            }
            solution[i] = determinant(modified) / detMain;
        }
        
        return new double[][]{solution};
    }
    
    private static double[][] copyMatrix(double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[][] copy = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(matrix[i], 0, copy[i], 0, cols);
        }
        return copy;
    }
    
    public static double determinant(double[][] matrix) {
        int n = matrix.length;
        
        if (n == 1) return matrix[0][0];
        if (n == 2) return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        
        double det = 0;
        for (int j = 0; j < n; j++) {
            double[][] sub = getSubmatrix(matrix, 0, j);
            det += Math.pow(-1, j) * matrix[0][j] * determinant(sub);
        }
        return det;
    }
    
    private static double[][] getSubmatrix(double[][] matrix, int row, int col) {
        int n = matrix.length;
        double[][] sub = new double[n-1][n-1];
        
        for (int i = 0, ii = 0; i < n; i++) {
            if (i == row) continue;
            for (int j = 0, jj = 0; j < n; j++) {
                if (j == col) continue;
                sub[ii][jj++] = matrix[i][j];
            }
            ii++;
        }
        return sub;
    }
}
```

## 2. Quadratic Equation Solver

```java
package com.mathacademy.algebra;

public class QuadraticSolver {
    
    public static class QuadraticResult {
        public int numRoots;
        public double[] roots;
        public double discriminant;
        
        public QuadraticResult(int numRoots, double[] roots, double discriminant) {
            this.numRoots = numRoots;
            this.roots = roots;
            this.discriminant = discriminant;
        }
    }
    
    public static QuadraticResult solve(double a, double b, double c) {
        if (a == 0) {
            if (b == 0) {
                throw new ArithmeticException("Not an equation");
            }
            return new QuadraticResult(1, new double[]{-c/b}, 0);
        }
        
        double discriminant = b * b - 4 * a * c;
        
        if (discriminant > 0) {
            double sqrtD = Math.sqrt(discriminant);
            return new QuadraticResult(2, new double[]{
                (-b + sqrtD) / (2 * a),
                (-b - sqrtD) / (2 * a)
            }, discriminant);
        } else if (discriminant == 0) {
            return new QuadraticResult(1, new double[]{-b / (2 * a)}, 0);
        } else {
            return new QuadraticResult(0, new double[]{}, discriminant);
        }
    }
    
    public static double[] solveUsingFormula(double a, double b, double c) {
        QuadraticResult result = solve(a, b, c);
        return result.roots;
    }
    
    public static double[] solveByFactoring(int a, int b, int c) {
        if (a == 0) {
            return new double[]{-c / (double) b};
        }
        
        for (int p = -Math.abs(c); p <= Math.abs(c); p++) {
            if (c % p == 0) {
                for (int q = -Math.abs(a); q <= Math.abs(a); q++) {
                    if (a % q == 0) {
                        int r = a / q;
                        if (p * r + q * (c / p) == b) {
                            return new double[]{-p * 1.0 / q, -(c / p) * 1.0 / r};
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public static double[] completeSquare(double a, double b, double c) {
        if (a == 0) {
            return new double[]{-c/b};
        }
        
        double h = -b / (2 * a);
        double k = (4 * a * c - b * b) / (4 * a);
        
        return new double[]{h, k};
    }
    
    public static String toVertexForm(double a, double b, double c) {
        double[] vertex = completeSquare(a, b, c);
        return String.format("y = %g(x - %g)² + %g", a, vertex[0], vertex[1]);
    }
    
    public static String toFactoredForm(double a, double b, double c) {
        QuadraticResult result = solve(a, b, c);
        if (result.numRoots == 2) {
            return String.format("y = %g(x - %g)(x - %g)", a, result.roots[0], result.roots[1]);
        }
        return "Cannot be factored over reals";
    }
}
```

## 3. Polynomial Operations

```java
package com.mathacademy.algebra;

import java.util.ArrayList;
import java.util.List;

public class Polynomial {
    private List<Double> coefficients;
    private int degree;
    
    public Polynomial(double... coeffs) {
        this.coefficients = new ArrayList<>();
        for (double c : coeffs) {
            this.coefficients.add(c);
        }
        this.degree = coeffs.length - 1;
    }
    
    public Polynomial(List<Double> coeffs) {
        this.coefficients = new ArrayList<>(coeffs);
        this.degree = coeffs.size() - 1;
    }
    
    public static Polynomial fromRoots(double... roots) {
        List<Double> coeffs = new ArrayList<>();
        coeffs.add(1.0);
        
        for (double r : roots) {
            List<Double> newCoeffs = new ArrayList<>();
            newCoeffs.add(-r);
            newCoeffs.add(1);
            
            coeffs = multiply(coeffs, newCoeffs);
        }
        
        return new Polynomial(coeffs);
    }
    
    public double evaluate(double x) {
        double result = 0;
        double power = 1;
        
        for (double c : coefficients) {
            result += c * power;
            power *= x;
        }
        return result;
    }
    
    public Polynomial add(Polynomial other) {
        int maxDegree = Math.max(this.degree, other.degree);
        List<Double> result = new ArrayList<>();
        
        for (int i = 0; i <= maxDegree; i++) {
            double a = i <= this.degree ? this.coefficients.get(i) : 0;
            double b = i <= other.degree ? other.coefficients.get(i) : 0;
            result.add(a + b);
        }
        
        return new Polynomial(trimZeros(result));
    }
    
    public Polynomial subtract(Polynomial other) {
        int maxDegree = Math.max(this.degree, other.degree);
        List<Double> result = new ArrayList<>();
        
        for (int i = 0; i <= maxDegree; i++) {
            double a = i <= this.degree ? this.coefficients.get(i) : 0;
            double b = i <= other.degree ? other.coefficients.get(i) : 0;
            result.add(a - b);
        }
        
        return new Polynomial(trimZeros(result));
    }
    
    public Polynomial multiply(Polynomial other) {
        int resultDegree = this.degree + other.degree;
        List<Double> result = new ArrayList<>();
        
        for (int i = 0; i <= resultDegree; i++) {
            result.add(0.0);
        }
        
        for (int i = 0; i <= this.degree; i++) {
            for (int j = 0; j <= other.degree; j++) {
                result.set(i + j, result.get(i + j) + 
                    this.coefficients.get(i) * other.coefficients.get(j));
            }
        }
        
        return new Polynomial(trimZeros(result));
    }
    
    public static Polynomial multiply(List<Double> a, List<Double> b) {
        int resultDegree = a.size() + b.size() - 1;
        List<Double> result = new ArrayList<>();
        
        for (int i = 0; i < resultDegree; i++) {
            result.add(0.0);
        }
        
        for (int i = 0; i < a.size(); i++) {
            for (int j = 0; j < b.size(); j++) {
                result.set(i + j, result.get(i + j) + a.get(i) * b.get(j));
            }
        }
        
        return new Polynomial(result);
    }
    
    public Polynomial[] divide(Polynomial divisor) {
        if (divisor.degree > this.degree) {
            return new Polynomial[]{new Polynomial(0), this};
        }
        
        Polynomial remainder = new Polynomial(this.coefficients);
        Polynomial quotientCoeffs = new ArrayList<>();
        
        for (int i = this.degree; i >= divisor.degree; i--) {
            double coeff = remainder.coefficients.get(i) / divisor.coefficients.get(divisor.degree);
            quotientCoeffs.add(0, coeff);
            
            List<Double> subtrahend = new ArrayList<>();
            for (int j = 0; j <= divisor.degree; j++) {
                subtrahend.add(coeff * divisor.coefficients.get(j));
            }
            
            while (subtrahend.size() < i + 1) {
                subtrahend.add(0, 0.0);
            }
            
            List<Double> newRemainder = new ArrayList<>();
            for (int j = 0; j <= i; j++) {
                newRemainder.add(remainder.coefficients.get(j) - subtrahend.get(j));
            }
            remainder = new Polynomial(trimZeros(newRemainder));
        }
        
        while (quotientCoeffs.size() < this.degree - divisor.degree + 1) {
            quotientCoeffs.add(0, 0.0);
        }
        
        return new Polynomial[]{new Polynomial(trimZeros(quotientCoeffs)), remainder};
    }
    
    public double[] syntheticDivision(double root) {
        List<Double> result = new ArrayList<>();
        double carry = 0;
        
        for (double c : coefficients) {
            carry = c + carry * root;
            result.add(carry);
        }
        
        double[] quotient = new double[result.size() - 1];
        for (int i = 0; i < quotient.length; i++) {
            quotient[i] = result.get(i);
        }
        
        return new double[]{remainder(result.get(result.size() - 1)), quotient[0], quotient[1]};
    }
    
    private static double remainder(double val) {
        return Math.abs(val) < 1e-10 ? 0 : val;
    }
    
    public List<Double> findAllRoots() {
        List<Double> roots = new ArrayList<>();
        List<Double> coeffs = new ArrayList<>(this.coefficients);
        
        double[] rootsArray = findNumericalRoots(coeffs);
        for (double r : rootsArray) {
            roots.add(r);
        }
        
        return roots;
    }
    
    private double[] findNumericalRoots(List<Double> coeffs) {
        List<Double> found = new ArrayList<>();
        List<Double> current = new ArrayList<>(coeffs);
        
        while (current.size() > 2 && found.size() < coeffs.size() - 1) {
            double root = findOneRoot(current);
            if (Double.isNaN(root)) break;
            
            found.add(root);
            
            double[] synthetic = synthetic(root, current);
            current = new ArrayList<>();
            for (int i = 0; i < synthetic.length - 1; i++) {
                current.add(synthetic[i]);
            }
        }
        
        if (current.size() == 3) {
            QuadraticSolver.QuadraticResult qr = 
                QuadraticSolver.solve(current.get(0), current.get(1), current.get(2));
            for (double r : qr.roots) {
                found.add(r);
            }
        }
        
        double[] result = new double[found.size()];
        for (int i = 0; i < found.size(); i++) {
            result[i] = found.get(i);
        }
        return result;
    }
    
    private double findOneRoot(List<Double> coeffs) {
        for (double x = -100; x <= 100; x += 0.1) {
            double val = evaluateAt(coeffs, x);
            if (Math.abs(val) < 1e-6) {
                return x;
            }
        }
        return Double.NaN;
    }
    
    private double evaluateAt(List<Double> coeffs, double x) {
        double result = 0;
        double power = 1;
        for (double c : coeffs) {
            result += c * power;
            power *= x;
        }
        return result;
    }
    
    private double[] synthetic(double root, List<Double> coeffs) {
        double[] result = new double[coeffs.size()];
        double carry = 0;
        
        for (int i = 0; i < coeffs.size(); i++) {
            carry = coeffs.get(i) + carry * root;
            result[i] = carry;
        }
        
        return result;
    }
    
    private static List<Double> trimZeros(List<Double> coeffs) {
        while (coeffs.size() > 1 && Math.abs(coeffs.get(coeffs.size() - 1)) < 1e-10) {
            coeffs.remove(coeffs.size() - 1);
        }
        return coeffs;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        for (int i = coefficients.size() - 1; i >= 0; i--) {
            double c = coefficients.get(i);
            if (Math.abs(c) < 1e-10) continue;
            
            if (sb.length() > 0 && c > 0) {
                sb.append(" + ");
            } else if (c < 0) {
                sb.append(" - ");
            } else {
                sb.append(c);
                continue;
            }
            
            int power = coefficients.size() - 1 - i;
            if (Math.abs(Math.abs(c)) != 1 || power == 0) {
                sb.append(String.format("%.2g", Math.abs(c)));
            }
            
            if (power > 0) {
                sb.append("x");
                if (power > 1) {
                    sb.append("^").append(power);
                }
            }
        }
        
        return sb.length() == 0 ? "0" : sb.toString();
    }
    
    public int getDegree() { return degree; }
    public List<Double> getCoefficients() { return coefficients; }
}
```

## 4. Rational Expression Class

```java
package com.mathacademy.algebra;

public class RationalExpression {
    private Polynomial numerator;
    private Polynomial denominator;
    
    public RationalExpression(Polynomial num, Polynomial denom) {
        if (denom.getDegree() < 0 || 
            (denom.getDegree() == 0 && denom.getCoefficients().get(0) == 0)) {
            throw new ArithmeticException("Division by zero");
        }
        this.numerator = num;
        this.denominator = denom;
    }
    
    public RationalExpression simplify() {
        Polynomial[] divResult = numerator.divide(denominator);
        Polynomial simplifiedNum = divResult[1];
        Polynomial simplifiedDen = denominator;
        
        double gcdCoef = gcdCoefficients(simplifiedNum, simplifiedDen);
        
        return new RationalExpression(
            new Polynomial(divideCoefficients(simplifiedNum.getCoefficients(), gcdCoef)),
            new Polynomial(divideCoefficients(simplifiedDen.getCoefficients(), gcdCoef))
        );
    }
    
    private double gcdCoefficients(Polynomial a, Polynomial b) {
        double g = a.getCoefficients().get(0);
        for (double c : a.getCoefficients()) {
            g = gcd(g, c);
        }
        for (double c : b.getCoefficients()) {
            g = gcd(g, c);
        }
        return g;
    }
    
    private double gcd(double a, double b) {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b > 1e-10) {
            double temp = a % b;
            a = b;
            b = temp;
        }
        return a;
    }
    
    private java.util.List<Double> divideCoefficients(java.util.List<Double> coeffs, double divisor) {
        java.util.List<Double> result = new java.util.ArrayList<>();
        for (double c : coeffs) {
            result.add(c / divisor);
        }
        return result;
    }
    
    public RationalExpression add(RationalExpression other) {
        Polynomial num = this.numerator.multiply(other.denominator)
                            .add(other.numerator.multiply(this.denominator));
        Polynomial denom = this.denominator.multiply(other.denominator);
        return new RationalExpression(num, denom).simplify();
    }
    
    public RationalExpression subtract(RationalExpression other) {
        Polynomial num = this.numerator.multiply(other.denominator)
                            .subtract(other.numerator.multiply(this.denominator));
        Polynomial denom = this.denominator.multiply(other.denominator);
        return new RationalExpression(num, denom).simplify();
    }
    
    public RationalExpression multiply(RationalExpression other) {
        Polynomial num = this.numerator.multiply(other.numerator);
        Polynomial denom = this.denominator.multiply(other.denominator);
        return new RationalExpression(num, denom).simplify();
    }
    
    public RationalExpression divide(RationalExpression other) {
        if (other.numerator.getDegree() == 0 && 
            other.numerator.getCoefficients().get(0) == 0) {
            throw new ArithmeticException("Division by zero");
        }
        
        RationalExpression reciprocal = new RationalExpression(other.denominator, other.numerator);
        return this.multiply(reciprocal);
    }
    
    public RationalExpression partialFractionDecomposition() {
        return this;
    }
    
    @Override
    public String toString() {
        return numerator + " / " + denominator;
    }
}
```

## 5. Matrix Operations

```java
package com.mathacademy.algebra;

import java.util.Arrays;

public class Matrix {
    private double[][] data;
    private int rows, cols;
    
    public Matrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.data = new double[rows][cols];
    }
    
    public Matrix(double[][] data) {
        this.data = data;
        this.rows = data.length;
        this.cols = data[0].length;
    }
    
    public Matrix add(Matrix other) {
        if (rows != other.rows || cols != other.cols) {
            throw new IllegalArgumentException("Dimension mismatch");
        }
        
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = data[i][j] + other.data[i][j];
            }
        }
        return new Matrix(result);
    }
    
    public Matrix subtract(Matrix other) {
        if (rows != other.rows || cols != other.cols) {
            throw new IllegalArgumentException("Dimension mismatch");
        }
        
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = data[i][j] - other.data[i][j];
            }
        }
        return new Matrix(result);
    }
    
    public Matrix multiply(Matrix other) {
        if (cols != other.rows) {
            throw new IllegalArgumentException("Dimension mismatch for multiplication");
        }
        
        double[][] result = new double[rows][other.cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < other.cols; j++) {
                for (int k = 0; k < cols; k++) {
                    result[i][j] += data[i][k] * other.data[k][j];
                }
            }
        }
        return new Matrix(result);
    }
    
    public Matrix scalarMultiply(double scalar) {
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = data[i][j] * scalar;
            }
        }
        return new Matrix(result);
    }
    
    public double determinant() {
        if (rows != cols) {
            throw new IllegalArgumentException("Matrix must be square");
        }
        
        if (rows == 1) return data[0][0];
        if (rows == 2) {
            return data[0][0] * data[1][1] - data[0][1] * data[1][0];
        }
        
        double det = 0;
        for (int j = 0; j < cols; j++) {
            det += Math.pow(-1, j) * data[0][j] * cofactor(0, j);
        }
        return det;
    }
    
    public Matrix transpose() {
        double[][] result = new double[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[j][i] = data[i][j];
            }
        }
        return new Matrix(result);
    }
    
    public Matrix cofactorMatrix() {
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = Math.pow(-1, i + j) * minor(i, j);
            }
        }
        return new Matrix(result);
    }
    
    public Matrix adjoint() {
        return cofactorMatrix().transpose();
    }
    
    public Matrix inverse() {
        double det = determinant();
        if (Math.abs(det) < 1e-10) {
            throw new ArithmeticException("Matrix is singular");
        }
        
        return adjoint().scalarMultiply(1.0 / det);
    }
    
    public double minor(int row, int col) {
        double[][] sub = new double[rows - 1][cols - 1];
        for (int i = 0, ii = 0; i < rows; i++) {
            if (i == row) continue;
            for (int j = 0, jj = 0; j < cols; j++) {
                if (j == col) continue;
                sub[ii][jj++] = data[i][j];
            }
            ii++;
        }
        return new Matrix(sub).determinant();
    }
    
    public double cofactor(int row, int col) {
        return Math.pow(-1, row + col) * minor(row, col);
    }
    
    public double[] solve(double[] constants) {
        if (rows != cols || rows != constants.length) {
            throw new IllegalArgumentException("Invalid dimensions");
        }
        
        Matrix augmented = new Matrix(rows, cols + 1);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                augmented.data[i][j] = data[i][j];
            }
            augmented.data[i][cols] = constants[i];
        }
        
        for (int i = 0; i < rows; i++) {
            int maxRow = i;
            for (int k = i + 1; k < rows; k++) {
                if (Math.abs(augmented.data[k][i]) > Math.abs(augmented.data[maxRow][i])) {
                    maxRow = k;
                }
            }
            
            double[] temp = augmented.data[i];
            augmented.data[i] = augmented.data[maxRow];
            augmented.data[maxRow] = temp;
            
            if (Math.abs(augmented.data[i][i]) < 1e-10) {
                throw new ArithmeticException("No unique solution");
            }
            
            for (int k = i + 1; k < rows; k++) {
                double factor = augmented.data[k][i] / augmented.data[i][i];
                for (int j = i; j <= cols; j++) {
                    augmented.data[k][j] -= factor * augmented.data[i][j];
                }
            }
        }
        
        double[] solution = new double[rows];
        for (int i = rows - 1; i >= 0; i--) {
            solution[i] = augmented.data[i][cols];
            for (int j = i + 1; j < cols; j++) {
                solution[i] -= augmented.data[i][j] * solution[j];
            }
            solution[i] /= augmented.data[i][i];
        }
        
        return solution;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            sb.append("[");
            for (int j = 0; j < cols; j++) {
                sb.append(String.format("%8.3f", data[i][j]));
                if (j < cols - 1) sb.append(" ");
            }
            sb.append("]\n");
        }
        return sb.toString();
    }
    
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public double[][] getData() { return data; }
}
```

## 6. Sequence and Series

```java
package com.mathacademy.algebra;

import java.util.ArrayList;
import java.util.List;

public class SequenceSeries {
    
    public static class ArithmeticSequence {
        private double firstTerm;
        private double commonDifference;
        
        public ArithmeticSequence(double a1, double d) {
            this.firstTerm = a1;
            this.commonDifference = d;
        }
        
        public double nthTerm(int n) {
            return firstTerm + (n - 1) * commonDifference;
        }
        
        public double sumFirstN(int n) {
            return n * (2 * firstTerm + (n - 1) * commonDifference) / 2;
        }
        
        public double sumToN(int n) {
            return n * (firstTerm + nthTerm(n)) / 2;
        }
        
        public List<Double> firstNTerms(int n) {
            List<Double> terms = new ArrayList<>();
            for (int i = 1; i <= n; i++) {
                terms.add(nthTerm(i));
            }
            return terms;
        }
    }
    
    public static class GeometricSequence {
        private double firstTerm;
        private double commonRatio;
        
        public GeometricSequence(double a1, double r) {
            this.firstTerm = a1;
            this.commonRatio = r;
        }
        
        public double nthTerm(int n) {
            return firstTerm * Math.pow(commonRatio, n - 1);
        }
        
        public double sumFirstN(int n) {
            if (Math.abs(commonRatio - 1) < 1e-10) {
                return firstTerm * n;
            }
            return firstTerm * (1 - Math.pow(commonRatio, n)) / (1 - commonRatio);
        }
        
        public double infiniteSum() {
            if (Math.abs(commonRatio) >= 1) {
                throw new ArithmeticException("Series does not converge");
            }
            return firstTerm / (1 - commonRatio);
        }
        
        public List<Double> firstNTerms(int n) {
            List<Double> terms = new ArrayList<>();
            for (int i = 1; i <= n; i++) {
                terms.add(nthTerm(i));
            }
            return terms;
        }
    }
    
    public static long binomialCoefficient(int n, int k) {
        if (k < 0 || k > n) return 0;
        if (k == 0 || k == n) return 1;
        
        long result = 1;
        for (int i = 0; i < k; i++) {
            result = result * (n - i) / (i + 1);
        }
        return result;
    }
    
    public static double[] binomialExpansion(double a, double b, int n) {
        double[] coefficients = new double[n + 1];
        for (int k = 0; k <= n; k++) {
            coefficients[k] = binomialCoefficient(n, k);
        }
        return coefficients;
    }
    
    public static String binomialTheorem(double a, double b, int n) {
        StringBuilder sb = new StringBuilder();
        for (int k = 0; k <= n; k++) {
            long coeff = binomialCoefficient(n, k);
            
            if (k > 0) sb.append(" + ");
            
            if (coeff > 1) sb.append(coeff);
            
            int aPower = n - k;
            int bPower = k;
            
            if (aPower > 0) {
                sb.append("(").append(a).append(")").append(aPower == 1 ? "" : "^" + aPower);
            }
            
            if (bPower > 0) {
                sb.append("(").append(b).append(")").append(bPower == 1 ? "" : "^" + bPower);
            }
        }
        return sb.toString();
    }
}
```

## 7. Complex Number Operations

```java
package com.mathacademy.algebra;

import java.util.Objects;

public class ComplexNumber {
    private final double real;
    private final double imaginary;
    
    public ComplexNumber(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }
    
    public static ComplexNumber fromPolar(double r, double theta) {
        return new ComplexNumber(r * Math.cos(theta), r * Math.sin(theta));
    }
    
    public ComplexNumber add(ComplexNumber other) {
        return new ComplexNumber(this.real + other.real, 
                                 this.imaginary + other.imaginary);
    }
    
    public ComplexNumber subtract(ComplexNumber other) {
        return new ComplexNumber(this.real - other.real, 
                                 this.imaginary - other.imaginary);
    }
    
    public ComplexNumber multiply(ComplexNumber other) {
        double newReal = this.real * other.real - this.imaginary * other.imaginary;
        double newImag = this.real * other.imaginary + this.imaginary * other.real;
        return new ComplexNumber(newReal, newImag);
    }
    
    public ComplexNumber divide(ComplexNumber other) {
        double denominator = other.real * other.real + other.imaginary * other.imaginary;
        if (denominator == 0) {
            throw new ArithmeticException("Division by zero");
        }
        
        double newReal = (this.real * other.real + this.imaginary * other.imaginary) / denominator;
        double newImag = (this.imaginary * other.real - this.real * other.imaginary) / denominator;
        return new ComplexNumber(newReal, newImag);
    }
    
    public ComplexNumber conjugate() {
        return new ComplexNumber(real, -imaginary);
    }
    
    public double modulus() {
        return Math.sqrt(real * real + imaginary * imaginary);
    }
    
    public double argument() {
        return Math.atan2(imaginary, real);
    }
    
    public ComplexNumber power(int n) {
        double r = modulus();
        double theta = argument();
        double newR = Math.pow(r, n);
        double newTheta = theta * n;
        return ComplexNumber.fromPolar(newR, newTheta);
    }
    
    public ComplexNumber power(double n) {
        double r = modulus();
        double theta = argument();
        double newR = Math.pow(r, n);
        double newTheta = theta * n;
        return ComplexNumber.fromPolar(newR, newTheta);
    }
    
    public ComplexNumber sqrt() {
        double r = modulus();
        double theta = argument();
        return ComplexNumber.fromPolar(Math.sqrt(r), theta / 2);
    }
    
    public static ComplexNumber exp(ComplexNumber z) {
        double expReal = Math.exp(z.real);
        return new ComplexNumber(expReal * Math.cos(z.imaginary), 
                               expReal * Math.sin(z.imaginary));
    }
    
    public static ComplexNumber ln(ComplexNumber z) {
        double r = z.modulus();
        double theta = z.argument();
        return new ComplexNumber(Math.log(r), theta);
    }
    
    @Override
    public String toString() {
        if (Math.abs(imaginary) < 1e-10) {
            return String.format("%.4f", real);
        }
        if (Math.abs(real) < 1e-10) {
            return String.format("%.4fi", imaginary);
        }
        String sign = imaginary >= 0 ? "+" : "-";
        return String.format("%.4f %s %.4fi", real, sign, Math.abs(imaginary));
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof ComplexNumber) {
            ComplexNumber other = (ComplexNumber) obj;
            return Math.abs(real - other.real) < 1e-10 &&
                   Math.abs(imaginary - other.imaginary) < 1e-10;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(Math.round(real * 1e6), Math.round(imaginary * 1e6));
    }
    
    public double getReal() { return real; }
    public double getImaginary() { return imaginary; }
}
```