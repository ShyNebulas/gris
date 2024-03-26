package com.github.gris.runtime.value;

import java.util.List;

import com.github.gris.typing.type.Type;
import com.github.gris.runtime.Interpreter;

/** Represents a callable entity in the Gris language runtime. */
public abstract class GrisCallable extends GrisType {
  /**
   * Constructs a GrisCallable object with the specified type.
   *
   * @param type The type of the callable object.
   */
  public GrisCallable(Type type) {
    super(type);
  }

  /**
   * Executes the callable object with the provided arguments.
   *
   * @param interpreter The interpreter instance used for execution.
   * @param arguments The list of arguments passed to the callable.
   * @return The result of the callable execution.
   */
  public abstract GrisType call(Interpreter interpreter, List<GrisType> arguments);
}
