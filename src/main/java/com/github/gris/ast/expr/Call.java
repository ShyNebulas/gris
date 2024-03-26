package com.github.gris.ast.expr;

import com.github.gris.ast.visitor.ExprVisitor;
import com.github.gris.lexer.Token;

import java.util.List;

/** Represents a function call expression. */
public class Call extends Expr {
  /** The expression representing the function being called. */
  public final Expr callee;

  /** The token representing the parentheses enclosing the arguments. */
  public final Token parenthesis;

  /** The list of arguments passed to the function call. */
  public final List<Expr> arguments;

  /**
   * Constructs a function call expression with the given callee, parentheses token, and arguments.
   *
   * @param callee The expression representing the function being called.
   * @param parenthesis The token representing the parentheses.
   * @param arguments The list of arguments passed to the function call.
   */
  public Call(Expr callee, Token parenthesis, List<Expr> arguments) {
    this.callee = callee;
    this.parenthesis = parenthesis;
    this.arguments = arguments;
  }

  /**
   * Accepts a visitor and performs an operation based on this function call expression.
   *
   * @param visitor The visitor.
   * @param <T> The return type of the visit operation.
   * @return The result of the visit operation.
   */
  @Override
  public <T> T accept(ExprVisitor<T> visitor) {
    return visitor.visitCallExpr(this);
  }
}
