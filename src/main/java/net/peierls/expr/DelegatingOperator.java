package net.peierls.expr;

/**
 * A partial implementation of both the unary and binary operator interfaces
 * that delegates to an underlying operator.
 */
public interface DelegatingOperator extends UnaryOp, BinaryOp {

    /**
     * Returns the operator to which method calls can be delegated.
     */
    Operator delegate();

    /**
     * Returns a valid unary operator, by default the delegate().
     * @throws ClassCastException if the delegate is not a unary operator
      */
    default UnaryOp asUnary() {
        if (delegate().type().arity() == 1) {
            return ((UnaryOp) delegate());
        }
        throw new ClassCastException("Attempt to evaluate binary operator as unary operator");
    }

    /**
     * Returns a valid binary operator, by default the delegate().
     * @throws ClassCastException if the delegate is not a binary operator
     */
    default BinaryOp asBinary() {
        if (delegate().type().arity() == 2) {
            return ((BinaryOp) delegate());
        }
        throw new ClassCastException("Attempt to evaluate unary operator as binary operator");
    }

    @Override default String symbol() { return delegate().symbol(); }
    @Override default Type type() { return delegate().type(); }
    @Override default int precedence() { return delegate().precedence(); }

    @Override default double evaluate(double v) { return asUnary().evaluate(v); }
    @Override default String format(String s) { return asUnary().format(s); }

    @Override default double evaluate(double v1, double v2) { return asBinary().evaluate(v1, v2); }
    @Override default String format(String s1, String s2) { return asBinary().format(s1, s2); }
}
