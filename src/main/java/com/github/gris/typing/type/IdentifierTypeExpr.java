package com.github.gris.typing.type;

import com.github.gris.lexer.Token;

/** Represents a type expression associated with an identifier token. */
public class IdentifierTypeExpr extends TypeExpr {

  /** The identifier token associated with this type expression. */
  public final Token identifier;

  /**
   * Constructs an IdentifierTypeExpr with the specified identifier token and type.
   *
   * @param identifier The identifier token associated with this type expression.
   * @param type The type associated with this type expression.
   */
  public IdentifierTypeExpr(Token identifier, Type type) {
    super(type);
    this.identifier = identifier;
  }
}
