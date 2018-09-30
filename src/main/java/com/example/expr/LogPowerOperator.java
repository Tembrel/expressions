package com.example.expr;

import static com.example.expr.OperatorBuilder.*;


/**
 * Logarithmic and exponential (power) operations on expressions.
 */
@SuppressWarnings("ImmutableEnumChecker")
public enum LogPowerOperator implements DelegatingOp {

    NATURAL_LOG (prefix("ln ", Math::log)       .precedence(100)),
    LOG_BASE_10 (prefix("log ", Math::log10)    .precedence(100)),
    POW         (infixr("^", Math::pow)         .precedence(30)),

    ;

    Operator delegate;

    LogPowerOperator(Operator delegate) {
        this.delegate = delegate;
    }

    @Override public Operator delegate() { return delegate; }


    public static UnaryOpExpression ln(Expression expr) {
        return new UnaryOpExpression(NATURAL_LOG, expr);
    }

    public static UnaryOpExpression log10(Expression expr) {
        return new UnaryOpExpression(LOG_BASE_10, expr);
    }

    public static BinaryOpExpression pow(Expression e1, Expression e2) {
        return new BinaryOpExpression(POW, e1, e2);
    }
}
