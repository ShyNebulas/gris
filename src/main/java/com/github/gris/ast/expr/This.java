package com.github.gris.ast.expr;

import com.github.gris.ast.visitor.ExprVisitor;
import com.github.gris.lexer.Token;

/** Represents a 'this' expression. */
public class This extends Expr {
  /** The token representing the 'this' keyword. */
  public final Token keyword;

  /**
   * Constructs a 'this' expression with the given 'this' keyword token.
   *
   * @param keyword The token representing the 'this' keyword.
   */
  public This(Token keyword) {
    this.keyword = keyword;
  }

  /**
   * Accepts a visitor and performs an operation based on this 'this' expression.
   *
   * @param visitor The visitor.
   * @param <T> The return type of the visit operation.
   * @return The result of the visit operation.
   */
  @Override
  public <T> T accept(ExprVisitor<T> visitor) {
    return visitor.visitThisExpr(this);
  }
}
