package com.github.gris.builtin;

import com.github.gris.runtime.Interpreter;
import com.github.gris.runtime.value.*;
import com.github.gris.typing.type.Type;

import java.util.List;

/** Built-in function implementation for the "charAt" function. */
public class CharAt extends GrisCallable {
    /** Constructs a Len built-in function. */
    public CharAt() {
        super(Type.FUNCTION);
    }

    /**
     * Executes the "charAt" function, printing its argument to the console.
     *
     * @param interpreter The interpreter instance.
     * @param arguments The arguments passed to the function.
     * @return GrisNumber indicating the function's return value.
     */
    @Override
    public GrisType call(Interpreter interpreter, List<GrisType> arguments) {
        String string = ((GrisString) arguments.get(0)).value;
        int index = (int) ((GrisNumber) arguments.get(1)).value;
        return new GrisString(String.valueOf(string.charAt(index)));
    }

    /**
     * Checks if this built-in function is equal to another GrisType.
     *
     * @param value The value to compare equality with.
     * @return GrisBoolean indicating whether the two values are equal.
     */
    @Override
    public GrisBoolean isEqual(GrisType value) {
        if (!(value instanceof GrisCallable)) return new GrisBoolean(false);
        return new GrisBoolean(this == value);
    }

    /**
     * Returns a string representation of the built-in function.
     *
     * @return GrisString representing the string representation of the function.
     */
    @Override
    public GrisString toGrisString() {
        return new GrisString("<built-in charAt>");
    }
}
