/**
 * This package provides an extensible framework for building, evaluating, formatting, and parsing
 * double-precision expressions: constants, variables, unary and binary operations,
 * and binding of variables.
 *
 * <h2>Usage</h2>
 *
 * The code snippets below assume the following static import:
 * <pre>
 * import static net.peierls.expr.Expression.expr;
 * </pre>
 *
 * <h3>Atomic expressions</h3>
 *
 * Use the static {@code expr} methods to create atomic expressions.
 * <pre>
 * // Constant expressions
 * ConstantExpression constExpr = expr(2.5);
 * assertEquals(2.5, constExpr.value(), 0);
 *
 * // Variable expressions
 * VariableExpression varExpr = expr("a");
 * assertEquals("a", varExpr.varName());
 * </pre>
 *
 * <h3>Operations</h3>
 *
 * Use the {@code apply} methods to build
 * unary and binary operations, or use the equivalent
 * built-in shortcut methods, which have overloadings
 * to accept {@code double} or {@code String} arguments in place of
 * {@code Expression}.
 * <pre>
 * Expression sumExpr1 = expr(2.5).apply(BasicOperator.PLUS, expr("a"));
 * Expression sumExpr2 = expr(2.5).plus("a"); // more compact and readable
 *
 * assertEquals(sumExpr1, sumExpr2);
 * </pre>
 *
 * <h4>Special rule for multiplication</h4>
 *
 * The built-in multiplication operator is formatted as whitespace;
 * the parser will accept either whitespace or an asterisk, e.g., {@code a * b}.
 *
 * <h3>Bound expressions</h3>
 *
 * These are expressions binding variables in an expression to
 * subexpression, analogous to these notations:
 * <pre>
 * let x = ..., y = ... in ...
 * // or
 * ... where x = ..., y = ...
 * </pre>
 *
 * The {@code where} methods create bound expressions.
 * <pre>
 * Expression quadratic = expr("a").times(expr("x").squared())
 *     .plus(expr("b").times("x"))
 *     .plus("c");
 * Expression quadWithBoundCoefficients = quadratic
 *     .where("a", 2, "b", 3, "c", 4);
 * </pre>
 * For more than three bindings, use
 * {@link net.peierls.expr.Expression#where(Map) where(Map&lt;String, Expression&gt;)}.
 * <pre>
 * Map&lt;String, Expression&gt; bindings = new HashMap&lt;&gt;();
 * bindings.put("a", expr(2));
 * bindings.put("b", expr(3));
 * bindings.put("c", expr(4));
 * bindings.put("x", expr(1));
 * Expression quadFullyBound = quadratic.where(bindings);
 *
 * // Similarly, but not equivalently:
 * Expression quadFullyBound2 = quadWithBoundCoefficients.where("x", 1);
 *
 * assertFalse(quadFullyBound.equals(quadFullyBound2));
 * assertEquals(quadFullyBound.value(), quadFullyBound2.value(), 0);
 * </pre>
 *
 * <h3>Free variables</h3>
 *
 * The {@link net.peierls.expr.Expression#freeVariables freeVariables} method returns a set
 * of variables that are <strong>not</strong> bound in the expression.
 * <pre>
 * assertTrue(constExpr.freeVariables().isEmpty());
 * assertEquals(ImmutableSet.of("x"), quadWithBoundCoefficients.freeVariables());
 * </pre>
 *
 * <h3>Evaluation</h3>
 *
 * Calling {@link net.peierls.expr.Expression#value value} on an expression with no free variables
 * throws {@link net.peierls.expr.DivisionByZeroException} if a division by zero is encountered during
 * the evaluation, otherwise it produces the value of the expression as a {@code double}.
 *
 * Evaluating an expression with free variables throws {@link net.peierls.expr.UnboundVariableException}.
 * You can test whether an expression has no free variables with
 * {@link net.peierls.expr.Expression#evaluable evaluable}.
 *
 * The {@link net.peierls.expr.Expression#optValue optValue} method does not throw these exceptions,
 * but rather returns an empty value in those cases.
 * <pre>
 * assertTrue(quadFullyBound.evaluable());
 * assertEquals(9.0, quadFullyBound.value(), 0);
 * assertEquals(42.0, expr(1).dividedBy(0).optValue().orElse(42));
 * assertEquals(66.6, expr(1).plus("a").optValue().orElse(66.6));
 * </pre>
 *
 * <h3>String representation</h3>
 *
 * Format an expression to a string representation with
 * {@link net.peierls.expr.Expression#toString toString}.
 * <pre>
 * assertEquals("2.5 + a", sumExpr1.toString());
 * assertEquals("let a = 3 in 2.5 + a", sumExpr1.where("a", 3).toString());
 * </pre>
 *
 * <h3>Parsing</h3>
 *
 * Parse expressions on the built-in operators with
 * {@link net.peierls.expr.Expression#parseExpr parseExpr(String exprText)}.
 * <pre>
 * assertEquals(sumExpr1, parseExpr("2.5 + a"));
 * assertEquals(sumExpr1.where("a", 3), parseExpr("let a = 3 in 2.5 + a"));
 * </pre>
 *
 * <h2>Adding operators</h2>
 *
 * To add user-defined operators, define an enum type implementing
 * {@link net.peierls.expr.DelegatingOperator DelegatingOperator},
 * using methods of {@link net.peierls.expr.OperatorBuilder}
 * to define the type of operator (prefix, postfix, infix left-associative,
 * infix non-associative, or infix right-associative), the symbol used
 * when formatting or parsing the operator, the
 * {@linkplain java.util.function.DoubleUnaryOperator unary} or
 * {@linkplain java.util.function.DoubleBinaryOperator binary} Java
 * function used to evaluate the operator, and the precedence of the
 * operator for parsing.
 * <pre>
 * public enum MyOp implements DelegatingOperator {
 *     // Space at end of symbol used when formatting, but ignored by parser.
 *     PI_R_SQUARED(prefix("pi_r_2 ", v -> Math.PI * v * v).precedence(100)),
 *     ;
 *     final Operator delegate;
 *     MyOp(Operator delegate) { this.delegate = delegate; }
 *    {@literal @}Override public Operator delegate() { return delegate; }
 *
 *     // Convenience method
 *     public static final UnaryOpExpression piRSquared(Expression e) {
 *         return e.apply(PI_R_SQUARED);
 *     }
 * }
 * </pre>
 * To create an extended expression type that has an instance method for
 * user-defined operators, extend
 * {@link net.peierls.expr.ExtendedExpression ExtendedExpression}.
 * <pre>
 * static class MyExpression extends ExtendedExpression&lt;MyExpression&gt; {
 *     // Constructor, required
 *     public MyExpression(Expression expr) {
 *         super(expr, MyExpression.class);
 *     }
 *     // Static factory, recommended
 *     public static MyExpression myExpr(Expression expr) {
 *         return wrap(expr, MyExpression.class);
 *     }
 *     // Convenience static factory, recommended
 *     public static MyExpression myExpr(double val) {
 *         return wrap(val, MyExpression.class);
 *     }
 *     // Convenience static factory, recommended
 *     public static MyExpression myExpr(String var) {
 *         return wrap(var, MyExpression.class);
 *     }
 *     // Operation instance method, the reason for extending
 *     public final MyExpression piRSquared() {
 *         return apply(MyOp.PI_R_SQUARED);
 *     }
 * }
 * </pre>
 * To parse expressions on user-defined operators, pass the operator enum type(s) to
 * {@link net.peierls.expr.Expression#parseExpr(String,Class,Class[]) parseExpr(str, userType1,...)}.
 * <pre>
 * Expression expected1 = expr(2).apply(MyOp.PI_R_SQUARED);
 * assertEquals(expected1, parseExpr("pi_r_2 2", MyOp.class));
 *
 * Expression expected2 = expr(2).apply(MyOp.CUBED);
 * assertEquals(expected2, parseExpr("2^^^", MyOp.class));
 * </pre>
 * Note that parsing with user-defined operators always uses
 * the standard expression types, not the extended expression
 * types, so it is possible that the parsed expression will
 * not equal the same values constructed in code using the
 * extended expression type.
 *
 * <h3>User-defined transformations</h3>
 *
 * Create new user-defined transformations of expressions by
 * implementing the {@link net.peierls.expr.Visitor Visitor}
 * interface and providing a static method to create an instance
 * of the concrete visitor and call the
 * {@link net.peierls.expr.Expression#accept accept} method of
 * an argument expression on that instance.
 * See <a href="https://github.com/Tembrel/expressions/blob/master/src/test/java/net/peierls/expr/TraceVisitor.java#L11"
 * target="_top">TraceVisitor</a> in the test code for a full example.
 */
package net.peierls.expr;