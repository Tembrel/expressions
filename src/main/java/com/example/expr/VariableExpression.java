package com.example.expr;

import java.util.Objects;

import java.util.regex.Pattern;


/**
 * An expression consisting of a single variable.
 */
public class VariableExpression extends Expression {

    private static final Pattern VALID_VAR_NAME = Pattern.compile("[A-Za-z][A-Za-z0-9_]*");
    private final String varName;

    VariableExpression(String varName) {
        if (!VALID_VAR_NAME.matcher(varName).matches()) {
            throw new IllegalArgumentException(String.format(
                "Bad variable name '%s': must start with letter then 0+ letters/digits/underscores",
                varName
            ));
        }
        this.varName = varName;
    }

    /** The name of the variable represented by this variable expression. */
    public String varName() {
        return varName;
    }

    @Override protected <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(this instanceof VariableExpression)) return false;
        String otherVarName = ((VariableExpression) obj).varName;
        return varName.equals(otherVarName);
    }

    @Override public int hashCode() {
        return Objects.hash(varName);
    }
}