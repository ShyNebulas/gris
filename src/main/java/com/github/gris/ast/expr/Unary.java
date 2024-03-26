package com.github.gris.ast.expr;

import com.github.gris.ast.visitor.ExprVisitor;
import com.github.gris.lexer.Token;

/** Represents a unary expression. */
public class Unary extends Expr {
  /** The operator used in the unary expression. */
  public final Token operator;

  /** The operand of the unary expression. */
  public final Expr right;

  /**
   * Constructs a unary expression with the given operator and operand.
   *
   * @param operator The operator.
   * @param right The operand.
   */
  public Unary(Token operator, Expr right) {
    this.operator = operator;
    this.right = right;
  }

  /**
   * Accepts a visitor and performs an operation based on this unary expression.
   *
   * @param visitor The visitor.
   * @param <T> The return type of the visit operation.
   * @return The result of the visit operation.
   */
  @Override
  public <T> T accept(ExprVisitor<T> visitor) {
    return visitor.visitUnaryExpr(this);
  }
}
