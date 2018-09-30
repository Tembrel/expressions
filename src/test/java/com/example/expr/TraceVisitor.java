package com.example.expr;

import java.util.stream.Stream;

import one.util.streamex.StreamEx;
import one.util.streamex.EntryStream;


public class TraceVisitor implements Visitor<Stream<String>> {

    private static final TraceVisitor TRACE_VISITOR = new TraceVisitor(1);

    final int depth;

    TraceVisitor(int depth) {
        this.depth = depth;
    }

    TraceVisitor child() {
        return new TraceVisitor(depth + 1);
    }

    public static String trace(Expression expr) {
        return StreamEx.of(expr.accept(TRACE_VISITOR)).joining("\n");
    }

    @Override public Stream<String> visit(ConstantExpression expr) {
        return add("constant " + expr.value());
    }

    @Override public Stream<String> visit(VariableExpression expr) {
        return add("variable " + expr.varName());
    }

    @Override public Stream<String> visit(UnaryOperationExpression expr) {
        UnaryOp op = expr.operator();
        Expression subExpr = expr.subExpression();
        return add(String.format("unary %s assoc=%s fix=%s prec=%s arity=%s",
                name(op), op.associativity(), op.fixity(), op.precedence(), op.arity()))
            .append(subExpr.accept(child()));
    }

    @Override public Stream<String> visit(BinaryOperationExpression expr) {
        BinaryOp op = expr.operator();
        Expression leftExpr = expr.leftExpression();
        Expression rightExpr = expr.rightExpression();
        TraceVisitor child = child();
        return add(String.format("binary %s assoc=%s fix=%s prec=%s arity=%s",
                name(op), op.associativity(), op.fixity(), op.precedence(), op.arity()))
            .append(leftExpr.accept(child))
            .append(rightExpr.accept(child));
    }

    @Override public Stream<String> visit(BoundExpression expr) {
        TraceVisitor child = child();
        return StreamEx.of(expr.subExpression().accept(this))
            .append(
                EntryStream.of(expr.bindings())
                    .flatMapKeyValue((varName, boundExpr) ->
                        add("where " + varName + " is bound to:")
                            .append(boundExpr.accept(child))
                    )
            );
    }

    @Override public Stream<String> visitUnknown(Expression expr) {
        return add("unknown");
    }

    private StreamEx<String> add(String s) {
        return StreamEx.of(prefix() + s);
    }

    private String prefix() {
        return StreamEx.generate(() -> "  ").limit(depth).joining();
    }

    private String name(Operator op) {
        if (op instanceof Enum) {
            return ((Enum) op).name();
        } else {
            return op.getClass().getSimpleName();
        }
    }
}
