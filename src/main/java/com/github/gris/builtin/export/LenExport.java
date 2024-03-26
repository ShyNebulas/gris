package com.github.gris.builtin.export;

import com.github.gris.ast.Parameter;
import com.github.gris.builtin.Builtin;
import com.github.gris.builtin.Len;
import com.github.gris.lexer.Token;
import com.github.gris.lexer.TokenType;
import com.github.gris.typing.type.*;

import java.util.ArrayList;
import java.util.List;

/** Export utility for the "len" built-in function. */
public class LenExport {
    /**
     * Exports the "len" built-in function.
     *
     * @return The exported "len" built-in function.
     */
    public static Builtin export() {
        final String name = "len";
        final List<Parameter> parameters = new ArrayList<>();
        parameters.add(
                new Parameter(
                        new Token(TokenType.IDENTIFIER, "len", null, -1, -1), new TypeExpr(Type.STRING)));

        return new Builtin(
                name, new Len(), new FunctionTypeExpr("Len", new TypeExpr(Type.NUMBER), parameters));
    }
}
