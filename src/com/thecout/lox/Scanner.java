package com.thecout.lox;


import java.util.ArrayList;
import java.util.List;

import static com.thecout.lox.TokenType.EOF;

public class Scanner {


    private final String source;
    private final List<Token> tokens = new ArrayList<>();


    public Scanner(String source) {
        this.source = source;
    }

    private boolean isComment = false;


    public List<Token> scanLine(String line, int lineNumber) {
        List<Token> returnToken = new ArrayList<>();

        String temp = "";
        Token token = null;
        Token newToken = null;
        char[] chars = line.toCharArray();

        for (int i = 0; i < chars.length; i++) {

            temp += chars[i];
            token = newToken;
            newToken = getToken(temp, lineNumber);

            // reading the line any further is worthless
            if(newToken == null && isComment){
                token = new Token(TokenType.COMMENT, temp, temp, lineNumber);
                returnToken.add(token);
                break;
            }


            // bei temp = "12." kackt er ab, deshalb diese IF
            if(token != null && token.type == TokenType.NUMBER && newToken == null && chars[i] == '.'){
                temp += chars[++i];
                newToken = getToken(temp, lineNumber);
            }

            // if new token is null -> the last token was the greatest possible match -> add last token
            if(newToken == null && token != null){
                token.idx = i;
                returnToken.add(token);
                temp = "";

                // if there is no whitespace between tokens e.g. printSum(a -> go back and read the last char again!
                if(chars[i] != ' ')
                    i--;
            }

            //on end of line:
            if(i == chars.length - 1 && newToken != null){
                newToken.idx = i;
                returnToken.add(newToken);
            }

        }

        isComment = false;

        return returnToken;
    }

    private Token getToken(String temp, int lineNumber) {

        Token token;

        if(temp.equals("//")) {
            isComment = true;
            return null;
        }

        if(temp.matches("\\d+([.]\\d+)?"))
            return new Token(TokenType.NUMBER, temp, Double.parseDouble(temp), lineNumber);

        if(temp.matches("\".*\""))
            return new Token(TokenType.STRING, temp, temp.substring(1, temp.length()-1), lineNumber);

        token = getKeywordToken(temp, lineNumber);
        if(token != null)
            return token;

        if(temp.matches("([a-z]|_)([a-z]|[A-Z]|_|\\d)*"))
            return new Token(TokenType.IDENTIFIER, temp, temp, lineNumber);


        token = getSingleCharacterToken(temp, lineNumber);
        if(token != null)
            return token;

        token = getOneOrTwoCharacterToken(temp, lineNumber);
        if(token != null)
            return token;


        return null;




    }


    private Token getSingleCharacterToken(String temp, int lineNum) {

        switch (temp){
            // Single-character tokens.
            case "(":
                return new Token(TokenType.LEFT_PAREN, temp, temp, lineNum);
            case ")":
                return new Token(TokenType.RIGHT_PAREN, temp, temp, lineNum);
            case "{":
                return new Token(TokenType.LEFT_BRACE, temp, temp, lineNum);
            case "}":
                return new Token(TokenType.RIGHT_BRACE, temp, temp, lineNum);
            case ",":
                return new Token(TokenType.COMMA, temp, temp, lineNum);
            case ".":
                return new Token(TokenType.DOT, temp, temp, lineNum);
            case "-":
                return new Token(TokenType.MINUS, temp, temp, lineNum);
            case "+":
                return new Token(TokenType.PLUS, temp, temp, lineNum);
            case ";":
                return new Token(TokenType.SEMICOLON, temp, temp, lineNum);
            case "/":
                return new Token(TokenType.SLASH, temp, temp, lineNum);
            case "*":
                return new Token(TokenType.STAR, temp, temp, lineNum);
            default:
                return null;
        }
    }

    private Token getOneOrTwoCharacterToken(String temp, int lineNum) {

        switch (temp){
            // One or two character tokens.
            case "!=":
                return new Token(TokenType.BANG_EQUAL, temp, temp, lineNum);
            case "!":
                return new Token(TokenType.BANG, temp, temp, lineNum);
            case "==":
                return new Token(TokenType.EQUAL_EQUAL, temp, temp, lineNum);
            case "=":
                return new Token(TokenType.EQUAL, temp, temp, lineNum);
            case ">=":
                return new Token(TokenType.GREATER_EQUAL, temp, temp, lineNum);
            case ">":
                return new Token(TokenType.GREATER, temp, temp, lineNum);
            case "<=":
                return new Token(TokenType.LESS_EQUAL, temp, temp, lineNum);
            case "<":
                return new Token(TokenType.LESS, temp, temp, lineNum);
            default:
                return null;
        }
    }


    private Token getKeywordToken(String temp, int lineNum) {

        switch (temp){
            // One or two character tokens.
            case "and":
                return new Token(TokenType.AND, temp, temp, lineNum);
            case "else":
                return new Token(TokenType.ELSE, temp, temp, lineNum);
            case "false":
                return new Token(TokenType.FALSE, temp, temp, lineNum);
            case "fun":
                return new Token(TokenType.FUN, temp, temp, lineNum);
            case "for":
                return new Token(TokenType.FOR, temp, temp, lineNum);
            case "if":
                return new Token(TokenType.IF, temp, temp, lineNum);
            case "nil":
                return new Token(TokenType.NIL, temp, temp, lineNum);
            case "or":
                return new Token(TokenType.OR, temp, temp, lineNum);
            case "print":
                return new Token(TokenType.PRINT, temp, temp, lineNum);
            case "return":
                return new Token(TokenType.RETURN, temp, temp, lineNum);
            case "true":
                return new Token(TokenType.TRUE, temp, temp, lineNum);
            case "var":
                return new Token(TokenType.VAR, temp, temp, lineNum);
            case "while":
                return new Token(TokenType.WHILE, temp, temp, lineNum);
            default:
                return null;
        }
    }


    public List<Token> scan() {
        String[] lines = source.split("\n");
        for (int i = 0; i < lines.length; i++) {
            tokens.addAll(scanLine(removeWhitespace(lines[i]), i+1));
        }
        tokens.add(new Token(EOF, "", "", lines.length));

        // TODO: How do you handle comments???
        tokens.removeIf(t -> t.type == TokenType.COMMENT);

        return tokens;
    }

    private String removeWhitespace(String line){
        return line.trim().replaceAll("\\s+", " ");
    }

}
