package com.github.gris.ast.stmt;

import com.github.gris.ast.expr.Expr;
import com.github.gris.ast.visitor.StmtVisitor;

/** Represents a while loop statement. */
public class While extends Stmt {
  /** The condition expression of the while loop. */
  public final Expr condition;

  /** The body statement of the while loop. */
  public final Stmt body;

  /**
   * Constructs a while loop statement with the given condition and body.
   *
   * @param condition The condition expression.
   * @param body The body statement.
   */
  public While(Expr condition, Stmt body) {
    this.condition = condition;
    this.body = body;
  }

  /**
   * Accepts a visitor and performs an operation based on this while loop statement.
   *
   * @param visitor The visitor.
   * @param <T> The return type of the visit operation.
   * @return The result of the visit operation.
   */
  @Override
  public <T> T accept(StmtVisitor<T> visitor) {
    return visitor.visitWhileStmt(this);
  }
}
