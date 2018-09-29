package com.example.expr;

import static com.example.expr.OperatorBuilder.binary;
import static com.example.expr.OperatorBuilder.unary;


/**
 * Logarithmic and exponential (power) operations on expressions.
 */
@SuppressWarnings("ImmutableEnumChecker")
public enum LogPowerOperator implements UnaryOperator, BinaryOperator {

    NATURAL_LOG (unary("ln ", Math::log)        .precedence(15)),
    LOG_BASE_10 (unary("log ", Math::log10)     .precedence(15)),

    POW         (binary("^", Math::pow)         .precedence(15)),

    ;

    Operator delegate;

    LogPowerOperator(Operator delegate) {
        this.delegate = delegate;
    }

    UnaryOperator unaryOp() {
        if (delegate.arity() == 1) {
            return ((UnaryOperator) delegate);
        }
        throw new ClassCastException("Attempt to evaluate binary operator as unary operator");
    }

    BinaryOperator binaryOp() {
        if (delegate.arity() == 2) {
            return ((BinaryOperator) delegate);
        }
        throw new ClassCastException("Attempt to evaluate unary operator as binary operator");
    }


    @Override public int precedence() { return delegate.precedence(); }
    @Override public Fixity fixity() { return delegate.fixity(); }
    @Override public Associativity associativity() { return delegate.associativity(); }

    @Override public double evaluate(double v) { return unaryOp().evaluate(v); }
    @Override public String format(String s) { return unaryOp().format(s); }

    @Override public double evaluate(double v1, double v2) { return binaryOp().evaluate(v1, v2); }
    @Override public String format(String s1, String s2) { return binaryOp().format(s1, s2); }


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
