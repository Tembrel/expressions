package com.example.expr;

/**
 * A double precision operator.
 */
public interface Operator {

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
     * The precedence of this operator. Higher numbers
     * bind more tightly. The standard range of values
     * is 16 (for highest precedence) to 1 (lowest precedence).
     *
     * @see https://introcs.cs.princeton.edu/java/11precedence/
     */
    int precedence();


    /**
     * The arity of this operator.
     */
    default int arity() {
        if (this instanceof UnaryOperator) {
            return 1;
        }
        if (this instanceof BinaryOperator) {
            return 2;
        }
        throw new IllegalStateException("Unknown arity operator");
    }


    default boolean isLeftToRight() { return associativity() == Associativity.LEFT_TO_RIGHT; }
    default boolean isRightToLeft() { return associativity() == Associativity.RIGHT_TO_LEFT; }
    default boolean isPrefix() { return fixity() == Fixity.PREFIX; }
    default boolean isPostfix() { return fixity() == Fixity.POSTFIX; }
    default boolean isInfix() { return fixity() == Fixity.INFIX; }
}
