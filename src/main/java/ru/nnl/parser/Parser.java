package ru.nnl.parser;


import ru.nnl.exception.LangParseException;
import ru.nnl.lexer.Lexeme;
import ru.nnl.token.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class Parser {

    private final List<Token> tokens;
    private int pos = -1;
    private ParseResult most_depth_error_res;
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void lang() throws LangParseException {
        //System.out.println("lang");

        List<Function<Object, ParseResult>> expressions = new ArrayList<>();
        expressions.add((arg0) -> plusOperation((arg) -> expr()));

        ParseResult res = andOperation(expressions);
        if (!res.success) {
            throw new LangParseException(most_depth_error_res.error_mes);
        }
    }

    private ParseResult expr() {
        //System.out.println("expr");

        List<Function<Object, ParseResult>> expressions = new ArrayList<>();
        expressions.add((arg) -> assignExpr());
        expressions.add((arg) -> condExpr());
        expressions.add((arg) -> whileExpr());
        expressions.add((arg) -> inputExpr());
        expressions.add((arg) -> outputExpr());
        expressions.add((arg) -> function());
        expressions.add((arg) -> functionCall());
        expressions.add((arg) -> returnExpr());
        expressions.add((arg) -> methodCall());

        return orOperation(expressions);
    }

    private ParseResult valueExpr() {
        //System.out.println("valueExpr");
        List<Function<Object, ParseResult>> expressions = new ArrayList<>();
        expressions.add((arg0) -> var());
        expressions.add((arg0) -> digit());
        expressions.add((arg0) -> functionCall());
        expressions.add((arg0) -> arithmeticExpr());

        return orOperation(expressions);
    }

    private ParseResult assignExpr() {
        //System.out.println("assignExpr");
        List<Function<Object, ParseResult>> expressions = new ArrayList<>();
        expressions.add((arg0) -> var());
        expressions.add((arg0) -> assignOp());
        expressions.add((arg0) -> assignValue());
        expressions.add((arg0) -> semicolon());

        return andOperation(expressions);
    }

    private ParseResult assignValue() {
        //System.out.println("assignValue");
        List<Function<Object, ParseResult>> expressions = new ArrayList<>();
        expressions.add((arg0) -> constString());
        expressions.add((arg0) -> type());
        expressions.add((arg0) -> valueExpr());

        return orOperation(expressions);
    }

    private ParseResult arithmeticExpr() {
        //System.out.println("arithmeticExpr");
        List<Function<Object, ParseResult>> expressions = new ArrayList<>();
        expressions.add((arg0) -> questionMarkOperation((arg1) -> openParanthesis()));
        expressions.add((arg0) -> valueExpr());
        expressions.add((arg0) -> {
            List<Function<Object, ParseResult>> andExpressions = new ArrayList<>();
            andExpressions.add((arg) -> op());
            andExpressions.add((arg) -> arithmeticExpr());
            return starOperation((arg) -> andOperation(andExpressions));
        });
        expressions.add((arg0) -> questionMarkOperation((arg1) -> closeParanthesis()));

        return andOperation(expressions);
    }

    private ParseResult op() {
        //System.out.println("op");
        List<Function<Object, ParseResult>> expressions = new ArrayList<>();
        expressions.add((arg0) -> plus());
        expressions.add((arg0) -> minus());
        expressions.add((arg0) -> multiply());
        expressions.add((arg0) -> divide());

        return orOperation(expressions);
    }

    private ParseResult logicOp() {
        //System.out.println("logicOp");
        List<Function<Object, ParseResult>> expressions = new ArrayList<>();
        expressions.add((arg0) -> greaterOp());
        expressions.add((arg0) -> lessOp());
        expressions.add((arg0) -> equalsOp());
        expressions.add((arg0) -> greaterOrEqualsOp());
        expressions.add((arg0) -> lessOrEqualsOp());

        return orOperation(expressions);
    }

    private ParseResult body() {
        //System.out.println("body");
        List<Function<Object, ParseResult>> expressions = new ArrayList<>();
        expressions.add((arg0) -> openBracket());
        expressions.add((arg0) -> plusOperation((arg) -> expr()));
        expressions.add((arg0) -> closeBracket());

        return andOperation(expressions);
    }

    private ParseResult logicalHead() {
        //System.out.println("logicalHead");
        List<Function<Object, ParseResult>> expressions = new ArrayList<>();
        expressions.add((arg0) -> openParanthesis());
        expressions.add((arg0) -> logicalExpr());
        expressions.add((arg0) -> closeParanthesis());

        return andOperation(expressions);
    }

    private ParseResult logicalExpr() {
        //System.out.println("logicalExpression");
        List<Function<Object, ParseResult>> expressions = new ArrayList<>();
        expressions.add((arg0) -> valueExpr());
        expressions.add((arg0) -> logicOp());
        expressions.add((arg0) -> valueExpr());

        return andOperation(expressions);
    }

    private ParseResult condExpr() {
        //System.out.println("condExpr");
        List<Function<Object, ParseResult>> expressions = new ArrayList<>();
        expressions.add((arg0) -> condHead());
        expressions.add((arg0) -> body());

        return andOperation(expressions);
    }

    private ParseResult condHead() {
        //System.out.println("condHead");
        List<Function<Object, ParseResult>> expressions = new ArrayList<>();
        expressions.add((arg0) -> ifKeyword());
        expressions.add((arg0) -> logicalHead());

        return andOperation(expressions);
    }

    private ParseResult whileExpr() {
        //System.out.println("whileExpr");
        List<Function<Object, ParseResult>> expressions = new ArrayList<>();
        expressions.add((arg0) -> whileHead());
        expressions.add((arg0) -> body());

        return andOperation(expressions);
    }

    private ParseResult whileHead() {
        //System.out.println("whileHead");
        List<Function<Object, ParseResult>> expressions = new ArrayList<>();
        expressions.add((arg0) -> whileKeyword());
        expressions.add((arg0) -> logicalHead());

        return andOperation(expressions);
    }

    private ParseResult argList() {
        //System.out.println("argList");
        List<Function<Object, ParseResult>> expressions = new ArrayList<>();
        expressions.add((arg0) -> valueExpr());
        expressions.add((arg0) -> {
            List<Function<Object, ParseResult>> starExpressions = new ArrayList<>();
            starExpressions.add((arg1) -> comma());
            starExpressions.add((arg1) -> valueExpr());
            return starOperation((arg1) -> andOperation(starExpressions));
        });

        return andOperation(expressions);
    }

    private ParseResult methodCall() {
        //System.out.println("methodCall");
        List<Function<Object, ParseResult>> expressions = new ArrayList<>();
        expressions.add((arg0) -> var());
        expressions.add((arg0) -> dot());
        expressions.add((arg0) -> method());
        expressions.add((arg0) -> openParanthesis());
        expressions.add((arg0) -> questionMarkOperation((arg1) -> argList()));
        expressions.add((arg0) -> closeParanthesis());
        expressions.add((arg0) -> semicolon());

        return andOperation(expressions);
    }

    private ParseResult function() {
        //System.out.println("function");
        List<Function<Object, ParseResult>> expressions = new ArrayList<>();
        expressions.add((arg0) -> functionHead());
        expressions.add((arg0) -> body());

        return andOperation(expressions);
    }

    private ParseResult functionHead() {
        //System.out.println("functionHead");
        List<Function<Object, ParseResult>> expressions = new ArrayList<>();
        expressions.add((arg0) -> var());
        expressions.add((arg0) -> openParanthesis());
        expressions.add((arg0) -> questionMarkOperation((arg1) -> argList()));
        expressions.add((arg0) -> closeParanthesis());

        return andOperation(expressions);
    }

    private ParseResult returnExpr() {
        //System.out.println("returnExpr");
        List<Function<Object, ParseResult>> expressions = new ArrayList<>();
        expressions.add((arg0) -> returnKeyword());
        expressions.add((arg0) -> valueExpr());
        expressions.add((arg0) -> semicolon());

        return andOperation(expressions);
    }

    private ParseResult functionCall() {
        //System.out.println("functionCall");
        List<Function<Object, ParseResult>> expressions = new ArrayList<>();
        expressions.add((arg0) -> functionHead());
        expressions.add((arg0) -> semicolon());

        return andOperation(expressions);
    }

    private ParseResult inputExpr() {
        //System.out.println("inputExpr");
        List<Function<Object, ParseResult>> expressions = new ArrayList<>();
        expressions.add((arg0) -> inputKeyword());
        expressions.add((arg0) -> openParanthesis());
        expressions.add((arg0) -> var());
        expressions.add((arg0) -> closeParanthesis());
        expressions.add((arg0) -> semicolon());

        return andOperation(expressions);
    }

    private ParseResult outputExpr() {
        //System.out.println("outputExpr");
        List<Function<Object, ParseResult>> expressions = new ArrayList<>();
        expressions.add((arg0) -> outputKeyword());
        expressions.add((arg0) -> openParanthesis());
        expressions.add((arg0) -> outputValue());
        expressions.add((arg0) -> closeParanthesis());
        expressions.add((arg0) -> semicolon());

        return andOperation(expressions);
    }

    private ParseResult outputValue() {
        //System.out.println("outputValue");
        List<Function<Object, ParseResult>> expressions = new ArrayList<>();
        expressions.add((arg0) -> valueExpr());
        expressions.add((arg0) -> constString());

        return orOperation(expressions);
    }

    private ParseResult comma() {
        //System.out.println("comma");
        return matchToken(match(), Lexeme.COMMA);
    }

    private ParseResult dot() {
        //System.out.println("dot");
        return matchToken(match(), Lexeme.DOT);
    }

    private ParseResult var() {
        //System.out.println("var");
        return matchToken(match(), Lexeme.VAR);
    }

    private ParseResult assignOp() {
        //System.out.println("assignOp");
        return matchToken(match(), Lexeme.ASSIGN_OP);
    }

    private ParseResult digit() {
        //System.out.println("digit");
        return matchToken(match(), Lexeme.DIGIT);
    }

    private ParseResult plus() {
        //System.out.println("plus");
        return matchToken(match(), Lexeme.PLUS);
    }

    private ParseResult minus() {
        //System.out.println("minus");
        return matchToken(match(), Lexeme.MINUS);
    }

    private ParseResult multiply() {
        //System.out.println("multiply");
        return matchToken(match(), Lexeme.MULTIPLY);
    }

    private ParseResult divide() {
        //System.out.println("divide");
        return matchToken(match(), Lexeme.DIVIDE);
    }

    private ParseResult greaterOp() {
        //System.out.println("greaterOp");
        return matchToken(match(), Lexeme.GREATER);
    }
    private ParseResult lessOp() {
        //System.out.println("lessOp");
        return matchToken(match(), Lexeme.LESS);
    }
    private ParseResult equalsOp() {
        //System.out.println("equalsOp");
        return matchToken(match(), Lexeme.EQUALS);
    }
    private ParseResult greaterOrEqualsOp() {
        //System.out.println("greaterOrEqualsOp");
        return matchToken(match(), Lexeme.GREATER_OR_EQUALS);
    }
    private ParseResult lessOrEqualsOp() {
        //System.out.println("lessOrEqualsOp");
        return matchToken(match(), Lexeme.LESS_OR_EQUALS);
    }

    private ParseResult constString() {
        //System.out.println("constString");
        return matchToken(match(), Lexeme.CONST_STRING);
    }

    private ParseResult semicolon() {
        //System.out.println("semicolon");
        return matchToken(match(), Lexeme.SEMICOLON);
    }

    private ParseResult openBracket() {
        //System.out.println("openBracket");
        return matchToken(match(), Lexeme.OPEN_BRACKET);
    }

    private ParseResult closeBracket() {
        //System.out.println("closeBracket");
        return matchToken(match(), Lexeme.CLOSE_BRACKET);
    }

    private ParseResult openParanthesis() {
        //System.out.println("openParanthesis");
        return matchToken(match(), Lexeme.OPEN_PARENTHESIS);
    }

    private ParseResult closeParanthesis() {
        //System.out.println("closeParanthesis");
        return matchToken(match(), Lexeme.CLOSE_PARENTHESIS);
    }

    private ParseResult type() {
        //System.out.println("type");
        return matchToken(match(), Lexeme.TYPE);
    }

    private ParseResult ifKeyword() {
        //System.out.println("if_kw");
        return matchToken(match(), Lexeme.IF_KW);
    }

    private ParseResult whileKeyword() {
        //System.out.println("while_kw");
        return matchToken(match(), Lexeme.WHILE_KW);
    }

    private ParseResult inputKeyword() {
        //System.out.println("input_kw");
        return matchToken(match(), Lexeme.INPUT_KW);
    }

    private ParseResult outputKeyword() {
        //System.out.println("output_kw");
        return matchToken(match(), Lexeme.OUTPUT_KW);
    }

    private ParseResult returnKeyword() {
        //System.out.println("return_kw");
        return matchToken(match(), Lexeme.RETURN_KW);
    }

    private ParseResult method() {
        //System.out.println("method");
        return matchToken(match(), Lexeme.METHOD);
    }

    private ParseResult orOperation(List<Function<Object, ParseResult>> expressions) {
        List<ParseResult> results = new ArrayList<>();

        for (Function<Object, ParseResult> func : expressions) {
            ParseResult cur_res = func.apply(null);
            if (cur_res.success) {
                return cur_res;
            } else {
                results.add(cur_res);
            }
        }

        // If no one expression matches then store the most depth result
        most_depth_error_res = Collections.max(results, Comparator.comparingInt(left -> left.depth));

        return results.get(results.size() - 1);
    }

    private ParseResult andOperation(List<Function<Object, ParseResult>> expressions) {
        int depthSum = 0;
        for (Function<Object, ParseResult> func : expressions) {
            ParseResult cur_res = func.apply(null);
            depthSum += cur_res.depth;

            if (!cur_res.success) {
                cur_res.depth = depthSum - cur_res.depth;
                back(cur_res.depth);
                return cur_res;
            }
        }

        return new ParseResult(true, depthSum, "");
    }

    private ParseResult starOperation(Function<Object, ParseResult> expression) {
        ParseResult curRes = new ParseResult();
        int depthSum = 0;

        while (curRes.success) {
            curRes = expression.apply(null);
            depthSum += curRes.depth;
        }

        curRes.depth = depthSum - curRes.depth;
        curRes.success = true;
        return curRes;
    }

    private ParseResult plusOperation(Function<Object, ParseResult> expression) {
        ParseResult cur_res = new ParseResult();
        int depth_sum = 0;
        int counter = -1;

        while (cur_res.success) {
            ++counter;
            cur_res = expression.apply(null);
            depth_sum += cur_res.depth;
        }

        if (counter < 1) {
            return cur_res;
        }

        cur_res.depth = depth_sum - cur_res.depth;
        cur_res.success = true;
        return cur_res;
    }

    private ParseResult questionMarkOperation(Function<Object, ParseResult> expression) {
        ParseResult curRes = expression.apply(null);

        curRes.success = true;
        return curRes;
    }

    private Token match() {
        return tokens.get(++pos);
    }

    private void back(int step) {
        pos -= step;
    }

    private ParseResult matchToken(Token token, Lexeme type) {
        if (!token.getLexeme().equals(type)) {
            back(1);
            return new ParseResult(false, 1, type + " expected, but " +
                    token.getLexeme().name() + ": " +
                    token.getValue() + " found");
        }

        return new ParseResult(true, 1, "");
    }

    private static class ParseResult {
        public boolean success;
        public int depth;
        public String error_mes;

        public ParseResult() {
            this.success = true;
            this.depth = 0;
            this.error_mes = "";
        }

        public ParseResult(boolean success, int depth, String error_mes) {
            this.success = success;
            this.depth = depth;
            this.error_mes = error_mes;
        }
    }
}