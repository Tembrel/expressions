package net.peierls.expr;

import static net.peierls.expr.Operator.*;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

/**
 * Fluent construction of new operators.
 * Use one of these static methods to start:
 * <ul>
 *   <li>{@link OperatorBuilder#prefix prefix}</li>
 *   <li>{@link OperatorBuilder#postfix postfix}</li>
 *   <li>{@link OperatorBuilder#infixl infixl}</li>
 *   <li>{@link OperatorBuilder#infixn infixn}</li>
 *   <li>{@link OperatorBuilder#infixr infixr}</li>
 * </ul>
 * and optionally set a precedence with
 * {@link OperatorBuilder#precedence(int) precedence(int)}.
 * By default, unary operators will have higher precedence
 * than standard binary operators and lower precedence than
 * standard unary operators, and binary operators will have
 * the same precedence as addition.
 */
public class OperatorBuilder implements UnaryOp, BinaryOp {

    final DoubleUnaryOperator unaryOp;
    final DoubleBinaryOperator binaryOp;
    final String symbol;
    final int precedence;
    final Type type;


    OperatorBuilder(DoubleUnaryOperator unaryOp, DoubleBinaryOperator binaryOp,
            String symbol, int precedence, Type type) {
        this.unaryOp = unaryOp;
        this.binaryOp = binaryOp;
        this.symbol = symbol;
        this.precedence = precedence;
        this.type = type;
    }


    /**
     * Creates a builder for a unary prefix operator.
     */
    public static OperatorBuilder prefix(String symbol, DoubleUnaryOperator unaryOp) {
        return unary(symbol, unaryOp, Type.PREFIX);
    }

    /**
     * Creates a builder for a unary postfix operator.
     */
    public static OperatorBuilder postfix(String symbol, DoubleUnaryOperator unaryOp) {
        return unary(symbol, unaryOp, Type.POSTFIX);
    }

    /**
     * Creates a builder for a left-associative binary operator.
     */
    public static OperatorBuilder infixl(String symbol, DoubleBinaryOperator binaryOp) {
        return binary(symbol, binaryOp, Type.INFIXL);
    }

    /**
     * Creates a builder for a non-associative binary operator.
     */
    public static OperatorBuilder infixn(String symbol, DoubleBinaryOperator binaryOp) {
        return binary(symbol, binaryOp, Type.INFIXN);
    }

    /**
     * Creates a builder for a right-associative binary operator.
     */
    public static OperatorBuilder infixr(String symbol, DoubleBinaryOperator binaryOp) {
        return binary(symbol, binaryOp, Type.INFIXR);
    }

    /**
     * Creates a builder for a unary operator of the given type.
     */
    private static OperatorBuilder unary(String symbol, DoubleUnaryOperator unaryOp, Type type) {
        if (unaryOp == null) {
            throw new IllegalArgumentException("operator argument must not be null");
        }
        if (type.arity() != 1) {
            throw new IllegalArgumentException("type argument must be PREFIX or POSTFIX");
        }
        // XXX ensure valid symbol
        return new OperatorBuilder(unaryOp, null, symbol, 90, type);
    }

    /**
     * Creates a builder for a binary operator of the given type.
     */
    private static OperatorBuilder binary(String symbol, DoubleBinaryOperator binaryOp, Type type) {
        if (binaryOp == null) {
            throw new IllegalArgumentException("operator argument must not be null");
        }
        if (type.arity() != 2) {
            throw new IllegalArgumentException("type argument must be INFIXL, INFIXN, or INFIXR");
        }
        // XXX ensure valid symbol
        return new OperatorBuilder(null, binaryOp, symbol, 10, type);
    }


    /**
     * Returns a new instance equivalent to this one, but with the given precedence.
     */
    public OperatorBuilder precedence(int precedence) {
        return new OperatorBuilder(this.unaryOp, this.binaryOp, this.symbol, precedence, this.type);
    }


    /**
     * Returns the symbol that will be used when formatting or parsing this operator.
     */
    @Override public String symbol() {
        return symbol;
    }

    /**
     * Returns the symbol that will be used when formatting or parsing this operator.
     */
    @Override public int precedence() {
        return precedence;
    }

    /**
     * Returns the type of operator: prefix, postfix, infix (and, if infix, whether
     * it is left-, right, or non-associative).
     */
    @Override public Type type() {
        return type;
    }


    @Override public double evaluate(double v) {
        return unaryOp.applyAsDouble(v);
    }

    @Override public String format(String s) {
        switch (type()) {
            case PREFIX:
                return String.format("%s%s", symbol, s);
            case POSTFIX:
                return String.format("%s%s", s, symbol);
            default:
                throw new IllegalStateException("Unary operator must be prefix or postfix");
        }
    }

    @Override public double evaluate(double v1, double v2) {
        return binaryOp.applyAsDouble(v1, v2);
    }

    @Override public String format(String s1, String s2) {
        switch (type()) {
            case INFIXL:
            case INFIXN:
            case INFIXR:
                return String.format("%s%s%s", s1, symbol, s2);
            default:
                throw new IllegalStateException("Binary operator must be infix");
        }
    }
}
