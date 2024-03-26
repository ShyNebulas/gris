package com.github.gris.lexer;

/** Enum representing different token types used in lexical analysis. */
public enum TokenType {
  // Braces and parentheses
  LEFT_BRACE,
  RIGHT_BRACE,
  LEFT_PARENTHESIS,
  RIGHT_PARENTHESIS,

  // Comparison operators
  BANG,
  BANG_EQUAL,
  EQUAL,
  EQUAL_EQUAL,
  GREATER,
  GREATER_EQUAL,
  LESS,
  LESS_EQUAL,

  // Conditional operators
  COLON,
  QUESTION,

  // Keywords
  CLASS,
  DEF,
  VAL,
  FOR,
  WHILE,
  IF,
  ELSE,
  TRUE,
  FALSE,
  NULL,
  BOOLEAN,
  NUMBER,
  STRING,
  VOID,
  RETURN,
  SUPER,
  THIS,

  // Literals
  IDENTIFIER,
  NUMBER_LITERAL,
  STRING_LITERAL,

  // Logical operators
  AND,
  OR,

  // Mathematical operations
  CARET,
  MINUS,
  MODULO,
  PLUS,
  SLASH,
  STAR,

  // Separators
  COMMA,
  DOT,
  SEMICOLON,

  // Special
  RIGHT_ARROW,

  // End of file
  EOF,
}
