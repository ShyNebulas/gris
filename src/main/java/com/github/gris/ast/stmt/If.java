package com.github.gris.ast.stmt;

import com.github.gris.ast.expr.Expr;
import com.github.gris.ast.visitor.StmtVisitor;

/** Represents an if statement. */
public class If extends Stmt {
  /** The condition expression of the if statement. */
  public final Expr condition;

  /** The statement representing the body of the 'then' branch. */
  public final Stmt thenBranch;

  /** The statement representing the body of the 'else' branch. */
  public final Stmt elseBranch;

  /**
   * Constructs an if statement with the given condition, 'then' branch, and 'else' branch.
   *
   * @param condition The condition expression.
   * @param thenBranch The statement representing the body of the 'then' branch.
   * @param elseBranch The statement representing the body of the 'else' branch.
   */
  public If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
    this.condition = condition;
    this.thenBranch = thenBranch;
    this.elseBranch = elseBranch;
  }

  /**
   * Accepts a visitor and performs an operation based on this if statement.
   *
   * @param visitor The visitor.
   * @param <T> The return type of the visit operation.
   * @return The result of the visit operation.
   */
  @Override
  public <T> T accept(StmtVisitor<T> visitor) {
    return visitor.visitIfStmt(this);
  }
}
