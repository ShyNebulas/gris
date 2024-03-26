package com.github.gris.lexer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test class for the {@link Lexer} class.
 */
class LexerTest {

    /**
     * Tests if the lexer returns EOF token when the source is empty.
     */
    @Test
    void returnsEOF() {
        Lexer lexer = new Lexer("");
        List<Token> tokens = lexer.scanTokens();
        assertEquals(1, tokens.size());
        assertEquals(TokenType.EOF, tokens.get(0).type);
    }

    /**
     * Tests if the lexer returns expected tokens for a simple source code.
     */
    @Test
    void ExpectedTokens() {
        // Test source code
        String source = "def add(x, y) { return x + y; }";
        Lexer lexer = new Lexer(source);

        List<Token> tokens = lexer.scanTokens();

        assertEquals(15, tokens.size());
        assertTokenType(tokens.get(0), TokenType.DEF);
        assertTokenType(tokens.get(1), TokenType.IDENTIFIER);
        assertTokenType(tokens.get(2), TokenType.LEFT_PARENTHESIS);
        assertTokenType(tokens.get(3), TokenType.IDENTIFIER);
        assertTokenType(tokens.get(4), TokenType.COMMA);
        assertTokenType(tokens.get(5), TokenType.IDENTIFIER);
        assertTokenType(tokens.get(6), TokenType.RIGHT_PARENTHESIS);
        assertTokenType(tokens.get(7), TokenType.LEFT_BRACE);
        assertTokenType(tokens.get(8), TokenType.RETURN);
        assertTokenType(tokens.get(9), TokenType.IDENTIFIER);
        assertTokenType(tokens.get(10), TokenType.PLUS);
        assertTokenType(tokens.get(11), TokenType.IDENTIFIER);
        assertTokenType(tokens.get(12), TokenType.SEMICOLON);
        assertTokenType(tokens.get(13), TokenType.RIGHT_BRACE);
        assertTokenType(tokens.get(14), TokenType.EOF);
    }

    /**
     * Tests if the lexer returns expected tokens for source code with comments.
     */
    @Test
    void ExpectedTokensWithComments() {
        String source =
                """
                // This is a test comment
                def add(x, y) { return x + y; }
                /*
                    This is another test comment
                */
                """;
        Lexer lexer = new Lexer(source);

        List<Token> tokens = lexer.scanTokens();

        assertEquals(15, tokens.size());
        assertTokenType(tokens.get(0), TokenType.DEF);
        assertTokenType(tokens.get(1), TokenType.IDENTIFIER);
        assertTokenType(tokens.get(2), TokenType.LEFT_PARENTHESIS);
        assertTokenType(tokens.get(3), TokenType.IDENTIFIER);
        assertTokenType(tokens.get(4), TokenType.COMMA);
        assertTokenType(tokens.get(5), TokenType.IDENTIFIER);
        assertTokenType(tokens.get(6), TokenType.RIGHT_PARENTHESIS);
        assertTokenType(tokens.get(7), TokenType.LEFT_BRACE);
        assertTokenType(tokens.get(8), TokenType.RETURN);
        assertTokenType(tokens.get(9), TokenType.IDENTIFIER);
        assertTokenType(tokens.get(10), TokenType.PLUS);
        assertTokenType(tokens.get(11), TokenType.IDENTIFIER);
        assertTokenType(tokens.get(12), TokenType.SEMICOLON);
        assertTokenType(tokens.get(13), TokenType.RIGHT_BRACE);
        assertTokenType(tokens.get(14), TokenType.EOF);
    }

    /**
     * Helper method to assert the type of a token.
     *
     * @param token        The token to be checked.
     * @param expectedType The expected type of the token.
     */
    private void assertTokenType(Token token, TokenType expectedType) {
        assertEquals(expectedType, token.type);
    }
}
