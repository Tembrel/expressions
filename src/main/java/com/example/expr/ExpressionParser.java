package com.example.expr;

import static com.example.expr.Expression.expr;

import com.google.common.collect.ImmutableList;

import java.util.List;

import org.jparsec.*;

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
        return new Builder(operators).build();
    }

    static final class Builder {
        final ImmutableList<Operator> operators;
        final List<String> opSymbols;
        final String[] opSymArray;
        final Terminals OPERATORS;
        final Terminals TERMS;
        final Parser<?> TOKENIZER;
        final Parser<?> WHITESPACE_MUL;

        Builder(ImmutableList<Operator> operators) {
            this.operators = operators;
            this.opSymbols = StreamEx.of(operators)
                .map(Operator::symbol)
                .map(String::trim)
                .append("(", ")", "[", "]")
                .toList();
            this.opSymArray = opSymbols.toArray(new String[0]);
            this.OPERATORS = Terminals.operators(opSymbols);
            this.TERMS = OPERATORS
                .words(Scanners.IDENTIFIER)
                .keywords("let", "in")
                .build();
            this.TOKENIZER = TERMS.tokenizer().cast()
                .or(Terminals.DecimalLiteral.TOKENIZER);
            this.WHITESPACE_MUL = term(opSymArray).not();
        }


        Parser<?> term(String... names) {
            return OPERATORS.token(names);
        }

        <T> Parser<T> op(String name, T value) {
            return term(name).retn(value);
        }


        Parser<Expression> exprParser(Parser<Expression> atom) {
            Parser.Reference<Expression> ref = Parser.newReference();
            Parser<Expression> parenthesized = ref.lazy().between(term("("), term(")"));
            //Parser<Expression> bracketed = ref.lazy().between(term("["), term("]"));
            Parser<Expression> unit = parenthesized
                //.or(bracketed)
                .or(atom);
            Parser<Expression> parser = new OperatorTable<Expression>()
                //.infixl(op("+", (l, r) -> l + r), 10)
                //.infixl(op("-", (l, r) -> l - r), 10)
                //.infixl(Parsers.or(term("*"), WHITESPACE_MUL).retn((l, r) -> l * r), 20)
                //.infixl(op("/", (l, r) -> l / r), 20)
                //.prefix(op("-", v -> -v), 30)
                .build(unit);
            ref.set(parser);
            return parser;
        }

        public Parser<Expression> build() {
            return exprParser(ATOM).from(TOKENIZER, IGNORED);
        }
    }

    private static final Parser<Expression> CONSTANT =
        Terminals.DecimalLiteral.PARSER.map(s -> expr(Double.valueOf(s)));

    private static final Parser<Expression> VARIABLE =
        Terminals.StringLiteral.PARSER.map(s -> expr(s));

    private static final Parser<Expression> ATOM = CONSTANT.or(VARIABLE);

    private static final Parser<Void> IGNORED = Scanners.WHITESPACES;
}
