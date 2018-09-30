package com.example.expr;

import com.google.common.collect.ImmutableList;

import java.util.List;

import org.jparsec.Parser;

import one.util.streamex.StreamEx;


/**
 * Builds parser for Expression instances.
 * Adapted from JParsec tutorial.
 * @see <a href="https://github.com/jparsec/jparsec/wiki/Tutorial">JParsec tutorail</a>
 */
public class ExpressionParser {

    final ImmutableList<Operator> operators;

    private ExpressionParser(List<Operator> operators) {
        this.operators = ImmutableList.copyOf(operators);
    }

    /**
     * Creates a builder for a parser on the given operators.
     */
    static Parser<Expression> parserFor(List<Class<? extends Enum<? extends Operator>>> operatorTypes) {
        List<Operator> operators = StreamEx.of(operatorTypes)
            .distinct()
            .map(Class::getEnumConstants)
            .flatMap(opArray -> StreamEx.of(opArray))
            .map(op -> (Operator) op)
            .toList();
        return new ExpressionParser(operators).build();
    }

    /**
     * Builds a parser for expressions on the operations known
     * by this builder.
     */
    public Parser<Expression> build() {
        return null;
    }
}
