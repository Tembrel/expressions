package net.peierls.expr;

/**
 * Thrown when expression parsing fails for some reason.
 * @impl.note The cause is the underlying JParsec {@code ParserException}.
 */
public class ExpressionParsingException extends RuntimeException {
    ExpressionParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
