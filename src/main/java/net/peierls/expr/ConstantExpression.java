package net.peierls.expr;

import java.util.Objects;


/**
 * An expression consisting of a constant value.
 */
public class ConstantExpression extends Expression {

    private static final ConstantExpression PI = new ConstantExpression(Math.PI);
    private static final ConstantExpression E = new ConstantExpression(Math.E);

    private final double value;

    ConstantExpression(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("constant expressions must be finite");
        }
        this.value = value;
    }


    /**
     * Constant expression for the value of pi.
     */
    public static ConstantExpression pi() {
        return PI;
    }

    /**
     * Constant expression for the value of e.
     */
    public static ConstantExpression e() {
        return E;
    }

    /** The double precision value represented by this constant expression. */
    @Override public double value() {
        return value;
    }

    @Override protected <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ConstantExpression)) return false;
        double otherValue = ((ConstantExpression) obj).value;
        return Objects.equals(value, otherValue);
    }

    @Override public int hashCode() {
        return Objects.hash(value);
    }
}
