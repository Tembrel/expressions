package net.peierls.expr;

/**
 * Base for exceptions thrown during expression evaluation.
 */
public class WrapException extends RuntimeException {
    protected WrapException(String message, Throwable cause) {
        super(message, cause);
    }
}