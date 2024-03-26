package com.github.gris.typing.type;

import com.github.gris.ast.stmt.Class;
import com.github.gris.ast.stmt.Function;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** Represents a type expression for a class. */
public class ClassTypeExpr extends TypeExpr {
  /** The name of the class. */
  public final String name;

  /** The map of method names to their type expressions. */
  public final Map<String, FunctionTypeExpr> methods = new HashMap<>();

  /** The initializer method type expression. */
  public FunctionTypeExpr initializer = null;

  /** The superclass type expression. */
  public final ClassTypeExpr superclass;

  /**
   * Constructs a ClassTypeExpr with the specified class and superclass type expressions.
   *
   * @param klass The class statement representing the class.
   * @param superclass The superclass type expression.
   */
  public ClassTypeExpr(Class klass, ClassTypeExpr superclass) {
    super(Type.CLASS);

    this.name = klass.name.lexeme;
    this.superclass = superclass;

    for (Function method : klass.methods) {
      if (Objects.equals(method.name.lexeme, "constructor")) {
        this.initializer =
            new FunctionTypeExpr(method.name.lexeme, method.returnType, method.parameters);
      }
      this.methods.put(
          method.name.lexeme,
          new FunctionTypeExpr(method.name.lexeme, method.returnType, method.parameters));
    }
  }

  /**
   * Checks if this class type expression is a superclass of another class type expression.
   *
   * @param typeExpr The class type expression to check against.
   * @return True if this class type expression is a superclass of the provided type expression,
   *     false otherwise.
   */
  public boolean isSuperOf(ClassTypeExpr typeExpr) {
    ClassTypeExpr superclass = typeExpr.superclass;
    while (superclass != null) {
      if (this == superclass) return true;
      superclass = superclass.superclass;
    }
    return false;
  }

  /**
   * Checks if this class type expression or its superclass has a method with the given name.
   *
   * @param name The name of the method to check.
   * @return True if this class type expression or its superclass has the method, false otherwise.
   */
  public boolean hasMethod(String name) {
    return this.methods.containsKey(name) || superclass.hasMethod(name);
  }

  /**
   * Retrieves the method type expression with the given name.
   *
   * @param name The name of the method.
   * @return The method type expression if found, null otherwise.
   */
  public FunctionTypeExpr getMethod(String name) {
    if (methods.get(name) != null) {
      return methods.get(name);
    }
    return superclass.getMethod(name);
  }

  /**
   * Checks if this class type expression or its superclass has a property with the given name.
   *
   * @param name The name of the property to check.
   * @return True if this class type expression or its superclass has the property, false otherwise.
   */
  public boolean hasProperty(String name) {
    return hasMethod(name);
  }

  /**
   * Retrieves the property type expression with the given name.
   *
   * @param name The name of the property.
   * @return The property type expression if found, null otherwise.
   */
  public TypeExpr getProperty(String name) {
    return getMethod(name);
  }
}
