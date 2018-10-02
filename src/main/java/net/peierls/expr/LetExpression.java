package net.peierls.expr;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Objects;


/**
 * An expression representing a subexpression with variable names
 * bound to other expressions.
 */
public class LetExpression extends Expression {
    private final Expression expr;
    private final ImmutableMap<String, Expression> bindings;

    LetExpression(Expression expr, Map<String, Expression> bindings) {
        this.bindings = ImmutableMap.copyOf(bindings);
        this.expr = expr;
    }

    /** The subexpression bound in this expression. */
    public Expression subExpression() {
        return expr;
    }

    /** The local bindings of this bound expression. */
    public Map<String, Expression> bindings() {
        return bindings;
    }

    @Override protected <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(this instanceof LetExpression)) return false;
        LetExpression that = (LetExpression) obj;
        return this.bindings.equals(that.bindings)
            && this.expr.equals(that.expr);
    }

    @Override public int hashCode() {
        return Objects.hash(bindings, expr);
    }
}

