package com.example.expr;

/** Base for exceptions thrown during expression evaluation */
public class EvaluationException extends RuntimeException {
    EvaluationException(String message) {
        super(message);
    }
}
