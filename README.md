# Expressions

This repository contains a Java framework for evaluating, formatting, and parsing
double-precision expressions with constants, variables, unary and binary operations,
and binding variables to expressions.

The framework is extensible in two ways:

1. New operations can be added beyond the basic set provided here,
   and (optionally) new expression subtypes can be created that add methods
   to apply these new operations.
1. New transformations of expressions can be added beyond evaluation
   and formatting as string, e.g., compilation to bytecode.


## API usage

[Javadoc](https://tembrel.github.io/expressions/javadoc/) for the most recent release.


### Basic usage

See the [basic usage JUnit test](
  src/test/java/com/example/expr/BasicUsageTest.java#L10
).

### Adding new operator categories and extending expressions

To ensure that operators are singletons, the framework uses
the typesafe enum pattern. Following this pattern is not
strictly required for user-defined operators, but extended
parsing only works with enum types.

The [`TrigonometricOperator` class](
  src/main/java/com/example/expr/TrigonometricOperator.java#L10
) demonstrates how to add a user-defined operator enum
by implementing `DelegatingOperator`. The only boilerplate,
other than `implements DelegatingOperator` is in
[these three lines](
  src/main/java/com/example/expr/TrigonometricOperator.java#L24-26
).

The expression parsing test class has [an example of parsing with
a user-defined operator enum](
  src/test/java/com/example/expr/ExpressionParsingTest.java#L74
);

The [`TrigonometricExpression` class](
  src/main/java/com/example/expr/TrigonometricExpression.java#L10
) demonstrates how to extend `Expression`
with convenience methods to apply these
new operations.


### Adding new transformations of expressions

The [`TraceVisitor` class](
  src/test/java/com/example/expr/TraceVistor.java#L11
) demonstrates how to add a new transformation
over expressions.


## Dependencies

- Guava
- StreamEx
- JParsec
- ErrorProne (compile-time only)
- Apache Ant (build-time only)


## Building

Clone this repository and navigate to the top-level directory.

Type `ant test` to run the tests (Apache Ant 1.10.x or later).

The Ant build will download the Apache Ivy jar that manages
dependencies.
By default it will not use an existing Ivy installation
or cache, but this can be changed by overriding properties.
See the [Ivy build properties file](
  ivy/build-ivy.properties
) for details.


## Issues FAQ

- Why isn't this framework parameterizable over other numeric types?

  - The framework is already extensible along two axes; adding a third
    would complicate the API unnecessarily. Parallel frameworks could
    be devised for other numeric types.

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
    and `classs TrigonometricExpression` demonstrates how to create an
    extended expression type that supports new methods.

- Could you remove the Guava and StreamEx dependencies?

  - Yes, no Guava or StreamEx types appear in the API, and the implementation
    could be rewritten without them. I used them to simplify the implementation
    and to make it easier to reason about its correctness.

- Could you remove the JParsec dependency?

  - Not easily. It would very hard to write the expression parser by hand.
    There are other Java parsing libraries, but most of them focus on
    working from a grammar expressed in text (e.g., BNF). JParsec
    makes it easy to build a grammar in Java dynamically, based on the
    operations passed at parser build time.

- Could you remove the ErrorProne compile-time dependency?

  - Yes, but since it doesn't impose any burden on API users
    and because it increases confidence in the correctness of
    the implementation, I don't see a reason to remove it.

- Why didn't you package this framework using Maven?

  - I'm more comfortable with Ant and Ivy.
