package com.github.gris.runtime;

import com.github.gris.runtime.value.GrisType;

/** Represents an error when returning a value from a function. */
public class ReturnError extends RuntimeException {
  /** The value being returned. */
  public final GrisType value;

  /**
   * Constructs a ReturnError with the specified value.
   *
   * @param value The value being returned.
   */
  public ReturnError(GrisType value) {
    super(null, null, false, false);
    this.value = value;
  }
}
