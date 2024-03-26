package com.github.gris.typing.type;

/** Represents a generic type expression that includes all built-in types. */
public class GenericAllTypeExpr extends GenericTypeExpr {

  /** Constructs a GenericAllTypeExpr that includes all built-in types. */
  public GenericAllTypeExpr() {
    super(
        Type.BOOLEAN,
        Type.CLASS,
        Type.CLASS_INSTANCE,
        Type.FUNCTION,
        Type.IDENTIFIER,
        Type.NUMBER,
        Type.STRING,
        Type.VOID);
  }
}
