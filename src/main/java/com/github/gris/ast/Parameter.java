package com.github.gris.ast;

import com.github.gris.typing.type.TypeExpr;
import com.github.gris.lexer.Token;

/** Represents a parameter in a function declaration. */
public class Parameter {
  /** The token representing the name of the parameter. */
  public final Token name;

  /** The type expression of the parameter. */
  public final TypeExpr type;

  /**
   * Constructs a parameter with the given name and type expression.
   *
   * @param name The name of the parameter.
   * @param type The type expression of the parameter.
   */
  public Parameter(Token name, TypeExpr type) {
    this.name = name;
    this.type = type;
  }
}
