package com.github.gris.typing.type;

import org.apache.commons.lang3.builder.ToStringBuilder;

/** Represents a type expression. */
public class TypeExpr {

  /** The type associated with this type expression. */
  public final Type type;

  /**
   * Constructs a TypeExpr with the specified type.
   *
   * @param type The type associated with this type expression.
   */
  public TypeExpr(Type type) {
    this.type = type;
  }

  /**
   * Matches two type expressions.
   *
   * @param t1 The first type expression to match.
   * @param t2 The second type expression to match.
   * @return True if the type expressions match, false otherwise.
   */
  public static boolean matchTypeExpr(TypeExpr t1, TypeExpr t2) {
    if (t1 instanceof GenericTypeExpr) return ((GenericTypeExpr) t1).match(t2.type);
    if (t2 instanceof GenericTypeExpr) return ((GenericTypeExpr) t2).match(t1.type);

    if (t1 instanceof FunctionTypeExpr) {
      if (!(t2 instanceof FunctionTypeExpr)) return false;
      return ((FunctionTypeExpr) t1).matchSignature(((FunctionTypeExpr) t2));
    }

    return t1.type == t2.type;
  }

  /**
   * Returns a string representation of this type expression.
   *
   * @return A string representation of this type expression.
   */
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
