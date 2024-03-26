package com.github.gris.ast.expr;

import com.github.gris.ast.visitor.ExprVisitor;
import com.github.gris.lexer.Token;

/** Represents an assignment expression. */
public class Assign extends Expr {
  /** The name of the variable being assigned. */
  public final Token name;

  /** The value to be assigned. */
  public final Expr value;

  /**
   * Constructs an assignment expression with the given name and value.
   *
   * @param name The name of the variable.
   * @param value The value to be assigned.
   */
  public Assign(Token name, Expr value) {
    this.name = name;
    this.value = value;
  }

  /**
   * Accepts a visitor and performs an operation based on this assignment expression.
   *
   * @param visitor The visitor.
   * @param <T> The return type of the visit operation.
   * @return The result of the visit operation.
   */
  @Override
  public <T> T accept(ExprVisitor<T> visitor) {
    return visitor.visitAssignExpr(this);
  }
}
