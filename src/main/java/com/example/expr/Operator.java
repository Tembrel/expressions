package com.example.expr;

/**
 * A double precision operator with arity, precedence, fixity, and associativity.
 */
public interface Operator {

    /**
     * The arity of this operator.
     */
    default int arity() {
        if (this instanceof UnaryOp) {
            return 1;
        }
        if (this instanceof BinaryOp) {
            return 2;
        }
        throw new IllegalStateException("Unknown arity operator");
    }


    /**
     * The precedence of this operator. Higher numbers bind more tightly.
     */
    int precedence();


    /**
     * Where the operator is place in relation to the argument(s).
     */
    enum Fixity {
        INFIX,
        PREFIX,
        POSTFIX,
    }

    /**
     * The fixity of this operator.
     */
    default Fixity fixity() {
        switch (arity()) {
            case 1:
            default:
                return Fixity.PREFIX;
            case 2:
                return Fixity.INFIX;
        }
    }


    /**
     * Describes how an operation associates within an expression
     * in the absence of parentheses.
     */
    enum Associativity {
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT,
        NOT_ASSOCIATIVE,
    }

    /**
     * The associativity of this operator.
     */
    default Associativity associativity() {
        switch (arity()) {
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
    }


    /**
     * Convenience method to test if this is a prefix operator,
     * equivalent to {@code fixity() == PREFIX}.
     */
    default boolean isPrefix() { return fixity() == Fixity.PREFIX; }

    /**
     * Convenience method to test if this is a postfix operator,
     * equivalent to {@code fixity() == POSTFIX}.
     */
    default boolean isPostfix() { return fixity() == Fixity.POSTFIX; }

    /**
     * Convenience method to test if this is an infix operator,
     * equivalent to {@code fixity() == INFIX}.
     */
    default boolean isInfix() { return fixity() == Fixity.INFIX; }

    /**
     * Convenience method to test for left-to-right associativity,
     * equivalent to {@code associativity() == LEFT_TO_RIGHT}.
     */
    default boolean isLeftToRight() { return associativity() == Associativity.LEFT_TO_RIGHT; }

    /**
     * Convenience method to test for right-to-left associativity,
     * equivalent to {@code associativity() == RIGHT_TO_LEFT}.
     */
    default boolean isRightToLeft() { return associativity() == Associativity.RIGHT_TO_LEFT; }
}
