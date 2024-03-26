package com.github.gris.ast.expr;

import com.github.gris.ast.visitor.ExprVisitor;
import com.github.gris.lexer.Token;

/** Represents a binary expression. */
public class Binary extends Expr {
  /** The left operand of the binary expression. */
  public final Expr left;

  /** The operator used in the binary expression. */
  public final Token operator;

  /** The right operand of the binary expression. */
  public final Expr right;

  /**
   * Constructs a binary expression with the given left operand, operator, and right operand.
   *
   * @param left The left operand.
   * @param operator The operator.
   * @param right The right operand.
   */
  public Binary(Expr left, Token operator, Expr right) {
    this.left = left;
    this.operator = operator;
    this.right = right;
  }

  /**
   * Accepts a visitor and performs an operation based on this binary expression.
   *
   * @param visitor The visitor.
   * @param <T> The return type of the visit operation.
   * @return The result of the visit operation.
   */
  @Override
  public <T> T accept(ExprVisitor<T> visitor) {
    return visitor.visitBinaryExpr(this);
  }
}
