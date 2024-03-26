package com.github.gris.runtime.union;

import com.github.gris.ast.expr.Expr;

public class ExprUnion implements ExprUnionTypeExpr {
    public Expr value;

    public ExprUnion(Expr expr) { this.value = expr; }
}
