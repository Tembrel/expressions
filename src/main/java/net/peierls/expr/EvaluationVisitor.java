package net.peierls.expr;

import com.google.common.collect.ImmutableMap;

import java.util.Map;


/** Visitor used to implement evaluation. */
class EvaluationVisitor implements Visitor<Double> {

    private static final EvaluationVisitor EVALUATE_VISITOR = new EvaluationVisitor();

    final ImmutableMap<String, Expression> bindings;
    final EvaluationVisitor parent;

    EvaluationVisitor() {
        this.bindings = ImmutableMap.of();
        this.parent = null;
    }

    public static EvaluationVisitor instance() {
        return EVALUATE_VISITOR;
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

    @Override public Double visit(UnaryOpExpression expr) {
        return expr.operator().evaluate(expr.subExpression().accept(this));
    }

    @Override public Double visit(BinaryOpExpression expr) {
        return expr.operator().evaluate(
            expr.leftExpression().accept(this),
            expr.rightExpression().accept(this)
        );
    }

    @Override public Double visit(LetExpression expr) {
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
