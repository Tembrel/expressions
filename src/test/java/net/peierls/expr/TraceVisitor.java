package net.peierls.expr;

import java.util.stream.Stream;

import one.util.streamex.StreamEx;
import one.util.streamex.EntryStream;

/**
 * Computes an indented trace of an expression.
 */
public class TraceVisitor implements Visitor<Stream<String>> {

    private static final TraceVisitor TRACE_VISITOR = new TraceVisitor(1);

    final int depth;

    TraceVisitor(int depth) {
        this.depth = depth;
    }

    TraceVisitor child() {
        return new TraceVisitor(depth + 1);
    }

    /**
     * Entry point to apply the trace transformation to an expression.
     */
    public static String trace(Expression expr) {
        return StreamEx.of(expr.accept(TRACE_VISITOR)).joining("\n");
    }


    @Override public Stream<String> visit(ConstantExpression expr) {
        return addLine("CONST " + expr.value());
    }

    @Override public Stream<String> visit(VariableExpression expr) {
        return addLine("VAR " + expr.varName());
    }

    @Override public Stream<String> visit(UnaryOpExpression expr) {
        UnaryOp op = expr.operator();
        Expression subExpr = expr.subExpression();
        return addLine(String.format("%s %s precedence=%s",
                op.type(), name(op), op.precedence()))
            .append(subExpr.accept(child()));
    }

    @Override public Stream<String> visit(BinaryOpExpression expr) {
        BinaryOp op = expr.operator();
        Expression leftExpr = expr.leftExpression();
        Expression rightExpr = expr.rightExpression();
        TraceVisitor child = child();
        return addLine(String.format("%s %s precedence=%s",
                op.type(), name(op), op.precedence()))
            .append(leftExpr.accept(child))
            .append(rightExpr.accept(child));
    }

    @Override public Stream<String> visit(LetExpression expr) {
        TraceVisitor child = child();
        return StreamEx.of(expr.subExpression().accept(this))
            .append(
                EntryStream.of(expr.bindings())
                    .flatMapKeyValue((varName, boundExpr) ->
                        addLine("where " + varName + " is bound to:")
                            .append(boundExpr.accept(child))
                    )
            );
    }

    @Override public Stream<String> visitUnknown(Expression expr) {
        return addLine("unknown");
    }


    private StreamEx<String> addLine(String s) {
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
