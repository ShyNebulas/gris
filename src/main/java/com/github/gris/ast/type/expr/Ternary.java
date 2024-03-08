package com.github.gris.ast.type.expr;

import com.github.gris.ast.visitor.ExprVisitor;
import com.github.gris.token.Token;
import com.github.gris.ast.Expr;

public class Ternary extends Expr {
    public final Expr condition;
    final Token leftOperator;
    public final Expr thenBranch;
    final Token rightOperator;
    public final Expr elseBranch;

    public Ternary(Expr condition, Token leftOperator, Expr thenBranch, Token rightOperator, Expr elseBranch) {
        this.condition = condition;
        this.leftOperator = leftOperator;
        this.thenBranch = thenBranch;
        this.rightOperator = rightOperator;
        this.elseBranch = elseBranch;
    }

    @Override
    public <T> T accept(ExprVisitor<T> visitor) {
        return visitor.visitTernaryExpr(this);
    }
}
