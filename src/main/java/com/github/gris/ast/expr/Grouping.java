package com.github.gris.ast.expr;

import com.github.gris.ast.visitor.ExprVisitor;

/** Represents a grouping expression. */
public class Grouping extends Expr {
  /** The expression enclosed within the grouping. */
  public final Expr expression;

  /**
   * Constructs a grouping expression with the given enclosed expression.
   *
   * @param expression The expression enclosed within the grouping.
   */
  public Grouping(Expr expression) {
    this.expression = expression;
  }

  /**
   * Accepts a visitor and performs an operation based on this grouping expression.
   *
   * @param visitor The visitor.
   * @param <T> The return type of the visit operation.
   * @return The result of the visit operation.
   */
  @Override
  public <T> T accept(ExprVisitor<T> visitor) {
    return visitor.visitGroupingExpr(this);
  }
}
