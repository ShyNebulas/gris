package com.github.gris.typing.type;

import com.github.gris.ast.Parameter;

import java.util.ArrayList;
import java.util.List;

/** Represents a type expression for a function. */
public class FunctionTypeExpr extends TypeExpr {
  /** The name of the function. */
  public final String name;

  /** The return type expression of the function. */
  public final TypeExpr returnType;

  /** The list of parameter type expressions of the function. */
  public final List<TypeExpr> parameters;

  /**
   * Constructs a FunctionTypeExpr with the specified name, return type, and parameter types.
   *
   * @param name The name of the function.
   * @param returnType The return type expression of the function.
   * @param parameters The list of parameter type expressions of the function.
   */
  public FunctionTypeExpr(String name, TypeExpr returnType, List<Parameter> parameters) {
    super(Type.FUNCTION);
    this.name = name;
    this.returnType = returnType == null ? new TypeExpr(Type.VOID) : returnType;

    List<TypeExpr> typedParameters = new ArrayList<>();
    for (Parameter parameter : parameters) {
      typedParameters.add(parameter.type);
    }
    this.parameters = typedParameters;
  }

  /**
   * Matches the signature of this function type expression with another function type expression.
   *
   * @param func The function type expression to match against.
   * @return True if the signatures match, false otherwise.
   */
  public boolean matchSignature(FunctionTypeExpr func) {
    if (matchTypeExpr(this.returnType, func.returnType)) {
      return matchParams(func.parameters);
    }
    return false;
  }

  /**
   * Matches the parameters of this function type expression with a list of parameter type
   * expressions.
   *
   * @param parameters The list of parameter type expressions to match against.
   * @return True if the parameters match, false otherwise.
   */
  public boolean matchParams(List<TypeExpr> parameters) {
    if (this.parameters.size() != parameters.size()) return false;

    for (int i = 0; i < this.parameters.size(); i++) {
      if (!matchTypeExpr(this.parameters.get(i), parameters.get(i))) return false;
    }

    return true;
  }
}
