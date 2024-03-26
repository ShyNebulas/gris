package com.github.gris.typing;

import com.github.gris.ast.Parameter;
import com.github.gris.ast.expr.*;
import com.github.gris.ast.expr.Set;
import com.github.gris.ast.stmt.*;
import com.github.gris.ast.stmt.Class;
import com.github.gris.builtin.Builtin;
import com.github.gris.builtin.export.*;
import com.github.gris.lexer.Lexer;
import com.github.gris.resolver.VariableState;
import com.github.gris.typing.type.*;
import com.github.gris.ast.visitor.ExprVisitor;
import com.github.gris.ast.visitor.StmtVisitor;
import com.github.gris.lexer.Token;
import com.github.gris.runtime.Interpreter;
import com.github.gris.runtime.union.ExprUnionTypeExpr;

import java.util.*;

import static com.github.gris.typing.type.TypeExpr.matchTypeExpr;

/** The Typing class performs type checking on expressions and statements. */
public class Typing implements ExprVisitor<TypeExpr>, StmtVisitor<Void> {
  /** The lexer used for error reporting. */
  private final Lexer lexer;

  /** A map containing local variables and their types. */
  private Map<ExprUnionTypeExpr, Integer> locals = new HashMap<>();

  /** A map containing global variables and their types. */
  private final Map<String, TypeExpr> globals = new HashMap<>();

  /** A stack of scopes for managing variable scoping. */
  private final Stack<Map<String, TypeExpr>> scopes = new Stack<>();

  /** The current class being processed during type checking. */
  private ClassTypeExpr currentClass;

  /** The current function being processed during type checking. */
  private FunctionTypeExpr currentFunction;

  /**
   * Constructs a Typing object with the given Lexer and Interpreter.
   *
   * @param lexer The lexer used for tokenizing source code.
   * @param interpreter The interpreter used for handling runtime operations.
   */
  public Typing(Lexer lexer, Interpreter interpreter) {
    this.lexer = lexer;
    this.locals = interpreter.locals;

    Builtin abs = AbsExport.export();
    globals.put(abs.name, abs.type);

    Builtin charAt = CharAtExport.export();
    globals.put(charAt.name, charAt.type);

    Builtin len = LenExport.export();
    globals.put(len.name, len.type);

    Builtin print = PrintExport.export();
    globals.put(print.name, print.type);

    Builtin println = PrintlnExport.export();
    globals.put(println.name, println.type);
  }

  /**
   * Checks a list of statements for type correctness.
   *
   * @param statements The list of statements to check.
   */
  public void check(List<Stmt> statements) {
    for (Stmt statement : statements) {
      check(statement);
    }
  }

  @Override
  public TypeExpr visitAssignExpr(Assign expr) {
    final TypeExpr value = type(expr.value);
    final TypeExpr variable = lookupVariableType(expr.name);

    if (isCallable(variable)) {
      throw new TypingError(
          String.format("Cannot assign function to variable '%s'", expr.name.lexeme),
          expr.name.line,
          expr.name.col,
          lexer.getSourceLine(expr.name.line));
    }
    if (!matchTypeExpr(value, variable)) {
      throw new TypingError(
          String.format(
              "Variable's of type '%s' does not match that of assignment type '%s'",
              variable.type, value.type),
          expr.name.line,
          expr.name.col,
          lexer.getSourceLine(expr.name.line));
    }
    return value;
  }

  @Override
  public TypeExpr visitBinaryExpr(Binary expr) {
    final TypeExpr leftType = type(expr.left);
    final TypeExpr rightType = type(expr.right);

    switch (expr.operator.type) {
      case BANG_EQUAL, EQUAL_EQUAL -> {
        if (!matchTypeExpr(leftType, rightType)) {
          throw new TypingError(
              String.format(
                  "Left, type '%s', does not match that of right, type '%s'",
                  leftType.type, rightType.type),
              expr.operator.line,
              expr.operator.col,
              lexer.getSourceLine(expr.operator.line));
        }
        return new TypeExpr(Type.BOOLEAN);
      }
      case CARET, MINUS, MODULO, PLUS, SLASH, STAR -> {
        if (!isNumber(leftType)) {
          throw new TypingError(
              String.format("Left of operator '%s' is not a Number", expr.operator.lexeme),
              expr.operator.line,
              expr.operator.col,
              lexer.getSourceLine(expr.operator.line));
        }
        if (!isNumber(rightType)) {
          throw new TypingError(
              String.format("Right of operator '%s' is not a Number", expr.operator.lexeme),
              expr.operator.line,
              expr.operator.col,
              lexer.getSourceLine(expr.operator.line));
        }
        return new TypeExpr(Type.NUMBER);
      }
      case GREATER, GREATER_EQUAL, LESS, LESS_EQUAL -> {
        if (!isNumber(leftType)) {
          throw new TypingError(
              String.format("Left of operator '%s' is not a Number", expr.operator.lexeme),
              expr.operator.line,
              expr.operator.col,
              lexer.getSourceLine(expr.operator.line));
        }
        if (!isNumber(rightType)) {
          throw new TypingError(
              String.format("Right of operator '%s' is not a Number", expr.operator.lexeme),
              expr.operator.line,
              expr.operator.col,
              lexer.getSourceLine(expr.operator.line));
        }
        return new TypeExpr(Type.BOOLEAN);
      }
    }
    // Unreachable
    return null;
  }

  @Override
  public TypeExpr visitCallExpr(Call expr) {
    final TypeExpr type = type(expr.callee);

    if (!isCallable(type)) {
      throw new TypingError(
          "Expression is not callable",
          expr.parenthesis.line,
          expr.parenthesis.col,
          lexer.getSourceLine(expr.parenthesis.line));
    }

    List<TypeExpr> typedArguments = new ArrayList<>();
    for (Expr argument : expr.arguments) {
      typedArguments.add(type(argument));
    }

    if (type instanceof FunctionTypeExpr) {
      if (!((FunctionTypeExpr) type).matchParams(typedArguments)) {
        throw new TypingError(
            "Arguments do not match that of callee's parameters",
            expr.parenthesis.line,
            expr.parenthesis.col,
            lexer.getSourceLine(expr.parenthesis.line));
      }
      return ((FunctionTypeExpr) type).returnType;
    } else if (type instanceof ClassTypeExpr klass) {
      if (klass.initializer != null) {
        if (klass.initializer.matchParams(typedArguments)) {
          throw new TypingError(
              "Arguments do not match that of the class' constructor",
              expr.parenthesis.line,
              expr.parenthesis.col,
              lexer.getSourceLine(expr.parenthesis.line));
        }
      }
      return new ClassInstanceTypeExpr((ClassTypeExpr) type);
    }
    // Unreachable
    return null;
  }

  @Override
  public TypeExpr visitGetExpr(Get expr) {
    final TypeExpr type = type(expr.object);

    if (!isInstance(type))
      throw new TypingError(
          "Object is not an instance of a class",
          expr.name.line,
          expr.name.col,
          lexer.getSourceLine(expr.name.line));

    if (type instanceof ClassInstanceTypeExpr) {
      final ClassTypeExpr klass = ((ClassInstanceTypeExpr) type).klass;
      String prop = expr.name.lexeme;

      if (!klass.hasProperty(prop))
        throw new TypingError(
            "Class does not have property",
            expr.name.line,
            expr.name.col,
            lexer.getSourceLine(expr.name.line));

      if (currentClass != null) {
        if (klass.isSuperOf(this.currentClass) || klass == currentClass) {
          return klass.getProperty(prop);
        }
      }

      return klass.getMethod(prop);
    }
    return null;
  }

  @Override
  public TypeExpr visitGroupingExpr(Grouping expr) {
    return type(expr.expression);
  }

  @Override
  public TypeExpr visitLiteralExpr(Literal expr) {
    return new TypeExpr(expr.type);
  }

  @Override
  public TypeExpr visitLogicalExpr(Logical expr) {
    final TypeExpr leftExpr = type(expr.left);
    final TypeExpr rightExpr = type(expr.right);

    if (!isBoolean(leftExpr))
      throw new TypingError(
          "Left expression is not a Boolean.",
          expr.operator.line,
          expr.operator.col,
          lexer.getSourceLine(expr.operator.line));

    if (!isBoolean(rightExpr))
      throw new TypingError(
          "Right expression is not a Boolean.",
          expr.operator.line,
          expr.operator.col,
          lexer.getSourceLine(expr.operator.line));

    return new TypeExpr(Type.BOOLEAN);
  }

  @Override
  public TypeExpr visitSetExpr(Set expr) {
    return null;
  }

  @Override
  public TypeExpr visitSuperExpr(Super expr) {
    TypeExpr superclass = lookupVariableType(expr.keyword);
    if (!(superclass instanceof ClassTypeExpr)) {
      throw new TypingError(
          "Super class is not a class",
          expr.keyword.line,
          expr.keyword.col,
          lexer.getSourceLine(expr.keyword.line));
    }

    if (!((ClassTypeExpr) superclass).hasMethod(expr.method.lexeme)) {
      throw new TypingError(
          "Super class does not have method",
          expr.keyword.line,
          expr.keyword.col,
          lexer.getSourceLine(expr.keyword.line));
    }

    return ((ClassTypeExpr) superclass).getMethod(expr.method.lexeme);
  }

  @Override
  public TypeExpr visitTernaryExpr(Ternary expr) {
    final TypeExpr condition = type(expr.condition);
    if (!isBoolean(condition))
      throw new TypingError("Ternary condition is not a Boolean.", 0, 0, "Unavailable");

    final TypeExpr thenType = type(expr.thenBranch);
    final TypeExpr elseType = type(expr.elseBranch);

    if (!matchTypeExpr(thenType, elseType))
      throw new TypingError("Unary '!' requires a Boolean", 0, 0, "Unavailable");

    return new TypeExpr(thenType.type);
  }

  @Override
  public TypeExpr visitThisExpr(This expr) {
    return null;
  }

  @Override
  public TypeExpr visitUnaryExpr(Unary expr) {
    final TypeExpr type = type(expr.right);

    switch (expr.operator.type) {
      case BANG -> {
        if (!isBoolean(type)) {
          throw new TypingError(
              "Unary '!' requires a Boolean",
              expr.operator.line,
              expr.operator.col,
              lexer.getSourceLine(expr.operator.line));
        }
      }
      case MINUS -> {
        if (!isNumber(type)) {
          throw new TypingError(
              "Unary '-' requires a Number",
              expr.operator.line,
              expr.operator.col,
              lexer.getSourceLine(expr.operator.line));
        }
      }
    }

    return type;
  }

  @Override
  public TypeExpr visitVariableExpr(Variable expr) {
    return lookupVariableType(expr.name);
  }

  @Override
  public Void visitBlockStmt(Block stmt) {
    beginScope();
    check(stmt.statements);
    endScope();
    return null;
  }

  @Override
  public Void visitClassStmt(Class stmt) {
    ClassTypeExpr superclass = null;
    if (stmt.superclass != null) {
      superclass = (ClassTypeExpr) lookupVariableType(stmt.superclass.name);
    }

    final ClassTypeExpr klass = new ClassTypeExpr(stmt, superclass);
    declare(stmt.name, klass);
    currentClass = klass;

    if (superclass != null) {
      beginScope();
      scopes.peek().put("SUPER", superclass);
    }

    beginScope();

    for (Function method : stmt.methods) {
      final FunctionTypeExpr type =
          new FunctionTypeExpr(method.name.lexeme, method.returnType, method.parameters);
      checkFunction(method, type);
    }

    endScope();

    currentFunction = null;

    return null;
  }

  @Override
  public Void visitExpressionStmt(Expression stmt) {
    type(stmt.expression);
    return null;
  }

  @Override
  public Void visitFunctionStmt(Function stmt) {
    final FunctionTypeExpr type =
        new FunctionTypeExpr(stmt.name.lexeme, stmt.returnType, stmt.parameters);
    declare(stmt.name, type);
    checkFunction(stmt, type);
    return null;
  }

  @Override
  public Void visitIfStmt(If stmt) {
    final TypeExpr condition = type(stmt.condition);
    if (!isBoolean(condition)) {
      throw new TypingError("If statement condition not a boolean", 0, 0, "Unavailable");
    }

    check(stmt.thenBranch);
    if (stmt.elseBranch != null) check(stmt.elseBranch);

    return null;
  }

  @Override
  public Void visitReturnStmt(Return stmt) {
    TypeExpr returnType = new TypeExpr(Type.VOID);
    if (stmt.value != null) returnType = this.type(stmt.value);

    if (!matchTypeExpr(returnType, currentFunction.returnType)) {
      throw new TypingError(
          String.format(
              "Return value's type, '%s', does not match that of the function's type, '%s'",
              currentFunction.returnType, returnType),
          stmt.keyword.line,
          stmt.keyword.col,
          lexer.getSourceLine(stmt.keyword.line));
    }
    return null;
  }

  @Override
  public Void visitValStmt(Val stmt) {
    final TypeExpr initializer = type(stmt.initializer);
    final TypeExpr type = variableType(stmt, initializer);

    declare(stmt.name, type);
    return null;
  }

  @Override
  public Void visitWhileStmt(While stmt) {
    final TypeExpr condition = type(stmt.condition);
    if (!isBoolean(condition)) {
      throw new TypingError(
          String.format("While statement condition, type '%s', not a boolean", condition),
          0,
          0,
          "Unavailable");
    }
    check(stmt.body);
    return null;
  }

  /**
   * Determines the type of a variable declared with the 'val' keyword. If the type is not
   * explicitly specified, it uses the type of the initializer.
   *
   * @param stmt The 'val' statement representing the variable declaration.
   * @param initializer The type of the initializer expression.
   * @return The resolved type of the variable.
   * @throws TypingError If the declared type and the initializer type do not match, or if the
   *     declared type is not a valid class type.
   */
  private TypeExpr variableType(Val stmt, TypeExpr initializer) {
    TypeExpr type = stmt.type;
    if (stmt.type == null) type = initializer;

    if (type.type == Type.IDENTIFIER) {
      TypeExpr object = lookupVariableType(((IdentifierTypeExpr) type).identifier);
      if (!isObject(object)) {
        throw new TypingError(
            String.format(
                "Type '%s' is not a class", ((IdentifierTypeExpr) type).identifier.lexeme),
            ((IdentifierTypeExpr) type).identifier.line,
            ((IdentifierTypeExpr) type).identifier.col,
            lexer.getSourceLine(((IdentifierTypeExpr) type).identifier.line));
      }

      if (object instanceof ClassTypeExpr) {
        type = new ClassInstanceTypeExpr((ClassTypeExpr) object);
      }
    }

    if (!matchTypeExpr(type, initializer)) {
      throw new TypingError(
          String.format(
              "Variable type '%s' and initializer type '%s' do not match",
              type.type.toString(), initializer.type.toString()),
          stmt.name.line,
          stmt.name.col,
          lexer.getSourceLine(stmt.name.line));
    }

    return type;
  }

  /**
   * Determines the type of a variable by looking up its name.
   *
   * @param name The name of the variable.
   * @return The type of the variable.
   * @throws TypingError If the variable is not defined.
   */
  private TypeExpr lookupVariableType(Token name) {
    for (int i = scopes.size() - 1; i >= 0; i--) {
      final Map<String, TypeExpr> scope = scopes.get(i);
      if (scope.containsKey(name.lexeme)) {
        return scope.get(name.lexeme);
      }
    }

    if (globals.containsKey(name.lexeme)) {
      return globals.get(name.lexeme);
    }

    throw new TypingError(
        String.format("Variable '%s' is not defined", name.lexeme),
        name.line,
        name.col,
        lexer.getSourceLine(name.line));
  }

  /** Begins a new scope by creating a new map for variable bindings. */
  private void beginScope() {
    scopes.push(new HashMap<String, TypeExpr>());
  }

  /** Ends the current scope by removing the topmost map of variable bindings. */
  private void endScope() {
    scopes.pop();
  }

  /**
   * Declares a variable with its corresponding type in the current scope.
   *
   * @param name The name of the variable.
   * @param typeExpr The type of the variable.
   */
  private void declare(Token name, TypeExpr typeExpr) {
    if (!scopes.isEmpty()) {
      Map<String, TypeExpr> scope = scopes.get(this.scopes.size() - 1);
      scope.put(name.lexeme, typeExpr);
    } else {
      globals.put(name.lexeme, typeExpr);
    }
  }

  /**
   * Determines the type of an expression by visiting it.
   *
   * @param expr The expression to be typed.
   * @return The type of the expression.
   */
  private TypeExpr type(Expr expr) {
    return expr.accept(this);
  }

  /**
   * Checks the type of a statement by visiting it.
   *
   * @param stmt The statement to be type-checked.
   */
  private void check(Stmt stmt) {
    stmt.accept(this);
  }

  /**
   * Checks the parameters and body of a function for correct typing.
   *
   * @param stmt The function statement.
   * @param typeExpr The type expression representing the function's type.
   */
  private void checkFunction(Function stmt, FunctionTypeExpr typeExpr) {
    currentFunction = typeExpr;
    beginScope();
    for (Parameter param : stmt.parameters) {
      declare(param.name, param.type);
    }
    check(stmt.body);
    endScope();
    currentFunction = null;
  }

  /**
   * Checks if a given type expression represents a Boolean type.
   *
   * @param typeExpr The type expression to check.
   * @return True if the type expression represents a Boolean type, otherwise false.
   */
  private boolean isBoolean(TypeExpr typeExpr) {
    return typeExpr.type == Type.BOOLEAN;
  }

  /**
   * Checks if a given type expression represents a callable type (class or function).
   *
   * @param typeExpr The type expression to check.
   * @return True if the type expression represents a callable type, otherwise false.
   */
  private boolean isCallable(TypeExpr typeExpr) {
    return typeExpr.type == Type.CLASS || typeExpr.type == Type.FUNCTION;
  }

  /**
   * Checks if a given type expression represents a class instance.
   *
   * @param typeExpr The type expression to check.
   * @return True if the type expression represents a class instance, otherwise false.
   */
  private boolean isInstance(TypeExpr typeExpr) {
    return typeExpr.type == Type.CLASS_INSTANCE;
  }

  /**
   * Checks if a given type expression represents a numeric type.
   *
   * @param typeExpr The type expression to check.
   * @return True if the type expression represents a numeric type, otherwise false.
   */
  private boolean isNumber(TypeExpr typeExpr) {
    return typeExpr.type == Type.NUMBER;
  }

  /**
   * Checks if a given type expression represents a class type.
   *
   * @param typeExpr The type expression to check.
   * @return True if the type expression represents a class type, otherwise false.
   */
  private boolean isObject(TypeExpr typeExpr) {
    return typeExpr.type == Type.CLASS;
  }
}
