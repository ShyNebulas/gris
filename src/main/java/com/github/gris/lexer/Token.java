package com.github.gris.lexer;

import org.apache.commons.lang3.builder.ToStringBuilder;

/** Represents a token in the lexed source code. */
public final class Token {
  /** The type of the token. */
  public final TokenType type;

  /** The lexeme of the token. */
  public final String lexeme;

  /** The literal value of the token. */
  public final Object literal;

  /** The line number where the token occurs. */
  public final int line;

  /** The column number where the token occurs. */
  public final int col;

  /**
   * Constructs a Token object with the provided information.
   *
   * @param type The type of the token.
   * @param lexeme The lexeme of the token.
   * @param literal The literal value of the token.
   * @param line The line number where the token occurs.
   * @param col The column number where the token occurs.
   */
  public Token(TokenType type, String lexeme, Object literal, int line, int col) {
    this.type = type;
    this.lexeme = lexeme;
    this.literal = literal;
    this.line = line;
    this.col = col;
  }

  /**
   * Returns a string representation of the token.
   *
   * @return The string representation of the token.
   */
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
