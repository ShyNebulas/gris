package com.github.gris.runtime.value;

import com.github.gris.typing.type.Type;

/** Represents a generic value in the Gris language runtime. */
public abstract class GrisType {
  /** The type of the Gris value. */
  final Type type;

  /**
   * Constructs a GrisType object with the specified type.
   *
   * @param type The type of the Gris value.
   */
  public GrisType(Type type) {
    this.type = type;
  }

  /**
   * Checks if this GrisType object is equal to another GrisType object.
   *
   * @param value The GrisType object to compare.
   * @return A new GrisBoolean object representing the result of the equality comparison.
   */
  public abstract GrisBoolean isEqual(GrisType value);

  /**
   * Converts this GrisType object to a GrisString object.
   *
   * @return A new GrisString object representing the string representation of this value.
   */
  public abstract GrisString toGrisString();
}
