package com.github.gris.token;

import org.apache.commons.lang3.builder.ToStringBuilder;

public final class Token {
  public final Type type;
  public final String lexeme;
  public final Object literal;
  public final int line, col;

  public Token(Type type, String lexeme, Object literal, int line, int col) {
    this.type = type;
    this.lexeme = lexeme;
    this.literal = literal;
    this.line = line;
    this.col = col;
  }

  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
