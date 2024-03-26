package com.github.gris.builtin.export;

import com.github.gris.ast.Parameter;
import com.github.gris.builtin.Builtin;
import com.github.gris.builtin.Print;
import com.github.gris.lexer.Token;
import com.github.gris.lexer.TokenType;
import com.github.gris.typing.type.*;

import java.util.ArrayList;
import java.util.List;

/** Export utility for the "print" built-in function. */
public class PrintExport {
  /**
   * Exports the "print" built-in function.
   *
   * @return The exported "print" built-in function.
   */
  public static Builtin export() {
    final String name = "print";
    final List<Parameter> parameters = new ArrayList<>();
    parameters.add(
        new Parameter(
            new Token(TokenType.IDENTIFIER, "print", null, -1, -1), new GenericAllTypeExpr()));

    return new Builtin(
        name, new Print(), new FunctionTypeExpr("Print", new TypeExpr(Type.VOID), parameters));
  }
}
