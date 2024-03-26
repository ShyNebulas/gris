package com.github.gris.typing;

/** Represents an error that occurs during typing. */
public class TypingError extends RuntimeException {

  /** The line number where the error occurred. */
  public final int line;

  /** The column number where the error occurred. */
  public final int col;

  /** The source line where the error occurred. */
  public final String sourceLine;

  /**
   * Constructs a TypingError with the specified message, line number, column number, and source
   * line.
   *
   * @param message The error message.
   * @param line The line number where the error occurred.
   * @param col The column number where the error occurred.
   * @param sourceLine The source line where the error occurred.
   */
  public TypingError(String message, int line, int col, String sourceLine) {
    super(message);
    this.line = line;
    this.col = col;
    this.sourceLine = sourceLine;
  }

  /**
   * Returns a string representation of this typing error, including the error message, the position
   * in the source code where the error occurred, and a pointer to indicate the exact position of
   * the error.
   *
   * @return A string representation of this typing error.
   */
  @Override
  public String toString() {
    // Create a pointer to indicate the exact position of the error in the source line
    String pointer = "\t";
    pointer +=
        " "
            .repeat(
                (String.valueOf(line).length() + 1)
                    + (String.valueOf(col).length() + 3)
                    + (col - 1));
    pointer += '^';

    // Format and return the error message along with the source line and pointer
    return String.format(
        "[Typing Error] %s\n\t%d:%d | %s\n%s", getMessage(), line, col, sourceLine, pointer);
  }
}
