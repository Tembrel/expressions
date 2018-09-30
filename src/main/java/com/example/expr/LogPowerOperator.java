package com.example.expr;

import static com.example.expr.OperatorBuilder.op;


/**
 * Logarithmic and exponential (power) operations on expressions.
 */
@SuppressWarnings("ImmutableEnumChecker")
public enum LogPowerOperator implements DelegatingOp {

    NATURAL_LOG (op("ln ", Math::log)    .precedence(15)),
    LOG_BASE_10 (op("log ", Math::log10) .precedence(15)),

    POW         (op("^", Math::pow)      .precedence(15)),

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
