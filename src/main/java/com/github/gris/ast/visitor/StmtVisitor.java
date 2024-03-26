package com.github.gris.ast.visitor;

import com.github.gris.ast.stmt.*;
import com.github.gris.ast.stmt.Class;

/**
 * A visitor interface for statement nodes.
 *
 * @param <T> The type of the result returned by visit operations.
 */
public interface StmtVisitor<T> {
  T visitBlockStmt(Block stmt);

  T visitClassStmt(Class stmt);

  T visitExpressionStmt(Expression stmt);

  T visitFunctionStmt(Function stmt);

  T visitIfStmt(If stmt);

  T visitReturnStmt(Return stmt);

  T visitValStmt(Val stmt);

  T visitWhileStmt(While stmt);
}
