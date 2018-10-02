package net.peierls.expr;

/** Thrown on a division by zero during evaluation */
public class DivisionByZeroException extends EvaluationException {
    final double numerator;
    final double denominator;
    DivisionByZeroException(double numerator, double denominator) {
        super(String.format("Division by zero or NaN during evaluation: %f/%f", numerator, denominator));
        this.numerator = numerator;
        this.denominator = denominator;
    }
    public double numerator() { return numerator; }
    public double denominator() { return denominator; }
}
