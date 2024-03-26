package com.github.gris.ast.expr;

import com.github.gris.ast.visitor.ExprVisitor;
import org.apache.commons.lang3.builder.ToStringBuilder;

/** Represents an abstract expression. */
public abstract class Expr {
  /**
   * Accepts a visitor and performs an operation based on this expression.
   *
   * @param visitor The visitor.
   * @param <T> The return type of the visit operation.
   * @return The result of the visit operation.
   */
  public abstract <T> T accept(ExprVisitor<T> visitor);

  /**
   * Returns a string representation of this expression.
   *
   * @return The string representation.
   */
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
