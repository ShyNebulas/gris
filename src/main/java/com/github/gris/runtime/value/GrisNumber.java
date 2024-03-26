package com.github.gris.runtime.value;

import com.github.gris.typing.type.Type;

/** Represents a numeric value in the Gris language runtime. */
public class GrisNumber extends GrisType {

  /** The double value held by this GrisNumber object. */
  public final double value;

  /**
   * Constructs a GrisNumber object with the specified double value.
   *
   * @param value The double value to be stored.
   */
  public GrisNumber(double value) {
    super(Type.NUMBER);
    this.value = value;
  }

  /**
   * Checks if this GrisNumber object is equal to another GrisType object.
   *
   * @param value The GrisType object to compare.
   * @return A new GrisBoolean object representing the result of the equality comparison.
   */
  @Override
  public GrisBoolean isEqual(GrisType value) {
    if (!(value instanceof GrisNumber)) {
      return new GrisBoolean(false);
    }
    return new GrisBoolean(this.value == ((GrisNumber) value).value);
  }

  /**
   * Converts this GrisNumber object to a GrisString object.
   *
   * @return A new GrisString object representing the double value as a string.
   */
  @Override
  public GrisString toGrisString() {
    return new GrisString(String.valueOf(this.value));
  }
}
