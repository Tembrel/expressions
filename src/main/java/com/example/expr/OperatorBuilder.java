package com.example.expr;

import static com.example.expr.Operator.*;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

/**
 *
 */
public class OperatorBuilder {

    final Associativity associativity;
    final Fixity fixity;
    final int precedence;
    final int arity;


    OperatorBuilder() {
        this.associativity = null;
        this.fixity = null;
        this.precedence = 0;
        this.arity = 0;
    }

    OperatorBuilder(Associativity associativity, Fixity fixity, int precedence, int arity) {
        this.associativity = associativity;
        this.fixity = fixity;
        this.precedence = precedence;
        this.arity = arity;
    }


    /**
     * Creates a vanilla operator builder.
     */
    public static OperatorBuilder op() {
        return new OperatorBuilder();
    }

    /**
     * Sets the associativity of this operator.
     */
    public OperatorBuilder associativity(Associativity associativity) {
        return new OperatorBuilder(associativity, this.fixity, this.precedence, this.arity);
    }

    /**
     * Sets the fixity of this operator.
     */
    public OperatorBuilder fixity(Fixity fixity) {
        return new OperatorBuilder(this.associativity, fixity, this.precedence, this.arity);
    }

    /**
     * Sets the precedence of this operator.
     */
    public OperatorBuilder precedence(int precedence) {
        return new OperatorBuilder(this.associativity, this.fixity, precedence, this.arity);
    }

    /**
     * Sets the arity of this operator.
     */
    OperatorBuilder arity(int arity) {
        return new OperatorBuilder(this.associativity, this.fixity, this.precedence, arity);
    }


    static abstract class OperatorImpl implements Operator {
        final String symbol;
        final Associativity associativity;
        final Fixity fixity;
        final int precedence;

        OperatorImpl(String symbol, Associativity associativity, Fixity fixity, int precedence) {
            this.symbol = symbol;
            this.associativity = associativity;
            this.fixity = fixity;
            this.precedence = precedence;
        }

        @Override public Associativity associativity() {
            return associativity;
        }

        @Override public Fixity fixity() {
            return fixity;
        }

        @Override public int precedence() {
            return precedence;
        }
    }


    /**
     * Builds a unary operator with the given symbol and evaluation function.
     */
    public UnaryOperator unary(String symbol, DoubleUnaryOperator op) {
        return arity(1).newUnary(symbol, op);
    }

    private UnaryOperator newUnary(String symbol, DoubleUnaryOperator op) {
        return new UnaryOperatorImpl(symbol, op, associativity(), fixity(), precedence);
    }

    static class UnaryOperatorImpl extends OperatorImpl implements UnaryOperator {

        final DoubleUnaryOperator op;

        UnaryOperatorImpl(String symbol, DoubleUnaryOperator op,
                Associativity associativity, Fixity fixity, int precedence) {
            super(symbol, associativity, fixity, precedence);
            this.op = op;
        }

        @Override public double evaluate(double v) {
            return op.applyAsDouble(v);
        }

        @Override public String format(String s) {
            switch (fixity()) {
                case PREFIX:
                    return String.format("%s%s", symbol, s);
                case POSTFIX:
                    return String.format("%s%s", s, symbol);
                case INFIX:
                default:
                    return "?";
            }
        }
    }


    /**
     * Builds a binary operator with the given symbol and evaluation function.
     */
    public BinaryOperator binary(String symbol, DoubleBinaryOperator op) {
        return arity(2).newBinary(symbol, op);
    }

    private BinaryOperator newBinary(String symbol, DoubleBinaryOperator op) {
        return new BinaryOperatorImpl(symbol, op, associativity(), fixity(), precedence);
    }

    static class BinaryOperatorImpl extends OperatorImpl implements BinaryOperator {

        final DoubleBinaryOperator op;

        BinaryOperatorImpl(String symbol, DoubleBinaryOperator op,
                Associativity associativity, Fixity fixity, int precedence) {
            super(symbol, associativity, fixity, precedence);
            this.op = op;
        }

        @Override public double evaluate(double v1, double v2) {
            return op.applyAsDouble(v1, v2);
        }

        @Override public String format(String s1, String s2) {
            switch (fixity()) {
                case INFIX:
                    return String.format("%s%s%s", s1, symbol, s2);
                case PREFIX:
                case POSTFIX:
                default:
                    return "?";
            }
        }
    }


    private Associativity associativity() {
        if (associativity == null) {
            switch (arity) {
                case 1:
                    switch (fixity()) {
                        case PREFIX:
                            return Associativity.RIGHT_TO_LEFT;
                        case INFIX:
                        case POSTFIX:
                        default:
                            return Associativity.NOT_ASSOCIATIVE;
                    }
                case 2:
                    switch (fixity()) {
                        case INFIX:
                            return Associativity.LEFT_TO_RIGHT;
                        case PREFIX:
                        case POSTFIX:
                        default:
                            return Associativity.NOT_ASSOCIATIVE;
                    }
                default:
                    return Associativity.NOT_ASSOCIATIVE;
            }
        } else {
            return associativity;
        }
    }

    private Fixity fixity() {
        if (fixity == null) {
            switch (arity) {
                case 1:
                default:
                    return Fixity.PREFIX;
                case 2:
                    return Fixity.INFIX;
            }
        } else {
            return fixity;
        }
    }
}
