package com.github.gris.ast.stmt;

import com.github.gris.ast.expr.Expr;
import com.github.gris.ast.visitor.StmtVisitor;

/** Represents an expression statement. */
public class Expression extends Stmt {
  /** The expression of the statement. */
  public final Expr expression;

  /**
   * Constructs an expression statement with the given expression.
   *
   * @param expression The expression.
   */
  public Expression(Expr expression) {
    this.expression = expression;
  }

  /**
   * Accepts a visitor and performs an operation based on this expression statement.
   *
   * @param visitor The visitor.
   * @param <T> The return type of the visit operation.
   * @return The result of the visit operation.
   */
  @Override
  public <T> T accept(StmtVisitor<T> visitor) {
    return visitor.visitExpressionStmt(this);
  }
}
