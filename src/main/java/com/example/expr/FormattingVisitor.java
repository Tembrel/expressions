package com.example.expr;

import one.util.streamex.EntryStream;


class FormattingVisitor implements Visitor<String> {

    private static final FormattingVisitor FORMAT_VISITOR = new FormattingVisitor();

    public static FormattingVisitor instance() {
        return FORMAT_VISITOR;
    }

    @Override public String visit(ConstantExpression expr) {
        return formatDouble(expr.value());
    }

    @Override public String visit(VariableExpression expr) {
        return expr.varName();
    }

    @Override public String visit(UnaryOperationExpression expr) {
        return expr.operator().format(expr.subExpression().accept(this));
    }

    @Override public String visit(BinaryOperationExpression expr) {
        return expr.operator().format(
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
