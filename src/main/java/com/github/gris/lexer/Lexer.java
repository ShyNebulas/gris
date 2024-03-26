package com.github.gris.lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** This class represents a lexer which tokenizes source code. */
public final class Lexer {
  /** The source code to be tokenized. */
  private final String source;

  /** List to store the tokens. */
  private final List<Token> tokens = new ArrayList<>();

  /** Index of the start of the current lexeme. */
  private int start = 0;

  /** Current index in the source code. */
  private int current = 0;

  /** Current line number. */
  private int line = 1;

  /** Current column number. */
  private int col = 0;

  /** List to store the source lines. */
  private final List<String> sourceLines;

  // Map to store keywords and their corresponding token types
  private static final Map<String, TokenType> keywords;

  static {
    keywords = new HashMap<>();
    // Adding keywords and their token types to the map
    keywords.put("class", TokenType.CLASS);
    keywords.put("def", TokenType.DEF);
    keywords.put("val", TokenType.VAL);
    keywords.put("for", TokenType.FOR);
    keywords.put("while", TokenType.WHILE);
    keywords.put("if", TokenType.IF);
    keywords.put("else", TokenType.ELSE);
    keywords.put("true", TokenType.TRUE);
    keywords.put("false", TokenType.FALSE);
    keywords.put("null", TokenType.NULL);
    keywords.put("Boolean", TokenType.BOOLEAN);
    keywords.put("Number", TokenType.NUMBER);
    keywords.put("String", TokenType.STRING);
    keywords.put("Void", TokenType.VOID);
    keywords.put("return", TokenType.RETURN);
    keywords.put("super", TokenType.SUPER);
    keywords.put("this", TokenType.THIS);
    keywords.put("and", TokenType.AND);
    keywords.put("or", TokenType.OR);
  }

  /**
   * Constructs a Lexer object with the provided source code.
   *
   * @param source The source code to be tokenized.
   */
  public Lexer(String source) {
    this.source = source;
    this.sourceLines = splitSourceIntoLines(source);
  }

  /**
   * Tokenizes the source code and returns a list of tokens.
   *
   * @return A list of tokens.
   */
  public List<Token> scanTokens() {
    while (!isAtEnd()) {
      start = current;
      scanToken();
    }
    tokens.add(new Token(TokenType.EOF, "", null, line, col + 1));
    return tokens;
  }

  /**
   * Retrieves the source line at the specified line number.
   *
   * @param line The line number.
   * @return The source line.
   */
  public String getSourceLine(int line) {
    if (line <= sourceLines.size() && line >= 1) {
      return sourceLines.get(line - 1);
    }
    return null;
  }

  /** Scans the next token in the source code. */
  private void scanToken() {
    final char c = advance();
    switch (c) {
        // Braces and parentheses
      case '{' -> addToken(TokenType.LEFT_BRACE);
      case '}' -> addToken(TokenType.RIGHT_BRACE);
      case '(' -> addToken(TokenType.LEFT_PARENTHESIS);
      case ')' -> addToken(TokenType.RIGHT_PARENTHESIS);
        // Comparison operators
      case '!' -> addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
      case '=' -> addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
      case '>' -> addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
      case '<' -> addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
        // Conditional operators
      case ':' -> addToken(TokenType.COLON);
      case '?' -> addToken(TokenType.QUESTION);
        // Mathematical operations, right arrow and comments
      case '^' -> addToken(TokenType.CARET);
      case '-' -> addToken(match('>') ? TokenType.RIGHT_ARROW : TokenType.MINUS);
      case '%' -> addToken(TokenType.MODULO);
      case '+' -> addToken(TokenType.PLUS);
      case '/' -> {
        if (match('/')) comment();
        else if (match('*')) blockComment();
        else addToken(TokenType.SLASH);
      }
      case '*' -> addToken(TokenType.STAR);
        // Separators
      case ',' -> addToken(TokenType.COMMA);
      case '.' -> addToken(TokenType.DOT);
      case ';' -> addToken(TokenType.SEMICOLON);
        // Ignore whitespace
      case ' ', '\r', '\t' -> {}
      case '\n' -> {
        line++;
        col = 0;
      }
      case '"' -> string();
      default -> {
        if (isDigit(c)) number();
        else if (isAlpha(c)) identifier();
        else
          throw new LexingError(
              String.format("Unexpected character '%s'", c), line, col, getSourceLine(line));
      }
    }
  }

  /** Skips single-line comments. */
  private void comment() {
    while (peek() != '\n' && !isAtEnd()) advance();
  }

  /** Skips block comments. */
  private void blockComment() {
    int depth = 0;
    for (; ; ) {
      if (isAtEnd()) {
        throw new LexingError("Unterminated block comment", line, col, getSourceLine(line));
      }
      if (peek() == '\n') line++;
      else if (peek() == '*' && peekNext() == '/') {
        advance();
        advance();
        if (depth == 0) {
          return;
        }
        depth--;
      } else if (peek() == '/' && peekNext() == '*') {
        depth++;
      }
      advance();
    }
  }

  /** Scans identifiers. */
  private void identifier() {
    while (isAlphaNumeric(peek())) advance();
    final String text = source.substring(start, current);
    TokenType type = keywords.get(text);
    if (type == null) type = TokenType.IDENTIFIER;
    addToken(type);
  }

  /** Scans numeric literals. */
  private void number() {
    while (isDigit(peek())) advance();
    if (peek() == '.' && isDigit(peekNext())) {
      advance();
      while (isDigit(peek())) advance();
    }
    addToken(TokenType.NUMBER_LITERAL, Double.parseDouble(source.substring(start, current)));
  }

  /** Scans string literals. */
  private void string() {
    while (peek() != '"' && !isAtEnd()) {
      if (peek() == '\n') line++;
      advance();
    }
    if (isAtEnd()) {
      throw new LexingError("Unterminated string literal", line, col, getSourceLine(line));
    }
    advance(); // Consume closing "
    final String value = source.substring(start + 1, current - 1);
    addToken(TokenType.STRING_LITERAL, value);
  }

  /**
   * Checks if the next character matches the expected character and advances if true.
   *
   * @param expected The expected character.
   * @return True if the next character matches the expected character, false otherwise.
   */
  private boolean match(char expected) {
    if (isAtEnd() || source.charAt(current) != expected) return false;
    current++;
    col++;
    return true;
  }

  /**
   * Returns the current character without consuming it.
   *
   * @return The current character.
   */
  private char peek() {
    return isAtEnd() ? '\0' : source.charAt(current);
  }

  /**
   * Returns the next character without consuming it.
   *
   * @return The next character.
   */
  private char peekNext() {
    return current + 1 >= source.length() ? '\0' : source.charAt(current + 1);
  }

  /**
   * Checks if a character is alphabetic.
   *
   * @param c The character to be checked.
   * @return True if the character is alphabetic, false otherwise.
   */
  private boolean isAlpha(char c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
  }

  /**
   * Checks if a character is alphanumeric.
   *
   * @param c The character to be checked.
   * @return True if the character is alphanumeric, false otherwise.
   */
  private boolean isAlphaNumeric(char c) {
    return isAlpha(c) || isDigit(c);
  }

  /**
   * Checks if a character is a digit.
   *
   * @param c The character to be checked.
   * @return True if the character is a digit, false otherwise.
   */
  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }

  /**
   * Checks if the lexer has reached the end of the source code.
   *
   * @return True if the lexer has reached the end, false otherwise.
   */
  private boolean isAtEnd() {
    return current >= source.length();
  }

  /**
   * Advances to the next character in the source code.
   *
   * @return The next character.
   */
  private char advance() {
    col++;
    return source.charAt(current++);
  }

  /**
   * Adds a token to the list of tokens.
   *
   * @param type The type of the token.
   */
  private void addToken(TokenType type) {
    addToken(type, null);
  }

  /**
   * Adds a token with a literal value to the list of tokens.
   *
   * @param type The type of the token.
   * @param literal The literal value of the token.
   */
  private void addToken(TokenType type, Object literal) {
    final String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line, col));
  }

  /**
   * Splits the source code into lines.
   *
   * @param source The source code.
   * @return A list of lines.
   */
  private List<String> splitSourceIntoLines(String source) {
    final String[] lines = source.split("\\r?\\n");
    return List.of(lines);
  }
}
