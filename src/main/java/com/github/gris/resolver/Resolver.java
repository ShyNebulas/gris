package com.github.gris.resolver;

import com.github.gris.ast.Parameter;
import com.github.gris.ast.expr.*;
import com.github.gris.ast.expr.Set;
import com.github.gris.ast.stmt.*;
import com.github.gris.ast.stmt.Class;
import com.github.gris.builtin.Builtin;
import com.github.gris.builtin.export.*;
import com.github.gris.typing.type.IdentifierTypeExpr;
import com.github.gris.typing.type.Type;
import com.github.gris.typing.type.TypeExpr;
import com.github.gris.ast.visitor.ExprVisitor;
import com.github.gris.ast.visitor.StmtVisitor;
import com.github.gris.lexer.Lexer;
import com.github.gris.lexer.Token;
import com.github.gris.runtime.Interpreter;

import java.util.*;

/** Resolves variable scopes and types in the AST. */
public class Resolver implements ExprVisitor<Void>, StmtVisitor<Void> {
  /** The lexer used for error reporting. */
  private Lexer lexer;

  /** The interpreter for resolving expressions. */
  private Interpreter interpreter;

  /** Global variables and their states. */
  public Map<String, VariableState> globals = new HashMap<>();

  /** List of scopes, each containing variable names and their states. */
  public LinkedList<Map<String, VariableState>> scopes = new LinkedList<>();

  /** The current class type being resolved. */
  private ClassType currentClass = ClassType.NONE;

  /** The current function type being resolved. */
  private FunctionType currentFunction = FunctionType.NONE;

  /**
   * Constructs a Resolver with the provided Lexer and Interpreter.
   *
   * @param lexer The lexer to use for source code analysis.
   * @param interpreter The interpreter for resolving expressions.
   */
  public Resolver(Lexer lexer, Interpreter interpreter) {
    this.lexer = lexer;
    this.interpreter = interpreter;

    Builtin abs = AbsExport.export();
    globals.put(abs.name, VariableState.USED);

    Builtin charAt = CharAtExport.export();
    globals.put(charAt.name, VariableState.USED);

    Builtin len = LenExport.export();
    globals.put(len.name, VariableState.USED);

    Builtin print = PrintExport.export();
    globals.put(print.name, VariableState.USED);

    Builtin println = PrintlnExport.export();
    globals.put(println.name, VariableState.USED);
  }

  /**
   * Resolves the given list of statements.
   *
   * @param statements The list of statements to resolve.
   */
  public void resolve(List<Stmt> statements) {
    for (Stmt statement : statements) {
      resolve(statement);
    }
  }

  @Override
  public Void visitAssignExpr(Assign expr) {
    resolve(expr.value);
    resolveLocal(expr, expr.name);
    return null;
  }

  @Override
  public Void visitBinaryExpr(Binary expr) {
    resolve(expr.left);
    resolve(expr.right);
    return null;
  }

  @Override
  public Void visitCallExpr(Call expr) {
    resolve(expr.callee);
    for (Expr argument : expr.arguments) {
      resolve(argument);
    }
    return null;
  }

  @Override
  public Void visitGetExpr(Get expr) {
    resolve(expr.object);
    return null;
  }

  @Override
  public Void visitGroupingExpr(Grouping expr) {
    resolve(expr.expression);
    return null;
  }

  @Override
  public Void visitLiteralExpr(Literal expr) {
    return null;
  }

  @Override
  public Void visitLogicalExpr(Logical expr) {
    resolve(expr.left);
    resolve(expr.right);
    return null;
  }

  @Override
  public Void visitSetExpr(Set expr) {
    resolve(expr.value);
    resolve(expr.object);
    return null;
  }

  @Override
  public Void visitSuperExpr(Super expr) {
    if (currentClass == ClassType.NONE) {
      throw new ResolvingError(
          "'super' outside of class",
          expr.keyword.line,
          expr.keyword.col,
          lexer.getSourceLine(expr.keyword.line));
    } else if (currentClass != ClassType.SUBCLASS) {
      throw new ResolvingError(
          "'super' in a class with no superclass",
          expr.keyword.line,
          expr.keyword.col,
          lexer.getSourceLine(expr.keyword.line));
    }
    resolveLocal(expr, expr.keyword);
    return null;
  }

  @Override
  public Void visitTernaryExpr(Ternary expr) {
    resolve(expr.condition);
    resolve(expr.thenBranch);
    resolve(expr.elseBranch);
    return null;
  }

  @Override
  public Void visitThisExpr(This expr) {
    if (currentClass == ClassType.NONE) {
      throw new ResolvingError(
          "'this' outside of a class",
          expr.keyword.line,
          expr.keyword.col,
          lexer.getSourceLine(expr.keyword.line));
    }
    resolveLocal(expr, expr.keyword);
    return null;
  }

  @Override
  public Void visitUnaryExpr(Unary expr) {
    resolve(expr.right);
    return null;
  }

  @Override
  public Void visitVariableExpr(Variable expr) {
    if (!scopes.isEmpty() && scopes.getLast().get(expr.name.lexeme) == VariableState.DECLARED) {
      throw new ResolvingError(
          "Can't read local variable in its own initializer",
          expr.name.line,
          expr.name.col,
          lexer.getSourceLine(expr.name.line));
    }
    resolveLocal(expr, expr.name);
    return null;
  }

  @Override
  public Void visitBlockStmt(Block stmt) {
    beginScope();
    resolve(stmt.statements);
    endScope();
    return null;
  }

  @Override
  public Void visitClassStmt(Class stmt) {
    if (scopes.size() > 0) {
      throw new ResolvingError(
          "Class declared in local scope",
          stmt.name.line,
          stmt.name.col,
          lexer.getSourceLine(stmt.name.line));
    }

    declare(stmt.name);
    define(stmt.name);

    ClassType enclosingClass = currentClass;
    currentClass = ClassType.CLASS;

    if (stmt.superclass != null) {
      if (stmt.name.lexeme.equals(stmt.superclass.name.lexeme)) {
        throw new ResolvingError(
            "Class can't inherit from itself",
            stmt.name.line,
            stmt.name.col,
            lexer.getSourceLine(stmt.name.line));
      }

      currentClass = ClassType.SUBCLASS;

      resolveLocal(stmt.superclass, stmt.superclass.name);

      beginScope();
      scopes.getLast().put("SUPER", VariableState.USED);
    }

    beginScope();
    scopes.getLast().put("THIS", VariableState.USED);

    for (Function method : stmt.methods) {
      FunctionType declaration = FunctionType.METHOD;
      if (method.name.lexeme.equals("constructor")) {
        declaration = FunctionType.INITIALIZER;
      }
      resolveFunction(method, declaration);
    }

    endScope();

    if (stmt.superclass != null) endScope();

    currentClass = enclosingClass;
    return null;
  }

  @Override
  public Void visitExpressionStmt(Expression stmt) {
    resolve(stmt.expression);
    return null;
  }

  @Override
  public Void visitFunctionStmt(Function stmt) {
    declare(stmt.name);
    define(stmt.name);

    resolveFunction(stmt, FunctionType.FUNCTION);
    return null;
  }

  @Override
  public Void visitIfStmt(If stmt) {
    resolve(stmt.condition);
    resolve(stmt.thenBranch);
    if (stmt.elseBranch != null) resolve(stmt.elseBranch);
    return null;
  }

  @Override
  public Void visitReturnStmt(Return stmt) {
    if (currentFunction == FunctionType.NONE) {
      throw new ResolvingError(
          "Can't return from top-level code",
          stmt.keyword.line,
          stmt.keyword.col,
          lexer.getSourceLine(stmt.keyword.line));
    }
    if (stmt.value != null) {
      if (currentFunction == FunctionType.INITIALIZER) {
        throw new ResolvingError(
            "Can't return a value from an initializer",
            stmt.keyword.line,
            stmt.keyword.col,
            lexer.getSourceLine(stmt.keyword.line));
      }
      resolve(stmt.value);
    }
    return null;
  }

  @Override
  public Void visitValStmt(Val stmt) {
    declare(stmt.name);
    if (stmt.initializer != null) {
      resolve(stmt.initializer);
    }

    if (stmt.type.type == Type.IDENTIFIER) {
      resolveLocal(stmt.type, ((IdentifierTypeExpr) stmt.type).identifier);
    }

    define(stmt.name);

    return null;
  }

  @Override
  public Void visitWhileStmt(While stmt) {
    resolve(stmt.condition);
    resolve(stmt.body);
    return null;
  }

  /**
   * Resolves the given function with its parameters and body.
   *
   * @param function The function to resolve.
   * @param type The type of the function (e.g., function, initializer).
   */
  private void resolveFunction(Function function, FunctionType type) {
    FunctionType enclosingFunction = currentFunction;
    currentFunction = type;

    beginScope();
    for (Parameter parameter : function.parameters) {
      declare(parameter.name);
      define(parameter.name);
    }
    resolve(function.body);
    endScope();
    currentFunction = enclosingFunction;
  }

  /**
   * Declares a variable in the current scope.
   *
   * @param name The name of the variable.
   * @throws ResolvingError if a variable with the same name already exists in the scope.
   */
  private void declare(Token name) {
    Map<String, VariableState> scope = scopes.isEmpty() ? globals : scopes.getLast();

    if (scope.containsKey(name.lexeme)) {
      throw new ResolvingError(
          "Already a variable with this name in this scope",
          name.line,
          name.col,
          lexer.getSourceLine(name.line));
    }

    scope.put(name.lexeme, VariableState.DECLARED);
  }

  /**
   * Defines a variable in the current scope.
   *
   * @param name The name of the variable to define.
   */
  private void define(Token name) {
    Map<String, VariableState> scope = scopes.isEmpty() ? globals : scopes.getLast();
    scope.put(name.lexeme, VariableState.DEFINED);
  }

  /**
   * Resolves a local expression within the scopes.
   *
   * @param expr The expression to resolve.
   * @param name The name of the expression.
   * @throws ResolvingError if the variable is undefined.
   */
  private void resolveLocal(Expr expr, Token name) {
    for (int i = scopes.size() - 1; i >= 0; i--) {
      if (scopes.get(i).containsKey(name.lexeme)) {
        scopes.get(i).put(name.lexeme, VariableState.USED);
        interpreter.resolve(expr, scopes.size() - 1 - i);
        return;
      }
    }

    if (globals.containsKey(name.lexeme)) {
      globals.put(name.lexeme, VariableState.USED);
      return;
    }

    throw new ResolvingError(
        "Undefined Variable", name.line, name.col, lexer.getSourceLine(name.line));
  }

  /**
   * Resolves a local type expression within the scopes.
   *
   * @param typeExpr The type expression to resolve.
   * @param name The name of the type expression.
   * @throws ResolvingError if the variable is undefined.
   */
  private void resolveLocal(TypeExpr typeExpr, Token name) {
    for (int i = scopes.size() - 1; i >= 0; i--) {
      if (scopes.get(i).containsKey(name.lexeme)) {
        scopes.get(i).put(name.lexeme, VariableState.USED);
        interpreter.resolve(typeExpr, scopes.size() - 1 - i);
        return;
      }
    }

    if (globals.containsKey(name.lexeme)) {
      globals.put(name.lexeme, VariableState.USED);
      return;
    }

    throw new ResolvingError(
        "Undefined Variable", name.line, name.col, lexer.getSourceLine(name.line));
  }

  /** Begins a new scope. */
  private void beginScope() {
    scopes.add(new HashMap<>());
  }

  /** Ends the current scope. */
  private void endScope() {
    scopes.removeLast();
  }

  /**
   * Resolves a statement.
   *
   * @param statement The statement to resolve.
   */
  private void resolve(Stmt statement) {
    statement.accept(this);
  }

  /**
   * Resolves an expression.
   *
   * @param expression The expression to resolve.
   */
  private void resolve(Expr expression) {
    expression.accept(this);
  }
}
