package net.peierls.expr;

import static net.peierls.expr.Expression.*;

import java.lang.reflect.InvocationTargetException;


/**
 * Abstract base for expression subtypes that wrap existing
 * expressions to provide new methods.
 */
public abstract class ExtendedExpression<T extends ExtendedExpression<T>> extends Expression {

    private final Expression delegate;
    private final Class<T> type;

    ExtendedExpression(Expression delegate, Class<T> type) {
        this.delegate = delegate;
        this.type = type;
    }

    /**
     * Returns an extended expression equivalent to the
     * given expression, but supporting extended operations.
     */
    public static <T extends ExtendedExpression<T>> T wrap(Expression expr, Class<T> type) {
        if (type.isInstance(expr)) {
            return type.cast(expr);
        } else {
            try {
                return type.getConstructor(Expression.class)
                    .newInstance(expr);
            } catch (NoSuchMethodException  | InstantiationException
                   | IllegalAccessException | InvocationTargetException ex) {
                throw new WrapException(
                    String.format("Couldn't wrap expression with %s", type),
                    ex
                );
            }
        }
    }

    /**
     * Returns an extended expression representing the given constant.
     */
    public static <T extends ExtendedExpression<T>> T wrap(double val, Class<T> type) {
        return wrap(expr(val), type);
    }

    /**
     * Returns an extended expression representing the
     * given variable.
     */
    public static <T extends ExtendedExpression<T>> T wrap(String varName, Class<T> type) {
        return wrap(expr(varName), type);
    }


    @Override protected <R> R accept(Visitor<R> visitor) {
        return delegate.accept(visitor);
    }


    /**
     * Returns an extended expression representing the result of
     * applying the given unary operation to this expression.
     */
    @Override public T apply(UnaryOp op) {
        return wrap(delegate.apply(op), type);
    }

    /**
     * Returns an extended expression representing the result of
     * applying the given binary operation to this expression.
     */
    @Override public T apply(BinaryOp op, Expression that) {
        return wrap(delegate.apply(op, that), type);
    }
}
