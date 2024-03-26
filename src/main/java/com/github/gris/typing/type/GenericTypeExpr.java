package com.github.gris.typing.type;

import java.util.List;

/** Represents a generic type expression that can match against a list of specified types. */
public class GenericTypeExpr extends TypeExpr {

  /** The list of types that this generic type expression can match against. */
  public final List<Type> types;

  /**
   * Constructs a GenericTypeExpr with the specified types.
   *
   * @param types The types that this generic type expression can match against.
   */
  public GenericTypeExpr(Type... types) {
    super(Type.VOID);
    this.types = List.of(types);
  }

  /**
   * Checks if the provided type matches any of the types in this generic type expression.
   *
   * @param type The type to check against.
   * @return True if the provided type matches any of the types in this generic type expression,
   *     false otherwise.
   */
  public boolean match(Type type) {
    return types.contains(type);
  }
}
