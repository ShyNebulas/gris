package com.github.gris.ast.expr;

import com.github.gris.ast.visitor.ExprVisitor;
import com.github.gris.lexer.Token;

/** Represents a set expression. */
public class Set extends Expr {
  /** The object whose property is being set. */
  public final Expr object;

  /** The token representing the name of the property being set. */
  public final Token name;

  /** The value to set to the property. */
  public final Expr value;

  /**
   * Constructs a set expression with the given object, property name, and value.
   *
   * @param object The object whose property is being set.
   * @param name The name of the property being set.
   * @param value The value to set to the property.
   */
  public Set(Expr object, Token name, Expr value) {
    this.object = object;
    this.name = name;
    this.value = value;
  }

  /**
   * Accepts a visitor and performs an operation based on this set expression.
   *
   * @param visitor The visitor.
   * @param <T> The return type of the visit operation.
   * @return The result of the visit operation.
   */
  @Override
  public <T> T accept(ExprVisitor<T> visitor) {
    return visitor.visitSetExpr(this);
  }
}
