package com.github.gris.ast.expr;

import com.github.gris.ast.visitor.ExprVisitor;
import com.github.gris.lexer.Token;

/** Represents a 'super' expression. */
public class Super extends Expr {
  /** The token representing the 'super' keyword. */
  public final Token keyword;

  /** The token representing the method being accessed from the superclass. */
  public final Token method;

  /**
   * Constructs a 'super' expression with the given 'super' keyword and method token.
   *
   * @param keyword The token representing the 'super' keyword.
   * @param method The token representing the method being accessed from the superclass.
   */
  public Super(Token keyword, Token method) {
    this.keyword = keyword;
    this.method = method;
  }

  /**
   * Accepts a visitor and performs an operation based on this 'super' expression.
   *
   * @param visitor The visitor.
   * @param <T> The return type of the visit operation.
   * @return The result of the visit operation.
   */
  @Override
  public <T> T accept(ExprVisitor<T> visitor) {
    return visitor.visitSuperExpr(this);
  }
}
