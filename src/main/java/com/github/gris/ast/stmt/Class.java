package com.github.gris.ast.stmt;

import com.github.gris.ast.expr.Variable;
import com.github.gris.ast.visitor.StmtVisitor;
import com.github.gris.lexer.Token;

import java.util.List;

/** Represents a class declaration statement. */
public class Class extends Stmt {
  /** The token representing the name of the class. */
  public final Token name;

  /** The superclass variable of the class. */
  public final Variable superclass;

  /** The list of methods belonging to the class. */
  public final List<Function> methods;

  /**
   * Constructs a class declaration statement with the given name, superclass variable, and methods.
   *
   * @param name The name of the class.
   * @param superclass The superclass variable.
   * @param methods The list of methods.
   */
  public Class(Token name, Variable superclass, List<Function> methods) {
    this.name = name;
    this.superclass = superclass;
    this.methods = methods;
  }

  /**
   * Accepts a visitor and performs an operation based on this class declaration statement.
   *
   * @param visitor The visitor.
   * @param <T> The return type of the visit operation.
   * @return The result of the visit operation.
   */
  @Override
  public <T> T accept(StmtVisitor<T> visitor) {
    return visitor.visitClassStmt(this);
  }
}
