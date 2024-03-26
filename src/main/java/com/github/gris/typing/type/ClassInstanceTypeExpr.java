package com.github.gris.typing.type;

/** Represents a type expression for a class instance. */
public class ClassInstanceTypeExpr extends TypeExpr {
  /** The class type expression. */
  public final ClassTypeExpr klass;

  /**
   * Constructs a ClassInstanceTypeExpr with the specified class type expression.
   *
   * @param klass The class type expression.
   */
  public ClassInstanceTypeExpr(ClassTypeExpr klass) {
    super(Type.CLASS_INSTANCE);
    this.klass = klass;
  }
}
