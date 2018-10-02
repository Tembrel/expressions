package net.peierls.expr;


/**
 * A double precision operator with arity, precedence, fixity, and associativity.
 */
public interface Operator {

    /**
     * The text symbol for this operator.
     */
    String symbol();


    /**
     * The type of operator, whether it is pre-, post-, or infix, and
     * if the kind of associativity if infix.
     */
    enum Type {
        PREFIX(1),
        POSTFIX(1),
        INFIXL(2),
        INFIXN(2),
        INFIXR(2),
        ;
        final int arity;
        Type(int arity) {
            this.arity = arity;
        }
        int arity() { return arity; }
    }

    Type type();


    /**
     * The precedence of this operator. Higher numbers bind more tightly.
     */
    int precedence();


    /**
     * Convenience method to test if this is a prefix operator.
     */
    default boolean isPrefix() { return type() == Type.PREFIX; }

    /**
     * Convenience method to test if this is a postfix operator.
     */
    default boolean isPostfix() { return type() == Type.POSTFIX; }

    /**
     * Convenience method to test if this is an infix operator.
     */
    default boolean isInfix() { return type() == Type.INFIXL || type() == Type.INFIXN || type() == Type.INFIXR; }

    /**
     * Convenience method to test for left-to-right associativity.
     */
    default boolean isLeftToRight() { return type() == Type.INFIXL; }

    /**
     * Convenience method to test for right-to-left associativity.
     */
    default boolean isRightToLeft() { return type() == Type.INFIXR; }
}
