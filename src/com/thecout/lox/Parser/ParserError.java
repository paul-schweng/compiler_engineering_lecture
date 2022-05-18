package com.thecout.lox.Parser;

import com.thecout.lox.Token;

public class ParserError extends Exception {
    static void error(Token token, String message) {
        System.out.printf("%d %s\n", token.line, message);
    }

    public ParserError(Token token, String message) {
        super(String.format("line %d.%d: %s, got: %s (%s)\n", token.line, token.idx, message, token.type, token.lexeme));
        // System.out.printf("line %d: %s, got: %s (%s)\n", token.line, message, token.type, token.lexeme);
    }
}
