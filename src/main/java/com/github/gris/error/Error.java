package com.github.gris.error;

// TODO: Add support for showing the line which causes an error.
public final class Error {
  public static boolean hadError = false;

  public static void report(int line, int col, String message, String contents) {
    System.err.printf("Syntax Error: %s\n  %d:%d | %s%n", message, line, col, contents);



    // TODO: string format
    hadError = true;
  }

//  static void error(Token token, String message) {
//    if (token.type == Type.EOF) {
//      report(token.line, "at end", message);
//    } else {
//      report(token.line, "at'" + token.lexeme + "'", message);
//    }
//  }

  //    static void runtimeError(RuntimeError error) {
  //        System.err.println(error.getMessage() + "\n[line " + error.token.line + "]");
  //        hadRuntimeError = true;
  //    }

}
