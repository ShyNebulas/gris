package com.github.gris.ast.expr;

import com.github.gris.ast.visitor.ExprVisitor;
import com.github.gris.lexer.Token;

/** Represents a variable expression. */
public class Variable extends Expr {
  /** The token representing the name of the variable. */
  public final Token name;

  /**
   * Constructs a variable expression with the given variable name token.
   *
   * @param name The token representing the name of the variable.
   */
  public Variable(Token name) {
    this.name = name;
  }

  /**
   * Accepts a visitor and performs an operation based on this variable expression.
   *
   * @param visitor The visitor.
   * @param <T> The return type of the visit operation.
   * @return The result of the visit operation.
   */
  @Override
  public <T> T accept(ExprVisitor<T> visitor) {
    return visitor.visitVariableExpr(this);
  }
}
