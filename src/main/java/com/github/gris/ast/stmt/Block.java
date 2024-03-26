package com.github.gris.ast.stmt;

import com.github.gris.ast.visitor.StmtVisitor;

import java.util.List;

/** Represents a block statement. */
public class Block extends Stmt {
  /** The list of statements contained within the block. */
  public final List<Stmt> statements;

  /**
   * Constructs a block statement with the given list of statements.
   *
   * @param statements The list of statements.
   */
  public Block(List<Stmt> statements) {
    this.statements = statements;
  }

  /**
   * Accepts a visitor and performs an operation based on this block statement.
   *
   * @param visitor The visitor.
   * @param <T> The return type of the visit operation.
   * @return The result of the visit operation.
   */
  @Override
  public <T> T accept(StmtVisitor<T> visitor) {
    return visitor.visitBlockStmt(this);
  }
}
