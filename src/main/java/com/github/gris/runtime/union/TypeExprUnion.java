package com.github.gris.runtime.union;

import com.github.gris.typing.type.TypeExpr;

public class TypeExprUnion implements ExprUnionTypeExpr {
    public TypeExpr value;

    public TypeExprUnion(TypeExpr typeExpr) { this.value = typeExpr; }
}
