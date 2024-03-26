package com.github.gris.ast.stmt;

import com.github.gris.ast.expr.Expr;
import com.github.gris.ast.visitor.StmtVisitor;
import com.github.gris.lexer.Token;

/** Represents a return statement. */
public class Return extends Stmt {
  /** The token representing the 'return' keyword. */
  public final Token keyword;

  /** The expression representing the returned value. */
  public final Expr value;

  /**
   * Constructs a return statement with the given 'return' keyword token and returned value
   * expression.
   *
   * @param keyword The 'return' keyword token.
   * @param value The returned value expression.
   */
  public Return(Token keyword, Expr value) {
    this.keyword = keyword;
    this.value = value;
  }

  /**
   * Accepts a visitor and performs an operation based on this return statement.
   *
   * @param visitor The visitor.
   * @param <T> The return type of the visit operation.
   * @return The result of the visit operation.
   */
  @Override
  public <T> T accept(StmtVisitor<T> visitor) {
    return visitor.visitReturnStmt(this);
  }
}
