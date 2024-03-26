package com.github.gris.ast.stmt;

import com.github.gris.ast.visitor.StmtVisitor;
import org.apache.commons.lang3.builder.ToStringBuilder;

/** Represents a statement in the abstract syntax tree. */
public abstract class Stmt {
  /**
   * Accepts a visitor and performs an operation based on this statement.
   *
   * @param visitor The visitor.
   * @param <T> The return type of the visit operation.
   * @return The result of the visit operation.
   */
  public abstract <T> T accept(StmtVisitor<T> visitor);

  /**
   * Returns a string representation of this statement.
   *
   * @return A string representation of this statement.
   */
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
