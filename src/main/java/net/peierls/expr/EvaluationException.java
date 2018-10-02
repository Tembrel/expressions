package net.peierls.expr;

/** Base for exceptions thrown during expression evaluation */
public class EvaluationException extends RuntimeException {
    protected EvaluationException(String message) {
        super(message);
    }
}
