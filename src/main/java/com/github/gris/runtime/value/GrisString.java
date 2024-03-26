package com.github.gris.runtime.value;

import com.github.gris.typing.type.Type;

import java.util.Objects;

/** Represents a string value in the Gris language runtime. */
public class GrisString extends GrisType {
  /** The string value held by this GrisString object. */
  public final String value;

  /**
   * Constructs a GrisString object with the specified string value.
   *
   * @param value The string value to be stored.
   */
  public GrisString(String value) {
    super(Type.STRING);
    this.value = value;
  }

  /**
   * Checks if this GrisString object is equal to another GrisType object.
   *
   * @param value The GrisType object to compare.
   * @return A new GrisBoolean object representing the result of the equality comparison.
   */
  @Override
  public GrisBoolean isEqual(GrisType value) {
    if (!(value instanceof GrisString)) {
      return new GrisBoolean(false);
    }
    return new GrisBoolean(Objects.equals(this.value, ((GrisString) value).value));
  }

  /**
   * Converts this GrisString object to a GrisString object (identity operation).
   *
   * @return This GrisString object itself.
   */
  @Override
  public GrisString toGrisString() {
    return new GrisString(this.value);
  }
}
