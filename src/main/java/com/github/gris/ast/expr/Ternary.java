package com.github.gris.ast.expr;

import com.github.gris.ast.visitor.ExprVisitor;
import com.github.gris.lexer.Token;

/** Represents a ternary expression. */
public class Ternary extends Expr {
  /** The condition of the ternary expression. */
  public final Expr condition;

  /** The token representing the left operator. */
  final Token leftOperator;

  /** The 'then' branch of the ternary expression. */
  public final Expr thenBranch;

  /** The token representing the right operator. */
  final Token rightOperator;

  /** The 'else' branch of the ternary expression. */
  public final Expr elseBranch;

  /**
   * Constructs a ternary expression with the given condition, left operator, 'then' branch, right
   * operator, and 'else' branch.
   *
   * @param condition The condition of the ternary expression.
   * @param leftOperator The token representing the left operator.
   * @param thenBranch The 'then' branch of the ternary expression.
   * @param rightOperator The token representing the right operator.
   * @param elseBranch The 'else' branch of the ternary expression.
   */
  public Ternary(
      Expr condition, Token leftOperator, Expr thenBranch, Token rightOperator, Expr elseBranch) {
    this.condition = condition;
    this.leftOperator = leftOperator;
    this.thenBranch = thenBranch;
    this.rightOperator = rightOperator;
    this.elseBranch = elseBranch;
  }

  /**
   * Accepts a visitor and performs an operation based on this ternary expression.
   *
   * @param visitor The visitor.
   * @param <T> The return type of the visit operation.
   * @return The result of the visit operation.
   */
  @Override
  public <T> T accept(ExprVisitor<T> visitor) {
    return visitor.visitTernaryExpr(this);
  }
}
