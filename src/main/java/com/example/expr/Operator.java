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
        return Associativity.LEFT_TO_RIGHT;
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
    Fixity fixity();


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
    int arity();
}
