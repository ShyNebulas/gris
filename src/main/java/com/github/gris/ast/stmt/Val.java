package com.github.gris.ast.stmt;

import com.github.gris.typing.type.TypeExpr;
import com.github.gris.ast.expr.Expr;
import com.github.gris.ast.visitor.StmtVisitor;
import com.github.gris.lexer.Token;

/** Represents a variable declaration statement. */
public class Val extends Stmt {
  /** The token representing the name of the variable. */
  public final Token name;

  /** The type expression of the variable. */
  public final TypeExpr type;

  /** The initializer expression of the variable. */
  public final Expr initializer;

  /**
   * Constructs a variable declaration statement with the given name, type expression, and
   * initializer.
   *
   * @param name The name of the variable.
   * @param type The type expression of the variable.
   * @param initializer The initializer expression of the variable.
   */
  public Val(Token name, TypeExpr type, Expr initializer) {
    this.name = name;
    this.type = type;
    this.initializer = initializer;
  }

  /**
   * Accepts a visitor and performs an operation based on this variable declaration statement.
   *
   * @param visitor The visitor.
   * @param <T> The return type of the visit operation.
   * @return The result of the visit operation.
   */
  @Override
  public <T> T accept(StmtVisitor<T> visitor) {
    return visitor.visitValStmt(this);
  }
}
