package com.github.gris.lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.gris.token.Token;
import com.github.gris.token.Type;
import static com.github.gris.token.Type.*;
import com.github.gris.error.Error;

public final class Lexer {
  /** Our raw source code. */
  private final String source;

  /** Final generated list of tokens. */
  private final List<Token> tokens = new ArrayList<>();

  /** Points to the first character in the lexeme being scanned. */
  private int start = 0;

  /** Points to the character that is being considered while scanning. */
  private int current = 0;

  /** Points where our lexeme is related to the rest of the file (source line). */
  private int line = 1;

  /** TODO */
  private int col = 0;

  private static final Map<String, Type> keywords;

  static {
    keywords = new HashMap<>();
    keywords.put("and", AND);
    keywords.put("class", CLASS);
    keywords.put("else", ELSE);
    keywords.put("false", FALSE);
    keywords.put("for", FOR);
    keywords.put("fun", FUN);
    keywords.put("if", IF);
    keywords.put("nil", NIL);
    keywords.put("or", OR);
    keywords.put("print", PRINT);
    keywords.put("return", RETURN);
    keywords.put("super", SUPER);
    keywords.put("this", THIS);
    keywords.put("true", TRUE);
    keywords.put("var", VAR);
    keywords.put("while", WHILE);
  }

  public Lexer(String source) {
    this.source = source;
  }

  public List<Token> scanTokens() {
    while (!isAtEnd()) {
      // We are at the beginning of the next lexeme.
      start = current;
      scanToken();
    }

    tokens.add(new Token(EOF, "", null, line, col + 1));
    return tokens;
  }

  /** */
  private void scanToken() {
    char c = advance();
    switch (c) {
      case '(':
        addToken(LEFT_PAREN);
        break;
      case ')':
        addToken(RIGHT_PAREN);
        break;
      case '{':
        addToken(LEFT_BRACE);
        break;
      case '}':
        addToken(RIGHT_BRACE);
        break;
      case ',':
        addToken(COMMA);
        break;
      case '.':
        addToken(DOT);
        break;
      case '-':
        addToken(MINUS);
        break;
      case '+':
        addToken(PLUS);
        break;
      case ';':
        addToken(SEMICOLON);
        break;
      case '*':
        addToken(STAR);
        break;
      case ':':
        addToken(COLON);
        break;
      case '?':
        addToken(QUESTION);
      case '!':
        addToken(match('=') ? BANG_EQUAL : BANG);
        break;
      case '=':
        addToken(match('=') ? EQUAL_EQUAL : EQUAL);
        break;
      case '<':
        addToken(match('=') ? LESS_EQUAL : LESS);
        break;
      case '>':
        addToken(match('=') ? GREATER_EQUAL : GREATER);
        break;
      case '/':
        if (match('/')) {
          comment();
        } else if (match('*')) {
          blockComment();
        } else {
          addToken(SLASH);
        }
        break;

      case ' ':
      case '\r':
      case '\t':
        // Ignore whitespace
        break;

      case '\n':
        line++;
        col = 0;
        break;

      case '"':
        string();
        break;

      default:
        if (isDigit(c)) {
          number();
        } else if (isAlpha(c)) {
          identifier();
        } else {
          Error.report(line, col, "Unexpected character.", getLine());
        }
        break;
    }
  }

  /** Logic for single-line comments. */
  private void comment() {
    while (peek() != '\n' && !isAtEnd()) advance();
  }

  /** Logic for nested multi-line comments. */
  private void blockComment() {
    int depth = 0;
    for (; ; ) {
      if (isAtEnd()) {
        Error.report(line, col, "Unterminated block-comment.", "Test");
        return;
      }
      if (peek() == '\n') line++;
      else if (peek() == '*' && peekNext() == '/') {
        advance();
        advance();
        if (depth == 0) {
          return;
        }
        depth--;
      }
      // Nested comment
      else if (peek() == '/' && peekNext() == '*') {
        depth += 1;
      }
      advance();
    }
  }

  private void identifier() {
    while (isAlphaNumeric(peek())) advance();

    String text = source.substring(start, current);
    Type type = keywords.get(text);
    if (type == null) type = IDENTIFIER;
    addToken(type);
  }

  private void number() {
    while (isDigit(peek())) advance();

    // Look for a fractional part
    if (peek() == '.' && isDigit(peekNext())) {
      // Consume the "."
      advance();

      while (isDigit(peek())) advance();
    }

    addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
  }

  private void string() {
    while (peek() != '"' && !isAtEnd()) {
      if (peek() == '\n') line++;
      advance();
    }

    if (isAtEnd()) {
      Error.report(line, col, "Unterminated string.", getLine());
      return;
    }

    advance(); // The closing "

    // Trim the surrounding quotes
    String value = source.substring(start + 1, current - 1);
    addToken(STRING, value);
  }

  private boolean match(char expected) {
    if (source.charAt(current) != expected || isAtEnd()) return false;
    current++;
    col++;
    return true;
  }

  private char peek() {
    if (isAtEnd()) return '\0';
    return source.charAt(current);
  }

  private char peekNext() {
    if (current + 1 >= source.length()) return '\0';
    return source.charAt(current + 1);
  }

  private boolean isAlpha(char c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
  }

  private boolean isAlphaNumeric(char c) {
    return isAlpha(c) || isDigit(c);
  }

  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }

  /**
   * Determines whether we have reached the end of our source file.
   *
   * @return If we have reached the end of our source file, it returns true; otherwise, it returns
   *     false.
   */
  private boolean isAtEnd() {
    return current >= source.length();
  }

  /**
   * Moves us one step further in our source file.
   *
   * @return Consumes the next character in the source file and returns it.
   */
  private char advance() {
    col++;
    return source.charAt(current++);
  }

  /**
   * It takes our current lexeme and creates a token for it - if our lexeme has no literal
   * associated with it.
   *
   * @param type Our token's type.
   */
  private void addToken(Type type) {
    addToken(type, null);
  }

  /**
   * It takes our current lexeme and creates a token for it.
   *
   * @param type Our token's type.
   * @param literal Our lexeme's literal value.
   */
  private void addToken(Type type, Object literal) {
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line, col));
  }

  private String getLine() {
    // TODO: Revisit
    StringBuilder left, right;
    int i, k;

    i = start - 1;
    left = new StringBuilder();
    while (source.charAt(i) != '\n') {
      left.append(source.charAt(i));
      i--;
    }

    k = start + 1;
    right = new StringBuilder();
    while (source.charAt(k) != '\n') {
      right.append(source.charAt(k));
      k++;
    }

    if (source.charAt(current - 1) == '\n') {
      return source.charAt(current) + right.toString();
    }

    if (source.charAt(current + 1) == '\n') {
      return left.toString() + source.charAt(current);
    }

    return left.toString() + source.charAt(current) + right;
  }
}
