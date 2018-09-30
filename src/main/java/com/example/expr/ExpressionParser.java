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

    private static final Parser<Expression> CONSTANT =
        Terminals.DecimalLiteral.PARSER.map(s -> expr(Double.valueOf(s)));

    private static final Parser<Expression> VARIABLE =
        Terminals.StringLiteral.PARSER.map(s -> expr(s));

    private static final Parser<Expression> ATOM = CONSTANT.or(VARIABLE);

    private static final Parser<Void> IGNORED = Scanners.WHITESPACES;


    private final ImmutableList<Operator> operators;
    private final List<String> opSymbols;
    private final String[] opSymArray;
    private final Terminals TERMS;
    private final Parser<?> TOKENIZER;
    private final Parser<?> WHITESPACE_MUL;


    ExpressionParser(List<Operator> operators) {
        this.operators = ImmutableList.copyOf(operators);
        this.opSymbols = StreamEx.of(operators)
            .map(Operator::symbol)
            .map(String::trim)
            .append("(", ")")
            .distinct()
            .toList();
        this.opSymArray = opSymbols.toArray(new String[0]);
        this.TERMS = Terminals.operators(opSymbols)
            .words(Scanners.IDENTIFIER)
            .keywords("let", "in")
            .build();
        this.TOKENIZER = TERMS.tokenizer().cast()
            .or(Terminals.DecimalLiteral.TOKENIZER);
        this.WHITESPACE_MUL = term(opSymArray).not();
    }


    /**
     * Creates a builder for a parser on the given operators.
     */
    public static Parser<Expression> parserFor(List<Class<? extends Enum<? extends Operator>>> operatorTypes) {
        List<Operator> operators = StreamEx.of(operatorTypes)
            .distinct()
            .map(Class::getEnumConstants)
            .flatMap(opArray -> StreamEx.of(opArray))
            .map(op -> (Operator) op)
            .toList();
        return new ExpressionParser(operators).build();
    }


    public Parser<Expression> build() {
        return exprParser(ATOM)
            .from(TOKENIZER, IGNORED.skipMany());
    }


    Parser<?> term(String... names) {
        return TERMS.token(names);
    }


    Parser<Expression> exprParser(Parser<Expression> atom) {
        Parser.Reference<Expression> ref = Parser.newReference();
        Parser<Expression> parenthesized = ref.lazy().between(term("("), term(")"));
        Parser<Expression> unit = parenthesized.or(atom);
        Parser<Expression> parser = StreamEx.of(operators)
            .foldLeft(new OperatorTable<Expression>(), this::add)
            .build(unit);
        ref.set(parser);
        return parser;
    }

    private OperatorTable<Expression> add(OperatorTable<Expression> opTable, Operator op) {
        //<T> Parser<T> op(String name, T value) {
        //    return term(name).retn(value);
        //}
        //.infixl(op("+", (l, r) -> l + r), 10)
        //.infixl(op("-", (l, r) -> l - r), 10)
        //.infixl(Parsers.or(term("*"), WHITESPACE_MUL).retn((l, r) -> l * r), 20)
        //.infixl(op("/", (l, r) -> l / r), 20)
        //.prefix(op("-", v -> -v), 30)
        String symbol = op.symbol().trim();
        switch (op.type()) {
            case PREFIX:
            case POSTFIX:
            case INFIXL:
            case INFIXN:
            case INFIXR:
        }
        return opTable;
    }
}
