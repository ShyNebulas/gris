package com.github.gris.runtime.value;

import com.github.gris.ast.Parameter;
import com.github.gris.ast.stmt.Function;
import com.github.gris.runtime.ReturnError;
import com.github.gris.runtime.Environment;
import com.github.gris.runtime.Interpreter;
import com.github.gris.typing.type.Type;

import java.util.List;

/** Represents a function in the Gris language runtime. */
public class GrisFunction extends GrisCallable {
  /** The function declaration AST node. */
  private final Function declaration;

  /** The closure environment of the function. */
  private final Environment closure;

  /** Indicates whether the function is an initializer (constructor). */
  private final boolean isInitializer;

  /**
   * Constructs a GrisFunction object with the given function declaration, closure environment, and
   * initializer flag.
   *
   * @param declaration The function declaration AST node.
   * @param closure The closure environment of the function.
   * @param isInitializer Indicates whether the function is an initializer (constructor).
   */
  public GrisFunction(Function declaration, Environment closure, boolean isInitializer) {
    super(Type.FUNCTION);
    this.declaration = declaration;
    this.closure = closure;
    this.isInitializer = isInitializer;
  }

  /**
   * Binds the function to a class instance by adding the instance to the closure environment.
   *
   * @param instance The class instance to bind the function to.
   * @return A new GrisFunction object with the updated closure environment.
   */
  public GrisFunction bind(GrisClassInstance instance) {
    Environment environment = new Environment(closure);
    environment.define("this", instance);
    return new GrisFunction(declaration, environment, isInitializer);
  }

  /**
   * Executes the function with the provided arguments.
   *
   * @param interpreter The interpreter instance used for execution.
   * @param arguments The list of arguments passed to the function.
   * @return The result of the function execution.
   */
  @Override
  public GrisType call(Interpreter interpreter, List<GrisType> arguments) {
    final Environment environment = new Environment(closure);

    for (int i = 0; i < declaration.parameters.size(); i++) {
      environment.define(declaration.parameters.get(i).name.lexeme, arguments.get(i));
    }

    try {
      interpreter.executeBlock(declaration.body, environment);
    } catch (ReturnError error) {
      if (isInitializer) return closure.getAt(0, "this");
      return error.value;
    }

    if (isInitializer) return closure.getAt(0, "this");

    return new GrisVoid();
  }

  /**
   * Checks if this GrisFunction object is equal to another GrisType object.
   *
   * @param value The GrisType object to compare.
   * @return A new GrisBoolean object representing the result of the equality comparison.
   */
  @Override
  public GrisBoolean isEqual(GrisType value) {
    if (!(value instanceof GrisFunction)) {
      return new GrisBoolean(false);
    }
    return new GrisBoolean(this == value);
  }

  /**
   * Converts this GrisFunction object to a GrisString object.
   *
   * @return A GrisString object representing the name of the function.
   */
  @Override
  public GrisString toGrisString() {
    return new GrisString(this.declaration.name.lexeme);
  }
}
