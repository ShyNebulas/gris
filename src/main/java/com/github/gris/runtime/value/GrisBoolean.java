package com.github.gris.runtime.value;

import com.github.gris.typing.type.Type;

/** Represents a boolean value in the Gris language runtime. */
public class GrisBoolean extends GrisType {
  /** The boolean value held by this object. */
  public final boolean value;

  /**
   * Constructs a GrisBoolean object with the given boolean value.
   *
   * @param value The boolean value to be stored.
   */
  public GrisBoolean(boolean value) {
    super(Type.BOOLEAN);
    this.value = value;
  }

  /**
   * Checks if this GrisBoolean object is equal to another GrisType object.
   *
   * @param value The GrisType object to compare.
   * @return A new GrisBoolean object representing the result of the equality comparison.
   */
  @Override
  public GrisBoolean isEqual(GrisType value) {
    if (!(value instanceof GrisBoolean)) {
      return new GrisBoolean(false);
    }
    return new GrisBoolean(this.value == ((GrisBoolean) value).value);
  }

  /**
   * Converts this GrisBoolean object to a GrisString object.
   *
   * @return A new GrisString object representing the boolean value as a string.
   */
  @Override
  public GrisString toGrisString() {
    return new GrisString(String.valueOf(this.value));
  }
}
