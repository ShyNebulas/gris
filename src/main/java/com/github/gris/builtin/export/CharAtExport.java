package com.github.gris.builtin.export;

import com.github.gris.ast.Parameter;
import com.github.gris.builtin.Builtin;
import com.github.gris.builtin.CharAt;
import com.github.gris.lexer.Token;
import com.github.gris.lexer.TokenType;
import com.github.gris.typing.type.*;

import java.util.ArrayList;
import java.util.List;

/** Export utility for the "charAt" built-in function. */
public class CharAtExport {
    /**
     * Exports the "charAt" built-in function.
     *
     * @return The exported "charAt" built-in function.
     */
    public static Builtin export() {
        final String name = "charAt";
        final List<Parameter> parameters = new ArrayList<>();
        parameters.add(
                new Parameter(new Token(TokenType.IDENTIFIER, "string", null, -1, -1), new TypeExpr(Type.STRING))
        );
        parameters.add(
                new Parameter(new Token(TokenType.IDENTIFIER, "index", null, -1, -1), new TypeExpr(Type.NUMBER))
        );

        return new Builtin(
                name, new CharAt(), new FunctionTypeExpr("CharAt", new TypeExpr(Type.STRING), parameters));
    }
}
