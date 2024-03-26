package com.github.gris.ast.expr;

import com.github.gris.ast.visitor.ExprVisitor;
import com.github.gris.typing.type.Type;

/** Represents a literal expression. */
public class Literal extends Expr {
  /** The value of the literal. */
  public final Object value;

  /** The type of the literal. */
  public final Type type;

  /**
   * Constructs a literal expression with the given value and type.
   *
   * @param value The value of the literal.
   * @param type The type of the literal.
   */
  public Literal(Object value, Type type) {
    this.value = value;
    this.type = type;
  }

  /**
   * Accepts a visitor and performs an operation based on this literal expression.
   *
   * @param visitor The visitor.
   * @param <T> The return type of the visit operation.
   * @return The result of the visit operation.
   */
  @Override
  public <T> T accept(ExprVisitor<T> visitor) {
    return visitor.visitLiteralExpr(this);
  }
}
