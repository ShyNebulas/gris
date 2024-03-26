package com.github.gris.ast.expr;

import com.github.gris.ast.visitor.ExprVisitor;
import com.github.gris.lexer.Token;

/** Represents a logical expression. */
public class Logical extends Expr {
  /** The left operand of the logical expression. */
  public final Expr left;

  /** The operator used in the logical expression. */
  public final Token operator;

  /** The right operand of the logical expression. */
  public final Expr right;

  /**
   * Constructs a logical expression with the given left operand, operator, and right operand.
   *
   * @param left The left operand.
   * @param operator The operator.
   * @param right The right operand.
   */
  public Logical(Expr left, Token operator, Expr right) {
    this.left = left;
    this.operator = operator;
    this.right = right;
  }

  /**
   * Accepts a visitor and performs an operation based on this logical expression.
   *
   * @param visitor The visitor.
   * @param <T> The return type of the visit operation.
   * @return The result of the visit operation.
   */
  @Override
  public <T> T accept(ExprVisitor<T> visitor) {
    return visitor.visitLogicalExpr(this);
  }
}
