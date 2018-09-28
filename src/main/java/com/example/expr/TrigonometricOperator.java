package com.example.expr;

import static com.example.expr.Operator.Associativity;
import static com.example.expr.Operator.Fixity;
import static com.example.expr.OperatorBuilder.op;


/**
 * Trigonometric operations on expressions.
 */
@SuppressWarnings("ImmutableEnumChecker")
public enum TrigonometricOperator implements UnaryOperator {

    SINE(make().unary("sin ", Math::sin)),
    COSINE(make().unary("cos ", Math::cos)),
    TANGENT(make().unary("tan ", Math::tan)),
    ARC_SINE(make().unary("asin ", Math::asin)),
    ARC_COSINE(make().unary("acos ", Math::acos)),
    ARC_TANGENT(make().unary("atan ", Math::atan)),

    ;

    private static final OperatorBuilder make() {
        return op().precedence(15);
    }

    UnaryOperator delegate;

    TrigonometricOperator(UnaryOperator delegate) {
        this.delegate = delegate;
    }

    @Override public Associativity associativity() { return delegate.associativity(); }
    @Override public Fixity fixity() { return delegate.fixity(); }
    @Override public int precedence() { return delegate.precedence(); }
    @Override public double evaluate(double v) { return delegate.evaluate(v); }
    @Override public String format(String s) { return delegate.format(s); }


    public static UnaryOperationExpression sin(Expression expr) {
        return new UnaryOperationExpression(SINE, expr);
    }

    public static UnaryOperationExpression cos(Expression expr) {
        return new UnaryOperationExpression(COSINE, expr);
    }

    public static UnaryOperationExpression tan(Expression expr) {
        return new UnaryOperationExpression(TANGENT, expr);
    }

    public static UnaryOperationExpression asin(Expression expr) {
        return new UnaryOperationExpression(ARC_SINE, expr);
    }

    public static UnaryOperationExpression acos(Expression expr) {
        return new UnaryOperationExpression(ARC_COSINE, expr);
    }

    public static UnaryOperationExpression atan(Expression expr) {
        return new UnaryOperationExpression(ARC_TANGENT, expr);
    }
}
