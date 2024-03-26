package com.github.gris.runtime.value;

import com.github.gris.lexer.Token;
import com.github.gris.typing.type.Type;

import java.util.HashMap;
import java.util.Map;

/** Represents an instance of a GrisClass in the Gris language runtime. */
public class GrisClassInstance extends GrisType {
  /** The GrisClass associated with this instance. */
  private final GrisClass klass;

  /** The map of fields associated with this instance. */
  private final Map<String, GrisType> fields = new HashMap<>();

  /**
   * Constructs a GrisClassInstance object associated with the given GrisClass.
   *
   * @param klass The GrisClass associated with this instance.
   */
  public GrisClassInstance(GrisClass klass) {
    super(Type.CLASS_INSTANCE);
    this.klass = klass;
  }

  /**
   * Retrieves the value of a field or method associated with this instance.
   *
   * @param name The token representing the name of the field or method.
   * @return The value of the field or the result of calling the method.
   */
  public GrisType get(Token name) {
    if (fields.containsKey(name.lexeme)) {
      return fields.get(name.lexeme);
    }

    GrisFunction method = klass.findMethod(name.lexeme);
    return method.bind(this);
  }

  /**
   * Sets the value of a field associated with this instance.
   *
   * @param name The token representing the name of the field.
   * @param value The value to be set for the field.
   */
  public void set(Token name, GrisType value) {
    fields.put(name.lexeme, value);
  }

  /**
   * Checks if this GrisClassInstance object is equal to another GrisType object.
   *
   * @param value The GrisType object to compare.
   * @return A new GrisBoolean object representing the result of the equality comparison.
   */
  @Override
  public GrisBoolean isEqual(GrisType value) {
    if (!(value instanceof GrisClassInstance)) {
      return new GrisBoolean(false);
    }
    return new GrisBoolean(this == value);
  }

  /**
   * Converts this GrisClassInstance object to a GrisString object.
   *
   * @return A GrisString object representing the string representation of this instance.
   */
  @Override
  public GrisString toGrisString() {
    return new GrisString(String.format("<%s instance>", this.klass.name));
  }
}
