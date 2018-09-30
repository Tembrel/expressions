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
        UnaryOp op = expr.operator();
        Expression subExpr = expr.subExpression();
        String subFmt = subExpr.accept(this);
        if (subExpr instanceof OperationExpression) {
            Operator subOp = ((OperationExpression) subExpr).operator();
            if (op.isPrefix()) {
                if (op.isLeftToRight()) {
                    if (subOp.precedence() > op.precedence()) {
                        subFmt = String.format("(%s)", subFmt);
                    }
                }
            } else if (op.isPostfix()) {
                if (op.isRightToLeft()) {
                    if (subOp.precedence() > op.precedence()) {
                        subFmt = String.format("(%s)", subFmt);
                    }
                }
            }
        }
        return op.format(subFmt);
    }

    @Override public String visit(BinaryOperationExpression expr) {
        BinaryOp op = expr.operator();
        Expression leftExpr = expr.leftExpression();
        Expression rightExpr = expr.rightExpression();
        String leftFmt = leftExpr.accept(this);
        String rightFmt = rightExpr.accept(this);
        if (op.isInfix()) {
            if (leftExpr instanceof OperationExpression) {
                Operator leftOp = ((OperationExpression) leftExpr).operator();
                if (op.isLeftToRight()) {
                    if (leftOp.precedence() < op.precedence()) {
                        leftFmt = String.format("(%s)", leftFmt);
                    }
                } else if (op.isRightToLeft()) {
                    if (leftOp.precedence() > op.precedence()) {
                        leftFmt = String.format("(%s)", leftFmt);
                    }
                }
            }
            if (rightExpr instanceof OperationExpression) {
                Operator rightOp = ((OperationExpression) rightExpr).operator();
                if (op.isRightToLeft()) {
                    if (rightOp.precedence() > op.precedence()) {
                        rightFmt = String.format("(%s)", rightFmt);
                    }
                } else if (op.isLeftToRight()) {
                    if (rightOp.precedence() < op.precedence()) {
                        rightFmt = String.format("(%s)", rightFmt);
                    }
                }
            }
        }
        return op.format(leftFmt, rightFmt);
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
