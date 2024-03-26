package com.github.gris.builtin;

import com.github.gris.runtime.value.GrisCallable;
import com.github.gris.typing.type.FunctionTypeExpr;

/** Represents a built-in function. */
public class Builtin {
  /** The name of the built-in function. */
  public final String name;

  /** The callable object representing the built-in function. */
  public final GrisCallable function;

  /** The function type expression representing the type of the built-in function. */
  public final FunctionTypeExpr type;

  /**
   * Constructs a built-in function with the given name, callable object, and function type
   * expression.
   *
   * @param name The name of the built-in function.
   * @param function The callable object representing the built-in function.
   * @param type The function type expression representing the type of the built-in function.
   */
  public Builtin(String name, GrisCallable function, FunctionTypeExpr type) {
    this.name = name;
    this.function = function;
    this.type = type;
  }
}
