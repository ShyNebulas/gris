package com.github.gris.builtin.export;

import com.github.gris.ast.Parameter;
import com.github.gris.builtin.Builtin;
import com.github.gris.builtin.Println;
import com.github.gris.lexer.Token;
import com.github.gris.lexer.TokenType;
import com.github.gris.typing.type.*;

import java.util.ArrayList;
import java.util.List;

/** Export utility for the "println" built-in function. */
public class PrintlnExport {
  /**
   * Exports the "println" built-in function.
   *
   * @return The exported "println" built-in function.
   */
  public static Builtin export() {
    final String name = "println";
    final List<Parameter> parameters = new ArrayList<>();
    parameters.add(
        new Parameter(
            new Token(TokenType.IDENTIFIER, "println", null, -1, -1), new GenericAllTypeExpr()));

    return new Builtin(
        name, new Println(), new FunctionTypeExpr("Println", new TypeExpr(Type.VOID), parameters));
  }
}
