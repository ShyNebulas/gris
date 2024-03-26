package com.github.gris.runtime.value;

import com.github.gris.runtime.Interpreter;
import com.github.gris.typing.type.Type;

import java.util.List;
import java.util.Map;

/** Represents a class entity in the Gris language runtime. T */
public class GrisClass extends GrisCallable {
  /** The name of the class. */
  final String name;

  /** The superclass of the class. */
  final GrisClass superclass;

  /** The map of methods associated with the class. */
  private final Map<String, GrisFunction> methods;

  /**
   * Constructs a GrisClass object with the specified name, superclass, and methods.
   *
   * @param name The name of the class.
   * @param superclass The superclass of the class.
   * @param methods The map of methods associated with the class.
   */
  public GrisClass(String name, GrisClass superclass, Map<String, GrisFunction> methods) {
    super(Type.CLASS);
    this.name = name;
    this.superclass = superclass;
    this.methods = methods;
  }

  /**
   * Calls the class constructor and returns a new instance of the class.
   *
   * @param interpreter The interpreter instance used for execution.
   * @param arguments The list of arguments passed to the constructor.
   * @return A new instance of the class.
   */
  @Override
  public GrisType call(Interpreter interpreter, List<GrisType> arguments) {
    GrisClassInstance instance = new GrisClassInstance(this);
    GrisFunction initializer = findMethod("constructor");
    if (initializer != null) {
      initializer.bind(instance).call(interpreter, arguments);
    }
    return instance;
  }

  /**
   * Finds a method with the given name in the class or its superclass.
   *
   * @param name The name of the method to find.
   * @return The method with the specified name, or null if not found.
   */
  public GrisFunction findMethod(String name) {
    if (methods.containsKey(name)) {
      return methods.get(name);
    }

    if (superclass != null) {
      return superclass.findMethod(name);
    }

    return null;
  }

  /**
   * Checks if this GrisClass object is equal to another GrisType object.
   *
   * @param value The GrisType object to compare.
   * @return A new GrisBoolean object representing the result of the equality comparison.
   */
  @Override
  public GrisBoolean isEqual(GrisType value) {
    if (!(value instanceof GrisClass)) {
      return new GrisBoolean(false);
    }
    return new GrisBoolean(this == ((GrisClass) value));
  }

  /**
   * Converts this GrisClass object to a GrisString object.
   *
   * @return A new GrisString object representing the class name.
   */
  @Override
  public GrisString toGrisString() {
    return new GrisString(this.name);
  }
}
