# Expressions

This repository contains a Java framework for evaluating and formatting double-precision
expressions with constants, variables, unary and binary operations, and binding variables
to expressions, with support for evaluation and string representation.

The framework is extensible in two ways:

1. New operations can be added beyond the basic set provided here.
1. New functions over composite expressions can be added beyond evaluation
   string representation.

### Parsing

Parsing of string representations is planned but not yet supported.


## API usage

Intended use cases and how the API can be applied to meet them.


## Building

Compilation dependencies: Guava, StreamEx, ErrorProne, JParsec.

Runtime dependencies: Guava, StreamEx.

Not hard to remove the Guava and StreamEx dependencies.


## Issues FAQ

- Why are there two ways to use trigonometric expressions: `sin(...)` and `trigExpr(...).sin()`?
  - For pedagogical purposes:
    `enum TrigonometricOperator` demonstrates how to add new operators,
    and `classs TrigonometricExpression` demonstrates how to create an
    extended expression type that supports new methods.
- Why isn't this framework parameterizable over other numeric types?
  - The framework is already extensible along two axes; adding a third
    would complicate the API unnecessarily. Parallel frameworks could
    be devised for other numeric types.

