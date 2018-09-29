package com.example.expr;

import static com.example.expr.Operator.Associativity;
import static com.example.expr.Operator.Fixity;
import static com.example.expr.OperatorBuilder.op;


/**
 * Logarithmic and exponential (power) operations on expressions.
 */
@SuppressWarnings("ImmutableEnumChecker")
public enum LogPowerOperator implements UnaryOperator, BinaryOperator {

    NATURAL_LOG(make().unary("ln ", Math::log)),
    LOG_BASE_10(make().unary("log ", Math::log10)),
    POW(make().binary("^", Math::pow)),

    ;

    private static final OperatorBuilder make() {
        return op().precedence(15);
    }

    Operator delegate;

    LogPowerOperator(Operator delegate) {
        this.delegate = delegate;
    }

    UnaryOperator unary() {
        if (delegate.arity() == 1) {
            return ((UnaryOperator) delegate);
        }
        throw new ClassCastException("Attempt to evaluate binary operator as unary operator");
    }

    BinaryOperator binary() {
        if (delegate.arity() == 2) {
            return ((BinaryOperator) delegate);
        }
        throw new ClassCastException("Attempt to evaluate unary operator as binary operator");
    }


    @Override public Associativity associativity() { return delegate.associativity(); }
    @Override public Fixity fixity() { return delegate.fixity(); }
    @Override public int precedence() { return delegate.precedence(); }
    @Override public double evaluate(double v) { return unary().evaluate(v); }
    @Override public String format(String s) { return unary().format(s); }
    @Override public double evaluate(double v1, double v2) { return binary().evaluate(v1, v2); }
    @Override public String format(String s1, String s2) { return binary().format(s1, s2); }

    public static UnaryOperationExpression ln(Expression expr) {
        return new UnaryOperationExpression(NATURAL_LOG, expr);
    }

    public static UnaryOperationExpression log10(Expression expr) {
        return new UnaryOperationExpression(LOG_BASE_10, expr);
    }

    public static BinaryOperationExpression pow(Expression e1, Expression e2) {
        return new BinaryOperationExpression(POW, e1, e2);
    }
}
