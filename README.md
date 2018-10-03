# Expressions

This repository contains a Java framework for evaluating, formatting, and parsing
double-precision arithmetic expressions with constants, variables, unary and binary operations,
and binding of variables to other expressions.

The framework is extensible in two ways:

1. New operations can be added beyond the basic set provided here,
   and (optionally) new expression subtypes can be created that add methods
   to apply these new operations.
1. New transformations of expressions can be added beyond evaluation
   and formatting as string, e.g., compilation to bytecode.


## API usage

### Basic usage

See the [javadoc for the `net.peierls.expr` package](
  https://tembrel.github.io/expressions/javadoc/
) for how to use the API.
The code from this package documentation is also available
in the form of a [JUnit test](
  src/test/java/net/peierls/expr/BasicUsageTest.java#L10
).

### Extended usage

#### Add new operators

The [`TrigonometricOperator` class](
  src/main/java/net/peierls/expr/TrigonometricOperator.java#L10
) demonstrates how to add a user-defined operator enum
by implementing `DelegatingOperator`. The only boilerplate,
other than `implements DelegatingOperator` is in
[these three lines](
  src/main/java/net/peierls/expr/TrigonometricOperator.java#L24-L26
).

The expression parsing test class has [an example of parsing with
a user-defined operator enum](
  src/test/java/net/peierls/expr/ExtendedExpressionTest.java#L28-L63
);

The [`TrigonometricExpression` class](
  src/main/java/net/peierls/expr/TrigonometricExpression.java#L10
) demonstrates how to extend `Expression`
with convenience methods to apply these
new operations.

To ensure that operators are singletons, the framework uses
the [emulated extensible enum](
  https://drive.google.com/file/d/1qhEaShHhq5-0y4aQMByKzx9SOsKFu6oq/view
) pattern. Following this pattern is not
strictly required for user-defined operators, but extended
parsing only works with enum types.


#### Add new transformations of expressions

The current implementation uses the [Visitor pattern](
  https://drive.google.com/file/d/1k76P9Kl7__hXwp2FVAbvOcwATcphB3gm/view
) to implement the
[value](
  https://tembrel.github.io/expressions/javadoc/net/peierls/expr/Expression.html#value--
),
[freeVariables](
  https://tembrel.github.io/expressions/javadoc/net/peierls/expr/Expression.html#freeVariables--
), and
[toString](
  https://tembrel.github.io/expressions/javadoc/net/peierls/expr/Expression.html#toString--
) methods.
Users of this API can take advantage of this to create their
own transformations of expressions.
As an example, the [`TraceVisitor` class](
  src/test/java/net/peierls/expr/TraceVisitor.java#L11
) demonstrates how to add a new transformation
over expressions into an indented pseudo-AST (abstract syntax tree)
that displays the fixity, associativity, arity, and precedence
of the operators.

## Dependencies

- Guava
- StreamEx
- JParsec
- ErrorProne (compile-time only)
- Apache Ant (build-time only)


## Building

Clone this repository and navigate to the top-level directory.

Type `ant test` to run the tests (Apache Ant 1.10.x or later).
Use `ant jar` or just `ant` to build a Jar file for the framework
(not including dependencies) in the `build` subdirectory; this
will also run the tests.

The Ant build will download the Apache Ivy jar that manages
dependencies.
By default it will not use an existing Ivy installation
or cache, but this can be changed by overriding properties.
See the [Ivy build properties file](
  ivy/build-ivy.properties
) for details.


## Issues FAQ

- Constant and variable expressions compare equal if the
  wrapped values compare equal; their identities are not
  meaningful. Should they be?

  - It would be possible to use the identity of constant and variable
    expressions as equality, so that two distinct instances of `expr("a")` would
    not be equal to each other, but it would add a burden to
    the user to keep track of variables in a symbol table,
    and it would mean that string forms of unequal expressions
    could themselves be equal.
    It's much simpler to take advantage of Java's string interning.

- Equality of expressions is defined as structural identity rather
  than equivalence. Shouldn't you provide an equivalence test?

  - That's an excellent exercise for the reader! You could do it through
    structural analysis, but it might be more fun to cheat and see if
    repeatedly randomly binding the free variables to the same values
    in both expressions produces equal values.
  ```java
    assertTrue(equivalentExpressions(expr(1).plus("a"), expr("a").plus(1)));
    assertTrue(equivalentExpressions(expr("a").times(2), expr("a").plus("a")));
  ```

- Expressions that use variables that conflict with the
  reserved words `let` and `in` will format successfully as a string
  but will not be parseable. Shouldn't you prevent the use
  of those variable names?

  - Not all uses of expressions involve parsing. It seems
    draconian to prevent the use of perfectly good variable
    names for uses that don't require parsing.
    Anyone concerned by this could either write static `expr`
    methods that prevent the use of these keywords, or (more
    ambitiously) write an alternative formatting and parsing
    scheme.

- Now that you mention alternative parsing schemes, how about
  making the expression parser extensible?

  - Because it would be very hard and would have little benefit.

- Could this framework be parameterized by numeric type? It would
  be great to have `Expression<BigInteger>`, for example.

  - Maybe, but the framework is already extensible along two axes; adding a third
    would complicate the API unnecessarily by having to deal with things
    like the fact that some operations would apply to `double` but not
    to `BigInteger`, or vice versa.

- Expressions that only use single character variable names can be
  expressed more naturally by removing the whitespace in products,
  e.g., `ax^2 + bx + c` instead of `a x^2 + b x + c`. Could this
  library support that?

  - Yes, but the results would be parseable only through the
    ugly compromise of using something like
    quotation marks around variables with names longer than one character,
    e.g., `'pi'r^2`. It doesn't seem important enough to do right now.

- Why does binding throw `UnreferencedVariableException` when
  evaluating something like `let a = 3, b = 4 in b`?

  - Because it seemed more important to prevent what is probably
    a mistake than to support a vacuous interpretation.

- Why does binding throw `SelfReferenceException` when evaluating
  something like `let a = b + a in a + 1`? The `a` of `b + a` becomes
  a free variable in the result, so what's the problem?

  - Because that's pretty confusing. Better not to allow it in the first place.

- Why are there two ways to use trigonometric expressions: `sin(...)` and `trigExpr(...).sin()`?

  - For pedagogical purposes:
    `enum TrigonometricOperator` demonstrates how to add new operators,
    and `class TrigonometricExpression` demonstrates how to create an
    extended expression type that supports new methods.

- These different ways produce expressions that aren't equal. Is that an API bug?

  - It's a weakness, but if you consistently use one or the other approach, it won't be a
    problem in practice. And it's not easy to fix: Trying to get wrapped classes to
    pretend they are equal to the things they wrap is a fool's game.
  ```java
    @Test public void differentWaysOfUsingExtendedExpressions() {
        Expression sinA1 = sin(expr("a"));
        Expression sinA2 = trigExpr("a").sin();
        // They print the same, but they aren't equal.
        assertEquals(sinA1.toString(), sinA2.toString());
        assertFalse(sinA1.equals(sinA2));
    }
  ```

- `a^23` is interpreted as `a^2 * 3` by `Expression.of(...)`. Isn't that
  a bug?

  - Well... You can always write `a ^ 23` to get the parser to behave,
    but [I should probably remove `BasicOperator.SQUARED`](
      /Tembrel/expressions/issues/3
    ), as it doesn't
    buy us much, other than providing an example of a postfix operator.

- Could you remove the Guava and StreamEx dependencies?

  - Yes. Guava and StreamEx types do not appear in the API, and the implementation
    could be rewritten without them. I used them to simplify the implementation
    and to make it easier to reason about its correctness.

- Could you remove the JParsec dependency?

  - Not easily. JParsec types do not appear in the API, but it would be very
    hard to write the expression parser by hand.
    There are other Java parsing libraries, but most of them focus on
    working from a grammar expressed in text (e.g., BNF). JParsec
    makes it easy to build a grammar in Java dynamically, based on the
    operations passed at parser build time. The syntax of `OperatorBuilder`
    was taken directly from JParsec.

- Could you remove the ErrorProne compile-time dependency?

  - Yes, but since it doesn't impose any burden on API users
    and because it increases confidence in the correctness of
    the implementation, I don't see a reason to remove it.

- Why didn't you package this framework using Maven?

  - I'm more comfortable with Ant and Ivy.
