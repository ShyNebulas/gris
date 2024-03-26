package com.github.gris.runtime;

import com.github.gris.lexer.Lexer;
import com.github.gris.lexer.Token;
import com.github.gris.runtime.value.GrisType;

import java.util.HashMap;
import java.util.Map;

/** Represents an environment in the Gris language runtime. */
public class Environment {
  /** The lexer used for error reporting. */
  private Lexer lexer;

  /** The enclosing environment, if any. */
  final Environment enclosing;

  /** The map of variable values within the environment. */
  public final Map<String, GrisType> values = new HashMap<>();

  /**
   * Constructs a new environment with the given lexer.
   *
   * @param lexer The lexer used for error reporting.
   */
  public Environment(Lexer lexer) {
    this.lexer = lexer;
    this.enclosing = null;
  }

  /**
   * Constructs a new environment with the given enclosing environment.
   *
   * @param enclosing The enclosing environment.
   */
  public Environment(Environment enclosing) {
    this.enclosing = enclosing;
  }

  /**
   * Retrieves the value of a variable by its name.
   *
   * @param name The token representing the name of the variable.
   * @return The value of the variable.
   * @throws RuntimeError If the variable is not defined.
   */
  GrisType get(Token name) {

    if(values.containsKey(name.lexeme)) return values.get(name.lexeme);

    if (enclosing != null) {
      return enclosing.get(name);
    }

    throw new RuntimeError(
        String.format("Undefined variable name '%s'", name.lexeme),
        name.line,
        name.col,
        lexer.getSourceLine(name.line));
  }

  /**
   * Retrieves the value of a variable from an ancestor environment.
   *
   * @param distance The distance to the ancestor environment.
   * @param name The name of the variable.
   * @return The value of the variable.
   */
  public GrisType getAt(int distance, String name) {
    return ancestor(distance).values.get(name);
  }

  /**
   * Assigns a value to a variable in an ancestor environment.
   *
   * @param distance The distance to the ancestor environment.
   * @param name The name of the variable.
   * @param value The value to be assigned.
   */
  void assignAt(int distance, Token name, GrisType value) {
    ancestor(distance).values.put(name.lexeme, value);
  }

  /**
   * Assigns a value to a variable in the current or enclosing environment.
   *
   * @param name The name of the variable.
   * @param value The value to be assigned.
   * @throws RuntimeError If the variable is not defined.
   */
  void assign(Token name, GrisType value) {
    if (values.containsKey(name.lexeme)) {
      values.put(name.lexeme, value);
      return;
    }

    if (enclosing != null) {
      enclosing.assign(name, value);
      return;
    }

    throw new RuntimeError(
        String.format("Undefined variable name '%s'", name.lexeme),
        name.line,
        name.col,
        lexer.getSourceLine(name.line));
  }

  /**
   * Defines a variable in the current environment.
   *
   * @param name The name of the variable.
   * @param value The value of the variable.
   */
  public void define(String name, GrisType value) {
    values.put(name, value);
  }

  /**
   * Retrieves the ancestor environment at the specified distance.
   *
   * @param distance The distance to the ancestor environment.
   * @return The ancestor environment.
   */
  Environment ancestor(int distance) {
    Environment environment = this;
    for (int i = 0; i < distance; i++) {
      environment = environment.enclosing;
    }
    return environment;
  }
}
