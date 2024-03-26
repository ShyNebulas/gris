package com.github.gris.ast.visitor;

import com.github.gris.ast.expr.*;

/**
 * A visitor interface for expression nodes.
 *
 * @param <T> The type of the result returned by visit operations.
 */
public interface ExprVisitor<T> {
  T visitAssignExpr(Assign expr);

  T visitBinaryExpr(Binary expr);

  T visitCallExpr(Call expr);

  T visitGetExpr(Get expr);

  T visitGroupingExpr(Grouping expr);

  T visitLiteralExpr(Literal expr);

  T visitLogicalExpr(Logical expr);

  T visitSetExpr(Set expr);

  T visitSuperExpr(Super expr);

  T visitTernaryExpr(Ternary expr);

  T visitThisExpr(This expr);

  T visitUnaryExpr(Unary expr);

  T visitVariableExpr(Variable expr);
}
