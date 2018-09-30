package com.example.expr;

import static com.example.expr.Operator.*;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

/**
 *
 */
public class OperatorBuilder implements UnaryOp, BinaryOp {

    final int arity;
    final DoubleUnaryOperator unaryOp;
    final DoubleBinaryOperator binaryOp;
    final String symbol;
    final int precedence;
    final Fixity fixity;
    final Associativity associativity;


    OperatorBuilder(int arity, DoubleUnaryOperator unaryOp, DoubleBinaryOperator binaryOp,
            String symbol, int precedence, Fixity fixity, Associativity associativity) {
        this.arity = arity;
        this.unaryOp = unaryOp;
        this.binaryOp = binaryOp;
        this.symbol = symbol;
        this.precedence = precedence;
        this.fixity = fixity;
        this.associativity = associativity;
    }


    /**
     * Creates a builder for a unary operator.
     */
    public static OperatorBuilder op(String symbol, DoubleUnaryOperator unaryOp) {
        if (unaryOp == null) {
            throw new IllegalArgumentException("operator argument must not be null");
        }
        // XXX ensure valid symbol
        return new OperatorBuilder(1, unaryOp, null, symbol, 0, Fixity.PREFIX, Associativity.RIGHT_TO_LEFT);
    }

    /**
     * Creates a builder for a binary operator.
     */
    public static OperatorBuilder op(String symbol, DoubleBinaryOperator binaryOp) {
        if (binaryOp == null) {
            throw new IllegalArgumentException("operator argument must not be null");
        }
        // XXX ensure valid symbol
        return new OperatorBuilder(2, null, binaryOp, symbol, 0, Fixity.INFIX, Associativity.LEFT_TO_RIGHT);
    }

    /**
     * Sets the precedence of this operator.
     */
    public OperatorBuilder precedence(int precedence) {
        return new OperatorBuilder(this.arity, this.unaryOp, this.binaryOp, this.symbol, precedence, this.fixity, this.associativity);
    }

    /**
     * Sets the fixity of this operator.
     */
    public OperatorBuilder fixity(Fixity fixity) {
        if (this.arity == 1 && fixity == Fixity.INFIX) {
            throw new IllegalArgumentException("unary operators must be either prefix or postfix");
        } else if (this.arity == 2 && fixity != Fixity.INFIX) {
            throw new IllegalArgumentException("binary operators must be infix");
        }
        return new OperatorBuilder(this.arity, this.unaryOp, this.binaryOp, this.symbol, this.precedence, fixity, this.associativity);
    }

    /**
     * Sets the associativity of this operator.
     */
    public OperatorBuilder associativity(Associativity associativity) {
        return new OperatorBuilder(this.arity, this.unaryOp, this.binaryOp, this.symbol, this.precedence, this.fixity, associativity);
    }


    @Override public String symbol() {
        return symbol;
    }

    @Override public int arity() {
        return arity;
    }

    @Override public int precedence() {
        return precedence;
    }

    @Override public Fixity fixity() {
        return fixity;
    }

    @Override public Associativity associativity() {
        return associativity;
    }


    @Override public double evaluate(double v) {
        return unaryOp.applyAsDouble(v);
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

    @Override public double evaluate(double v1, double v2) {
        return binaryOp.applyAsDouble(v1, v2);
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
