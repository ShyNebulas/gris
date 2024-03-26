package com.github.gris.runtime.value;

import com.github.gris.typing.type.Type;

/** Represents a void value in the Gris language runtime. */
public class GrisVoid extends GrisType {
  /** Constructs a GrisVoid object. */
  public GrisVoid() {
    super(Type.VOID);
  }

  /**
   * Checks if this GrisVoid object is equal to another GrisType object.
   *
   * @param value The GrisType object to compare.
   * @return A new GrisBoolean object representing the result of the equality comparison.
   */
  @Override
  public GrisBoolean isEqual(GrisType value) {
    if (!(value instanceof GrisVoid)) {
      return new GrisBoolean(false);
    }
    return new GrisBoolean(true);
  }

  /**
   * Converts this GrisVoid object to a GrisString object.
   *
   * @return A new GrisString object representing the string representation of this void value.
   */
  @Override
  public GrisString toGrisString() {
    return new GrisString("Void");
  }
}
