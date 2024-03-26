package com.github.gris.ast.expr;

import com.github.gris.ast.visitor.ExprVisitor;
import com.github.gris.lexer.Token;

/** Represents a get expression. */
public class Get extends Expr {
  /** The object from which to get the property. */
  public final Expr object;

  /** The token representing the name of the property to get. */
  public final Token name;

  /**
   * Constructs a get expression with the given object and property name.
   *
   * @param object The object from which to get the property.
   * @param name The name of the property to get.
   */
  public Get(Expr object, Token name) {
    this.object = object;
    this.name = name;
  }

  /**
   * Accepts a visitor and performs an operation based on this get expression.
   *
   * @param visitor The visitor.
   * @param <T> The return type of the visit operation.
   * @return The result of the visit operation.
   */
  @Override
  public <T> T accept(ExprVisitor<T> visitor) {
    return visitor.visitGetExpr(this);
  }
}
