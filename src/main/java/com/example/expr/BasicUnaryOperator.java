package com.example.expr;

import static com.example.expr.Operator.Associativity;
import static com.example.expr.Operator.Fixity;
import static com.example.expr.Operator.Fixity.POSTFIX;
import static com.example.expr.OperatorBuilder.op;


/**
 * Basic unary operations on expressions.
 */
@SuppressWarnings("ImmutableEnumChecker")
public enum BasicUnaryOperator implements UnaryOperator {

    NEGATED(op().precedence(14).unary("-", a -> -a)),
    SQUARED(op().precedence(15).fixity(POSTFIX).unary("^2", a -> a * a)),
    SQUARE_ROOT(op().precedence(15).unary("sqrt ", Math::sqrt)),

    ;

    UnaryOperator delegate;

    BasicUnaryOperator(UnaryOperator delegate) {
        this.delegate = delegate;
    }

    @Override public Associativity associativity() { return delegate.associativity(); }
    @Override public Fixity fixity() { return delegate.fixity(); }
    @Override public int precedence() { return delegate.precedence(); }
    @Override public double evaluate(double v) { return delegate.evaluate(v); }
    @Override public String format(String s) { return delegate.format(s); }
}
