package com.example.expr;

import java.util.Objects;


/**
 * An expression consisting of a constant value.
 */
public class ConstantExpression extends Expression {
    private final double value;

    ConstantExpression(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("constant expressions must be finite");
        }
        this.value = value;
    }

    /** The double precision value represented by this constant expression. */
    public double value() {
        return value;
    }

    @Override protected <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(this instanceof ConstantExpression)) return false;
        double otherValue = ((ConstantExpression) obj).value;
        return Objects.equals(value, otherValue);
    }

    @Override public int hashCode() {
        return Objects.hash(value);
    }
}
