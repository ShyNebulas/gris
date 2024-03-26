package com.github.gris.builtin.export;

import com.github.gris.ast.Parameter;
import com.github.gris.builtin.Abs;
import com.github.gris.builtin.Builtin;
import com.github.gris.lexer.Token;
import com.github.gris.lexer.TokenType;
import com.github.gris.typing.type.*;

import java.util.ArrayList;
import java.util.List;

/** Export utility for the "abs" built-in function. */
public class AbsExport {
  /**
   * Exports the "abs" built-in function.
   *
   * @return The exported "abs" built-in function.
   */
  public static Builtin export() {
    final String name = "abs";
    final List<Parameter> parameters = new ArrayList<>();
    parameters.add(
        new Parameter(
            new Token(TokenType.IDENTIFIER, "abs", null, -1, -1), new TypeExpr(Type.NUMBER)));

    return new Builtin(
        name, new Abs(), new FunctionTypeExpr("Abs", new TypeExpr(Type.NUMBER), parameters));
  }
}
