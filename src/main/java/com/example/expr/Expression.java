package com.example.expr;

import static com.example.expr.Expression.BasicBinaryOperation.*;
import static com.example.expr.Expression.BasicUnaryOperation.*;

import com.google.common.collect.*;

import java.util.*;
import java.util.function.*;
import java.util.regex.Pattern;
import java.util.stream.*;

import one.util.streamex.EntryStream;

/**
 * A double-precision arithmetic expression on constants, variables,
 * unary and binary operations, with local binding of variables to
 * subexpressions, supporting evaluation and formatting (string
 * representation).
 */
public abstract class Expression {

    /**
     * Visitor interface for computing values from known expression subtypes.
     * Traversal of composite expression is accomplished by calling accept
     * on subexpressions within visit method implementations.
     */
    public interface Visitor<R> {
        R visit(ConstantExpression expr);
        R visit(VariableExpression expr);
        R visit(UnaryOperationExpression expr);
        R visit(BinaryOperationExpression expr);
        R visit(BoundExpression expr);
        R visitUnknown(Expression expr);
    }

    /**
     * Apply a visitor function to this expression.
     * The default implementation is to call the {@link Visitor#visitUnknown}
     * method, but subclasses should override to call the visit method on one
     * of the five concrete subclasses known to the visitor interface.
     */
    protected <R> R accept(Visitor<R> visitor) {
        return visitor.visitUnknown(this);
    }


    /**
     * Returns the string representation of this expression.
     */
    @Override public String toString() {
        return format();
    }

    /**
     * Turn a string representation of an expression into an Expression instance.
     */
    //public static Expression valueOf(String exprString) {
    //    throw new UnsupportedOperationException("Parsing not yet supported");
    //}


    /**
     * Returns a constant expression with the given value.
     */
    public static ConstantExpression expr(double value) {
        return new ConstantExpression(value);
    }

    /**
     * Returns a variable expression with the given variable name.
     */
    public static VariableExpression expr(String varName) {
        return new VariableExpression(varName);
    }

    /**
     * Returns a unary operation expression on the given operation and subexpression.
     */
    public Expression apply(UnaryOperation operation) {
        return new UnaryOperationExpression(operation, this);
    }

    /**
     * Returns a binary operation expression on the given operation and left and right subexpressions.
     */
    public Expression apply(BinaryOperation operation, Expression right) {
        return new BinaryOperationExpression(operation, this, right);
    }

    /**
     * Returns an expression equivalent to this expression but with
     * the named variable bound to the given subexpression.
     */
    public final BoundExpression where(String varName, Expression subExpr) {
        return new BoundExpression(this, ImmutableMap.of(varName, subExpr));
    }

    /**
     * Returns an expression equivalent to this expression but with
     * the named variables bound to the given subexpressions.
     */
    public final BoundExpression where(String v1, Expression e1, String v2, Expression e2) {
        return new BoundExpression(this, ImmutableMap.of(v1, e1, v2, e2));
    }

    /**
     * Returns an expression equivalent to this expression but with
     * the named variables bound to the given subexpressions.
     */
    public final BoundExpression where(String v1, Expression e1, String v2, Expression e2, String v3, Expression e3) {
        return new BoundExpression(this, ImmutableMap.of(v1, e1, v2, e2, v3, e3));
    }

    /**
     * Returns an expression equivalent to this expression but with
     * the named variable bound to the given double precision value.
     */
    public final BoundExpression where(String varName, double value) {
        return new BoundExpression(this, ImmutableMap.of(varName, expr(value)));
    }

    /**
     * Returns an expression equivalent to this expression but with
     * the named variables bound to the given double precision values.
     */
    public final BoundExpression where(String v1, double d1, String v2, double d2) {
        return new BoundExpression(this, ImmutableMap.of(v1, expr(d1), v2, expr(d2)));
    }

    /**
     * Returns an expression equivalent to this expression but with
     * the named variables bound to the given double precision values.
     */
    public final BoundExpression where(String v1, double d1, String v2, double d2, String v3, double d3) {
        return new BoundExpression(this, ImmutableMap.of(v1, expr(d1), v2, expr(d2), v3, expr(d3)));
    }

    /**
     * Returns an expression equivalent to this expression but with
     * the bindings of the given map of variable names to subexpressions.
     */
    public final BoundExpression where(Map<String, Expression> bindings) {
        return new BoundExpression(this, bindings);
    }


    //
    // Direct support for some unary and binary operations.
    //

    public final Expression plus(Expression that) {
        return apply(PLUS, that);
    }

    public final Expression minus(Expression that) {
        return apply(MINUS, that);
    }

    public final Expression times(Expression that) {
        return apply(TIMES, that);
    }

    public final Expression dividedBy(Expression that) {
        return apply(DIVIDED_BY, that);
    }

    public final Expression negated() {
        return apply(NEGATED);
    }

    public final Expression squared() {
        return apply(SQUARED);
    }

    public final Expression squareRoot() {
        return apply(SQUARE_ROOT);
    }


    //
    // Evaluation method, visitor implementation, and exception types
    //

    public final double evaluate() {
        return accept(EVALUATE_VISITOR);
    }

    private static final EvaluationVisitor EVALUATE_VISITOR = new EvaluationVisitor();

    private static class EvaluationVisitor implements Visitor<Double> {
        final ImmutableMap<String, Expression> bindings;
        final EvaluationVisitor parent;

        EvaluationVisitor() {
            this.bindings = ImmutableMap.of();
            this.parent = null;
        }

        EvaluationVisitor(Map<String, Expression> bindings, EvaluationVisitor parent) {
            this.bindings = ImmutableMap.copyOf(bindings);
            this.parent = parent;
        }

        @Override public Double visit(ConstantExpression expr) {
            return expr.value();
        }

        @Override public Double visit(VariableExpression expr) {
            String varName = expr.varName();
            for (EvaluationVisitor visitor = this; visitor != null; visitor = visitor.parent) {
                Expression result = visitor.bindings.get(varName);
                if (result != null) {
                    if (visitor.parent == null) {
                        throw new IllegalStateException("Unexpected non-empty root evaluation context");
                    }
                    return result.accept(visitor.parent);
                }
            }
            throw new UnboundVariableException(varName);
        }

        @Override public Double visit(UnaryOperationExpression expr) {
            return expr.operation().evaluate(expr.subExpression().accept(this));
        }

        @Override public Double visit(BinaryOperationExpression expr) {
            return expr.operation().evaluate(
                expr.leftExpression().accept(this),
                expr.rightExpression().accept(this)
            );
        }

        @Override public Double visit(BoundExpression expr) {
            Expression subExpr = expr.subExpression();
            return subExpr.accept(child(expr.bindings()));
        }

        @Override public Double visitUnknown(Expression expr) {
            return Double.NaN;
        }

        EvaluationVisitor child(Map<String, Expression> bindings) {
            return new EvaluationVisitor(bindings, this);
        }
    }

    /** Base for exceptions thrown during expression evaluation */
    public static class EvaluationException extends RuntimeException {
        protected EvaluationException(String message) {
            super(message);
        }
    }

    /** Thrown on a division by zero during evaluation */
    public static class DivisionByZeroException extends EvaluationException {
        final double numerator;
        final double denominator;
        DivisionByZeroException(double numerator, double denominator) {
            super(String.format("Division by zero or NaN during evaluation: %f/%f", numerator, denominator));
            this.numerator = numerator;
            this.denominator = denominator;
        }
        public double numerator() { return numerator; }
        public double denominator() { return denominator; }
    }

    /** Thrown when a variable is not bound to a value during evaluation */
    public static class UnboundVariableException extends EvaluationException {
        private final String varName;

        UnboundVariableException(String varName) {
            super(String.format("Unbound variable during evaluation: %s", varName));
            this.varName = varName;
        }

        public String unboundVariable() {
            return varName;
        }
    }


    //
    // Format method and visitor implementation
    //

    public final String format() {
        return accept(FORMAT_VISITOR);
    }

    private static final FormattingVisitor FORMAT_VISITOR = new FormattingVisitor();


    private static class FormattingVisitor implements Visitor<String> {
        @Override public String visit(ConstantExpression expr) {
            return formatDouble(expr.value());
        }

        @Override public String visit(VariableExpression expr) {
            return expr.varName();
        }

        @Override public String visit(UnaryOperationExpression expr) {
            return expr.operation().format(expr.subExpression().accept(this));
        }

        @Override public String visit(BinaryOperationExpression expr) {
            return expr.operation().format(
                expr.leftExpression().accept(this),
                expr.rightExpression().accept(this)
            );
        }

        @Override public String visit(BoundExpression expr) {
            String bindStr = EntryStream.of(expr.bindings())
                .mapKeyValue((varName, boundExpr) ->
                    String.format("%s = %s", varName, boundExpr.accept(this)))
                .joining(", ");
            String str = expr.subExpression().accept(this);
            return String.format("[let %s in %s]", bindStr, str);
        }

        @Override public String visitUnknown(Expression expr) {
            return formatDouble(Double.NaN);
        }

        private static String formatDouble(double value) {
            return Double.toString(value).replaceFirst("\\.0", "");
        }
    }

    //
    // Expression subtypes
    //

    /**
     * An expression consisting of a constant value.
     */
    public static class ConstantExpression extends Expression {
        private final double value;

        ConstantExpression(double value) {
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("constant expressions must be finite");
            }
            this.value = value;
        }

        /** The double precision value represented by this constant expression. */
        public double value() {
            return value;
        }

        @Override protected <R> R accept(Visitor<R> visitor) {
            return visitor.visit(this);
        }

        @Override public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(this instanceof ConstantExpression)) return false;
            double otherValue = ((ConstantExpression) obj).value;
            return Objects.equals(value, otherValue);
        }

        @Override public int hashCode() {
            return Objects.hash(value);
        }
    }


    /**
     * An expression consisting of a single variable.
     */
    public static class VariableExpression extends Expression {

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


    //
    // Unary operation interface, enum type, and expression subtype
    //

    /**
     * A double precision unary operation, supporting evaluation and
     * formatting (string representation).
     * This is an extensible enum pattern interface, and should only be
     * implemented by enum types to ensure that equality can be tested
     * using identity.
     */
    public interface UnaryOperation {
        /**
         * Apply this operation to a double value.
         */
        double evaluate(double v);

        /**
         * Find the string representation of this operation given a string
         * representation of its argument.
         */
        String format(String s);
    }

    @SuppressWarnings("ImmutableEnumChecker")
    public enum BasicUnaryOperation implements UnaryOperation {
        NEGATED(a -> -a, "-%s"),
        SQUARED(a -> a * a, "%s^2"),
        SQUARE_ROOT(Math::sqrt, "sqrt(%s)"),
        ;
        final DoubleUnaryOperator op;
        final String fmt;
        BasicUnaryOperation(DoubleUnaryOperator op, String fmt) {
            this.op = op;
            this.fmt = fmt;
        }
        @Override public double evaluate(double v) { return op.applyAsDouble(v); }
        @Override public String format(String s) { return String.format(fmt, s); }
    }

    /**
     * An expression representing the result of a unary operation on a subexpression.
     */
    public static class UnaryOperationExpression extends Expression {
        private final UnaryOperation operation;
        private final Expression expr;

        UnaryOperationExpression(UnaryOperation operation, Expression expr) {
            this.operation = operation;
            this.expr = expr;
        }

        /** The unary operation used in thie expression. */
        public UnaryOperation operation() {
            return operation;
        }

        /** The argument of this expression's unary operation. */
        public Expression subExpression() {
            return expr;
        }

        @Override protected <R> R accept(Visitor<R> visitor) {
            return visitor.visit(this);
        }

        @Override public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(this instanceof UnaryOperationExpression)) return false;
            UnaryOperationExpression that = (UnaryOperationExpression) obj;
            return this.operation == that.operation
                && this.expr.equals(that.expr);
        }

        @Override public int hashCode() {
            return Objects.hash(operation, expr);
        }
    }


    //
    // Binary operation interface, enum type, and expression subtype
    //

    /**
     * A double precision binary operation, supporting evaluation and
     * formatting (string representation).
     * This is an extensible enum pattern interface, and should only be
     * implemented by enum types to ensure that equality can be tested
     * using identity.
     */
    public interface BinaryOperation {
        /**
         * Apply this operation to a double value.
         */
        double evaluate(double v1, double v2);

        /**
         * Find the string representation of this operation given a string
         * representation of its arguments.
         */
        String format(String s1, String s2);
    }


    @SuppressWarnings("ImmutableEnumChecker")
    public enum BasicBinaryOperation implements BinaryOperation {
        PLUS((a, b) -> a + b, "(%s + %s)"),
        MINUS((a, b) -> a - b, "(%s - %s)"),
        TIMES((a, b) -> a * b, "%s %s"),
        DIVIDED_BY((a, b) -> divide(a, b), "(%s / %s)"),
        ;
        final DoubleBinaryOperator op;
        final String fmt;
        BasicBinaryOperation(DoubleBinaryOperator op, String fmt) {
            this.op = op;
            this.fmt = fmt;
        }
        @Override public double evaluate(double v1, double v2) { return op.applyAsDouble(v1, v2); }
        @Override public String format(String s1, String s2) { return String.format(fmt, s1, s2); }

        private static double divide(double a, double b) {
            double c = a / b;
            if (Double.isFinite(c)) {
                return c;
            }
            throw new DivisionByZeroException(a, b);
        }
    }

    /**
     * An expression representing the result of a binary operation on two subexpressions.
     */
    public static class BinaryOperationExpression extends Expression {
        private final BinaryOperation operation;
        private final Expression left;
        private final Expression right;

        BinaryOperationExpression(BinaryOperation operation, Expression left, Expression right) {
            this.operation = operation;
            this.left = left;
            this.right = right;
        }

        /** The binary operation used in thie expression. */
        public BinaryOperation operation() {
            return operation;
        }

        /** The left argument of this expression's binary operation. */
        public Expression leftExpression() {
            return left;
        }

        /** The right argument of this expression's binary operation. */
        public Expression rightExpression() {
            return right;
        }

        @Override protected <R> R accept(Visitor<R> visitor) {
            return visitor.visit(this);
        }

        @Override public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(this instanceof BinaryOperationExpression)) return false;
            BinaryOperationExpression that = (BinaryOperationExpression) obj;
            return this.operation == that.operation
                && this.left.equals(that.left)
                && this.right.equals(that.right);
        }

        @Override public int hashCode() {
            return Objects.hash(operation, left, right);
        }
    }


    /**
     * An expression representing a subexpression with variable names
     * bound to other expressions.
     */
    public static class BoundExpression extends Expression {
        private final Expression expr;
        private final ImmutableMap<String, Expression> bindings;

        BoundExpression(Expression expr, Map<String, Expression> bindings) {
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
    }

}
