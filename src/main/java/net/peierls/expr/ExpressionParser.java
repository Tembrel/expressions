package net.peierls.expr;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Map;

import org.jparsec.OperatorTable;
import org.jparsec.Parser;
import org.jparsec.Parsers;
import org.jparsec.Scanners;
import org.jparsec.Terminals;

import one.util.streamex.StreamEx;


/**
 * A parser for Expression instances.
 * Adapted from JParsec tutorial.
 * @see <a href="https://github.com/jparsec/jparsec/wiki/Tutorial">JParsec tutorial</a>
 */
@SuppressWarnings("InconsistentCapitalization")
public class ExpressionParser {

    private static final Parser<ConstantExpression> CONSTANT =
        Terminals.DecimalLiteral.PARSER.map(Double::valueOf).map(Expression::expr);

    private static final Parser<VariableExpression> VARIABLE =
        Terminals.Identifier.PARSER.map(Expression::expr);

    private static final Parser<Void> IGNORED = Scanners.WHITESPACES;

    private static ImmutableList<String> KEYWORDS = ImmutableList
        .of("let", "in");

    private static final String WHITESPACE_ALIAS = "*";

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private static final ExpressionParser DEFAULT_PARSER = parser(ImmutableList.of());


    private final ImmutableList<Operator> OPERATORS;
    private final String[] OP_SYMS;
    private final String[] OP_TERMS;
    private final Terminals TERMS;
    private final Parser<?> TOKENIZER;
    private final Parser<?> WHITESPACE_OP;
    private final Parser<Expression> parser;


    ExpressionParser(List<Operator> operators) {
        this.OPERATORS = ImmutableList.copyOf(operators);

        this.OP_SYMS = opSymbols()
            .toArray(EMPTY_STRING_ARRAY);

        this.OP_TERMS = opSymbols()
            .append("(", ")", "=", ",")
            .toArray(EMPTY_STRING_ARRAY);

        this.TERMS = Terminals.operators(OP_TERMS)
            .words(Scanners.IDENTIFIER)
            .keywords(KEYWORDS)
            .build();

        this.TOKENIZER = Parsers.or(
            TERMS.tokenizer(),
            Terminals.DecimalLiteral.TOKENIZER,
            Terminals.Identifier.TOKENIZER
        );

        this.WHITESPACE_OP = term(OP_SYMS).not();

        this.parser = buildParser();
    }

    /**
     * Returns a parser on the built-in operators.
     */
    public static ExpressionParser defaultParser() {
        return DEFAULT_PARSER;
    }

    /**
     * Creates a parser on the given operators and the built-in operators.
     */
    @SuppressWarnings("unchecked")
    @SafeVarargs
    public static ExpressionParser parser(Class<? extends Enum<? extends Operator>> firstOperatorType,
            Class<? extends Enum<? extends Operator>>... operatorTypes) {
        return parser(StreamEx.of(operatorTypes).prepend(firstOperatorType).toList());
    }

    /**
     * Creates a parser on the given operators and the built-in operators.
     */
    public static ExpressionParser parser(List<Class<? extends Enum<? extends Operator>>> operatorTypes) {
        return new ExpressionParser(
            operatorsOf(
                StreamEx.of(operatorTypes)
                    .prepend(builtInOperators())
                    .toList()
            )
        );
    }

    private static final List<Class<? extends Enum<? extends Operator>>> builtInOperators() {
        return ImmutableList.of(
            BasicOperator.class,
            TrigonometricOperator.class
        );
    }

    private static List<Operator> operatorsOf(List<Class<? extends Enum<? extends Operator>>> operatorTypes) {
        return StreamEx.of(operatorTypes)
            .distinct()
            .map(Class::getEnumConstants)
            .flatMap(opArray -> StreamEx.of(opArray))
            .map(op -> (Operator) op)
            .toList();
    }


    /**
     * Parse an expression using whatever operators
     * this parser has been configured with.
     */
    public Expression parse(String exprText) {
        return parser.parse(exprText);
    }


    StreamEx<String> opSymbols() {
        return StreamEx.of(OPERATORS)
            .map(Operator::symbol)
            .map(String::trim)
            .distinct();
    }

    private Parser<Expression> buildParser() {
        return exprParser().from(TOKENIZER, IGNORED.skipMany());
    }


    Parser<?> term(String... names) {
        return TERMS.token(names);
    }


    Parser<Expression> exprParser() {
        Parser.Reference<Expression> exprRef = Parser.newReference();
        Parser<Expression> parenthesized = exprRef.lazy().between(term("("), term(")"));
        Parser<Expression> letExpr = letExprParser(exprRef.lazy());
        Parser<Expression> operand = parenthesized
            .or(letExpr)
            .or(CONSTANT)
            .or(VARIABLE);
        Parser<Expression> parser = StreamEx.of(OPERATORS)
            .foldLeft(new OperatorTable<Expression>(), this::addOperator)
            .build(operand);
        exprRef.set(parser);
        return parser;
    }

    Parser<Expression> letExprParser(Parser<Expression> exprParser) {
        return Parsers.sequence(
            term("let"),
            bindingParser(exprParser).sepBy1(term(",")),
            term("in"),
            exprParser,
            (let, bindings, in, expr) ->
                expr.where(bindingsToMap(bindings))
        );
    }

    static class Binding {
        final String varName;
        final Expression boundExpr;

        Binding(VariableExpression varExpr, Expression boundExpr) {
            this.varName = varExpr.varName();
            this.boundExpr = boundExpr;
        }

        public String varName() { return varName; }
        public Expression boundExpr() { return boundExpr; }
    }

    Map<String, Expression> bindingsToMap(List<Binding> bindings) {
        return StreamEx.of(bindings)
            .mapToEntry(Binding::varName, Binding::boundExpr)
            .toMap();
    }

    Parser<Binding> bindingParser(Parser<Expression> exprParser) {
        return Parsers.sequence(
            VARIABLE,
            term("="),
            exprParser,
            (varExpr, eq, expr) -> new Binding(varExpr, expr)
        );
    }

    private OperatorTable<Expression> addOperator(OperatorTable<Expression> opTable, Operator op) {
        String symbol = op.symbol().trim();
        Parser<?> token = term(symbol);
        if (symbol.equals(WHITESPACE_ALIAS)) {
            token = token.cast().or(WHITESPACE_OP);
        }
        switch (op.type()) {
            case PREFIX:
                return opTable.prefix(token.retn(v -> v.apply((UnaryOp) op)), op.precedence());
            case POSTFIX:
                return opTable.postfix(token.retn(v -> v.apply((UnaryOp) op)), op.precedence());
            case INFIXL:
                return opTable.infixl(token.retn((l, r) -> l.apply((BinaryOp) op, r)), op.precedence());
            case INFIXN:
                return opTable.infixn(token.retn((l, r) -> l.apply((BinaryOp) op, r)), op.precedence());
            case INFIXR:
                return opTable.infixr(token.retn((l, r) -> l.apply((BinaryOp) op, r)), op.precedence());
            default:
                throw new IllegalStateException("unknown operator type: " + op.type());
        }
    }
}
