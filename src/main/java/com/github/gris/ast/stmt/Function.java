package com.github.gris.ast.stmt;

import com.github.gris.ast.Parameter;
import com.github.gris.typing.type.TypeExpr;
import com.github.gris.ast.visitor.StmtVisitor;
import com.github.gris.lexer.Token;

import java.util.List;

/** Represents a function declaration statement. */
public class Function extends Stmt {
  /** The token representing the name of the function. */
  public final Token name;

  /** The list of parameters of the function. */
  public final List<Parameter> parameters;

  /** The return type expression of the function. */
  public final TypeExpr returnType;

  /** The list of statements comprising the body of the function. */
  public final List<Stmt> body;

  /**
   * Constructs a function declaration statement with the given name, parameters, return type, and
   * body.
   *
   * @param name The name of the function.
   * @param parameters The parameters of the function.
   * @param returnType The return type expression of the function.
   * @param body The body of the function.
   */
  public Function(Token name, List<Parameter> parameters, TypeExpr returnType, List<Stmt> body) {
    this.name = name;
    this.parameters = parameters;
    this.returnType = returnType;
    this.body = body;
  }

  /**
   * Accepts a visitor and performs an operation based on this function declaration statement.
   *
   * @param visitor The visitor.
   * @param <T> The return type of the visit operation.
   * @return The result of the visit operation.
   */
  @Override
  public <T> T accept(StmtVisitor<T> visitor) {
    return visitor.visitFunctionStmt(this);
  }
}
