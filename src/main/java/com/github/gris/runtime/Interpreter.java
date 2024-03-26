package com.github.gris.runtime;

import com.github.gris.ast.expr.*;
import com.github.gris.ast.stmt.*;
import com.github.gris.ast.stmt.Class;
import com.github.gris.builtin.Builtin;
import com.github.gris.builtin.export.*;
import com.github.gris.lexer.Lexer;
import com.github.gris.typing.type.TypeExpr;
import com.github.gris.ast.visitor.ExprVisitor;
import com.github.gris.ast.visitor.StmtVisitor;
import com.github.gris.lexer.Token;
import com.github.gris.lexer.TokenType;
import com.github.gris.runtime.union.ExprUnion;
import com.github.gris.runtime.union.ExprUnionTypeExpr;
import com.github.gris.runtime.union.TypeExprUnion;
import com.github.gris.runtime.value.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Interpreter class responsible for interpreting Gris language expressions and statements. */
public class Interpreter implements ExprVisitor<GrisType>, StmtVisitor<Void> {
  /** The lexer used for error reporting. */
  private Lexer lexer;

  /** The global environment. */
  public final Environment globals;

  /** The current environment. */
  private Environment environment;

  /** A map to keep track of local variables and their scopes. */
  public final Map<ExprUnionTypeExpr, Integer> locals = new HashMap<>();

  /**
   * Constructs an Interpreter object with the given lexer.
   *
   * @param lexer The lexer used for error reporting.
   */
  public Interpreter(Lexer lexer) {
    this.lexer = lexer;
    this.globals = new Environment(lexer);
    this.environment = this.globals;

    Builtin abs = AbsExport.export();
    globals.define(abs.name, abs.function);

    Builtin charAt = CharAtExport.export();
    globals.define(charAt.name, charAt.function);

    Builtin len = LenExport.export();
    globals.define(len.name, len.function);

    Builtin print = PrintExport.export();
    globals.define(print.name, print.function);

    Builtin println = PrintlnExport.export();
    globals.define(println.name, println.function);
  }

  /**
   * Interprets a list of statements.
   *
   * @param statements The list of statements to interpret.
   */
  public void interpret(List<Stmt> statements) {
    for (Stmt statement : statements) {
      execute(statement);
    }
  }

  public GrisType visitAssignExpr(Assign expr) {
    GrisType value = this.evaluate(expr.value);


    Integer distance = null;
    for (Map.Entry<ExprUnionTypeExpr, Integer> local : locals.entrySet()) {
      if (((ExprUnion) local.getKey()).value == expr) {
        distance = local.getValue();
        break;
      }
    }


    if (distance != null) {
      this.environment.assignAt(distance, expr.name, value);
    } else {
      this.globals.assign(expr.name, value);
    }

    return value;
  }

  @Override
  public GrisType visitBinaryExpr(Binary expr) {
    final GrisType left = this.evaluate(expr.left);
    final GrisType right = this.evaluate(expr.right);

    switch (expr.operator.type) {
      case BANG_EQUAL -> {
        return new GrisBoolean(!left.isEqual(right).value);
      }
      case CARET -> {
        int leftInt = (int) ((GrisNumber) left).value;
        int rightInt = (int) ((GrisNumber) right).value;
        return new GrisNumber(Math.pow(leftInt, rightInt));
      }
      case EQUAL_EQUAL -> {
        return left.isEqual(right);
      }
      case GREATER -> {
        return new GrisBoolean(((GrisNumber) left).value > ((GrisNumber) right).value);
      }
      case GREATER_EQUAL -> {
        return new GrisBoolean(((GrisNumber) left).value >= ((GrisNumber) right).value);
      }
      case LESS -> {
        return new GrisBoolean(((GrisNumber) left).value < ((GrisNumber) right).value);
      }
      case LESS_EQUAL -> {
        return new GrisBoolean(((GrisNumber) left).value <= ((GrisNumber) right).value);
      }
      case MINUS -> {
        return new GrisNumber(((GrisNumber) left).value - ((GrisNumber) right).value);
      }
      case MODULO -> {
        return new GrisNumber(((GrisNumber) left).value % ((GrisNumber) right).value);
      }
      case PLUS -> {
        return new GrisNumber(((GrisNumber) left).value + ((GrisNumber) right).value);
      }
      case SLASH -> {
        return new GrisNumber(((GrisNumber) left).value / ((GrisNumber) right).value);
      }
      case STAR -> {
        return new GrisNumber(((GrisNumber) left).value * ((GrisNumber) right).value);
      }
    }
    // Unreachable
    return null;
  }

  @Override
  public GrisType visitCallExpr(Call expr) {
    Object callee = evaluate(expr.callee);

    List<GrisType> arguments = new ArrayList<>();
    for (Expr argument : expr.arguments) {
      arguments.add(evaluate(argument));
    }

    GrisCallable function = (GrisCallable) callee;

    return function.call(this, arguments);
  }

  @Override
  public GrisType visitGetExpr(Get expr) {
    Object object = evaluate(expr.object);
    if (object instanceof GrisClassInstance) {
      return ((GrisClassInstance) object).get(expr.name);
    }

    return null;
  }

  @Override
  public GrisType visitGroupingExpr(Grouping expr) {
    return this.evaluate(expr);
  }

  @Override
  public GrisType visitLiteralExpr(Literal expr) {
    if (expr.value == null) return null;
    switch (expr.type) {
      case BOOLEAN -> {
        return new GrisBoolean((Boolean) expr.value);
      }
      case NUMBER -> {
        return new GrisNumber((Double) expr.value);
      }
      case STRING -> {
        return new GrisString((String) expr.value);
      }
    }
    // Unreachable
    return null;
  }

  @Override
  public GrisType visitLogicalExpr(Logical expr) {
    GrisBoolean left = (GrisBoolean) this.evaluate(expr.left);

    if (expr.operator.type == TokenType.OR) {
      if (left.value) return left;
    } else {
      if (!left.value) return left;
    }

    return this.evaluate(expr.right);
  }

  @Override
  public GrisType visitSetExpr(Set expr) {
    GrisClassInstance object = (GrisClassInstance) this.evaluate(expr.object);
    GrisType value = this.evaluate(expr.value);

    object.set(expr.name, value);
    return value;
  }

  @Override
  public GrisType visitSuperExpr(Super expr) {
    final int distance = this.locals.get(expr);
    final GrisClass superclass = (GrisClass) this.environment.getAt(distance, "SUPER");
    final GrisClassInstance object = (GrisClassInstance) environment.getAt(distance - 1, "THIS");
    final GrisFunction method = superclass.findMethod(expr.method.lexeme);

    return method.bind(object);
  }

  @Override
  public GrisType visitTernaryExpr(Ternary expr) {
    final GrisBoolean condition = (GrisBoolean) evaluate(expr.condition);

    if (condition.value) {
      return evaluate(expr.thenBranch);
    } else {
      return evaluate(expr.elseBranch);
    }
  }

  @Override
  public GrisType visitThisExpr(This expr) {
    try {
      return this.lookUpVariable(expr.keyword, expr);
    } catch (Exception exception) {
      return null;
    }
  }

  @Override
  public GrisType visitUnaryExpr(Unary expr) {
    final GrisType right = evaluate(expr.right);

    switch (expr.operator.type) {
      case BANG -> {
        return new GrisBoolean(!((GrisBoolean) right).value);
      }
      case MINUS -> {
        return new GrisNumber(-((GrisNumber) right).value);
      }
    }
    // Unreachable
    return null;
  }

  @Override
  public GrisType visitVariableExpr(Variable expr) {
    return lookUpVariable(expr.name, expr);
  }

  @Override
  public Void visitBlockStmt(Block stmt) {
    executeBlock(stmt.statements, new Environment(environment));
    return null;
  }

  @Override
  public Void visitClassStmt(Class stmt) {
    GrisClass superclass = null;
    if (stmt.superclass != null) {
      superclass = (GrisClass) this.evaluate(stmt.superclass);
    }
    this.environment.define(stmt.name.lexeme, new GrisVoid());

    if (superclass != null) {
      this.environment = new Environment(environment);
      environment.define("SUPER", superclass);
    }

    Map<String, GrisFunction> methods = new HashMap<>();
    for (Function method : stmt.methods) {
      GrisFunction function =
          new GrisFunction(method, environment, method.name.lexeme.equals("constructor"));
      methods.put(method.name.lexeme, function);
    }

    GrisClass klass = new GrisClass(stmt.name.lexeme, superclass, methods);

    if (superclass != null) {
      environment = environment.enclosing;
    }

    environment.assign(stmt.name, klass);
    return null;
  }

  @Override
  public Void visitExpressionStmt(Expression stmt) {
    evaluate(stmt.expression);
    return null;
  }

  @Override
  public Void visitFunctionStmt(Function stmt) {
    GrisFunction function = new GrisFunction(stmt, environment, false);
    environment.define(stmt.name.lexeme, function);
    return null;
  }

  @Override
  public Void visitIfStmt(If stmt) {
    final GrisBoolean condition = (GrisBoolean) evaluate(stmt.condition);

    if (condition.value) {
      execute(stmt.thenBranch);
    } else if (stmt.elseBranch != null) {
      execute(stmt.elseBranch);
    }
    return null;
  }

  @Override
  public Void visitReturnStmt(Return stmt) {
    final GrisType value = stmt.value != null ? evaluate(stmt.value) : new GrisVoid();
    throw new ReturnError(value);
  }

  @Override
  public Void visitValStmt(Val stmt) {
    GrisType value = null;
    if (stmt.initializer != null) {
      value = evaluate(stmt.initializer);
    }
    environment.define(stmt.name.lexeme, value);

    return null;
  }

  @Override
  public Void visitWhileStmt(While stmt) {
    while (((GrisBoolean) evaluate(stmt.condition)).value) {
      execute(stmt.body);
    }
    return null;
  }

  /**
   * Evaluates an expression by invoking its corresponding visitor method.
   *
   * @param expr The expression to be evaluated.
   * @return The result of evaluating the expression.
   */
  private GrisType evaluate(Expr expr) {
    return expr.accept(this);
  }

  /**
   * Executes a statement by invoking its corresponding visitor method.
   *
   * @param stmt The statement to be executed.
   */
  private void execute(Stmt stmt) {
    stmt.accept(this);
  }

  /**
   * Executes a block of statements within the specified environment.
   *
   * @param statements The list of statements to be executed.
   * @param environment The environment in which the statements are executed.
   */
  public void executeBlock(List<Stmt> statements, Environment environment) {
    Environment previous = this.environment;
    try {
      this.environment = environment;
      for (Stmt statement : statements) {
        execute(statement);
      }
    } finally {
      this.environment = previous;
    }
  }

  /**
   * Looks up a variable in the current or global environment.
   *
   * @param name The name of the variable to look up.
   * @param expr The corresponding expression node.
   * @return The value of the variable, if found; otherwise, null.
   */
  private GrisType lookUpVariable(Token name, Expr expr) {
    Integer distance = null;
    for (Map.Entry<ExprUnionTypeExpr, Integer> local : locals.entrySet()) {
      if (((ExprUnion) local.getKey()).value == expr) {
        distance = local.getValue();
        break;
      }
    }


    if (distance != null) {
      return environment.getAt(distance, name.lexeme);
    } else {
      return globals.get(name);
    }
  }

  /**
   * Resolves an expression's local variable scope depth.
   *
   * @param expr The expression for which the scope depth is being resolved.
   * @param depth The depth of the local variable scope.
   */
  public void resolve(Expr expr, int depth) {
    locals.put(new ExprUnion(expr), depth);
  }

  /**
   * Resolves a type expression's local variable scope depth.
   *
   * @param typeExpr The type expression for which the scope depth is being resolved.
   * @param depth The depth of the local variable scope.
   */
  public void resolve(TypeExpr typeExpr, int depth) {
    locals.put(new TypeExprUnion(typeExpr), depth);
  }
}
