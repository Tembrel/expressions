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
