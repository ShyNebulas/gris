package com.github.gris.parser;

import com.github.gris.ast.Parameter;
import com.github.gris.ast.expr.*;
import com.github.gris.ast.stmt.*;
import com.github.gris.ast.stmt.Class;
import com.github.gris.typing.type.IdentifierTypeExpr;
import com.github.gris.typing.type.Type;
import com.github.gris.typing.type.TypeExpr;
import com.github.gris.lexer.Lexer;
import com.github.gris.lexer.Token;
import com.github.gris.lexer.TokenType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** This class represents a parser for the source code tokens. */
public class Parser {

  private final Lexer lexer;

  /** The list of tokens to be parsed. */
  private final List<Token> tokens;

  /** Current index in the list of tokens. */
  private int current = 0;

  /**
   * Constructs a Parser object with the given list of tokens.
   *
   * @param tokens The list of tokens to be parsed.
   */
  public Parser(Lexer lexer, List<Token> tokens) {
    this.lexer = lexer;
    this.tokens = tokens;
  }

  /**
   * Parses the list of tokens and returns a list of statements.
   *
   * @return A list of statements.
   */
  public List<Stmt> parse() {
    List<Stmt> statements = new ArrayList<>();
    while (!isAtEnd()) {
      statements.add(declaration());
    }
    return statements;
  }

  /**
   * Parses a declaration.
   *
   * @return The parsed statement.
   */
  private Stmt declaration() {
    try {
      if (match(TokenType.CLASS)) return classDeclaration();
      if (match(TokenType.DEF)) return function("function");
      if (match(TokenType.VAL)) return valDeclaration();
      return statement();
    } catch (ParsingError error) {
      System.err.println(error);
      synchronize();
      return null;
    }
  }

  /**
   * Parses a class declaration.
   *
   * @return The parsed class declaration.
   */
  private Stmt classDeclaration() {
    Token name = consume(TokenType.IDENTIFIER, "Expected class name");

    Variable superclass = null;
    if (match(TokenType.LESS)) {
      consume(TokenType.IDENTIFIER, "Expected superclass name");
      superclass = new Variable(previous());
    }

    consume(TokenType.LEFT_BRACE, "Expected '{' before class body");

    List<Function> methods = new ArrayList<>();
    while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
      methods.add(function("method"));
    }

    consume(TokenType.RIGHT_BRACE, "Expected '}' after class body");

    return new Class(name, superclass, methods);
  }

  /**
   * Parses a statement.
   *
   * @return The parsed statement.
   */
  private Stmt statement() {
    if (match(TokenType.FOR)) return forStatement();
    if (match(TokenType.IF)) return ifStatement();
    if (match(TokenType.RETURN)) return returnStatement();
    if (match(TokenType.WHILE)) return whileStatement();
    if (match(TokenType.LEFT_BRACE)) return new Block(block());

    return expressionStatement();
  }

  /**
   * Parses a for statement.
   *
   * @return The parsed for statement.
   */
  private Stmt forStatement() {
    consume(TokenType.LEFT_PARENTHESIS, "Expected '(' after 'for'");

    Stmt initializer;
    if (match(TokenType.SEMICOLON)) {
      initializer = null;
    } else if (match(TokenType.VAL)) {
      initializer = valDeclaration();
    } else {
      initializer = expressionStatement();
    }

    Expr condition = null;
    if (!check(TokenType.SEMICOLON)) {
      condition = expression();
    }
    consume(TokenType.SEMICOLON, "Expected ';' after loop condition");

    Expr increment = null;
    if (!check(TokenType.RIGHT_PARENTHESIS)) {
      increment = expression();
    }
    consume(TokenType.RIGHT_PARENTHESIS, "Expected ')' after for clauses");

    Stmt body = statement();

    if (increment != null) {
      body = new Block(Arrays.asList(body, new Expression(increment)));
    }

    if (condition == null) condition = new Literal(true, Type.BOOLEAN);
    body = new While(condition, body);

    if (initializer != null) {
      body = new Block(Arrays.asList(initializer, body));
    }
    return body;
  }

  /**
   * Parses an if statement.
   *
   * @return The parsed if statement.
   */
  private Stmt ifStatement() {
    consume(TokenType.LEFT_PARENTHESIS, "Expected '(' after 'if'");
    Expr condition = expression();
    consume(TokenType.RIGHT_PARENTHESIS, "Expected ')' after if condition");

    Stmt thenBranch = statement();
    Stmt elseBranch = null;
    if (match(TokenType.ELSE)) {
      elseBranch = statement();
    }

    return new If(condition, thenBranch, elseBranch);
  }

  /**
   * Parses a return statement.
   *
   * @return The parsed return statement.
   */
  private Stmt returnStatement() {
    Token keyword = previous();
    Expr value = null;
    if (!check(TokenType.SEMICOLON)) {
      value = expression();
    }

    consume(TokenType.SEMICOLON, "Expected ';' after return value");
    return new Return(keyword, value);
  }

  /**
   * Parses a variable declaration.
   *
   * @return The parsed variable declaration.
   */
  private Stmt valDeclaration() {
    Token name = consume(TokenType.IDENTIFIER, "Expected variable name");

    consume(TokenType.COLON, "Expected ':'");
    TypeExpr type = this.typeExpression();

    Expr initializer = null;
    if (match(TokenType.EQUAL)) {
      initializer = expression();
    }

    consume(TokenType.SEMICOLON, "Expected ';' after variable declaration");
    return new Val(name, type, initializer);
  }

  /**
   * Parses a while statement.
   *
   * @return The parsed while statement.
   */
  private Stmt whileStatement() {
    consume(TokenType.LEFT_PARENTHESIS, "Expected '(' after 'while'");
    Expr condition = expression();
    consume(TokenType.RIGHT_PARENTHESIS, "Expected ')' after condition");
    Stmt body = statement();

    return new While(condition, body);
  }

  /**
   * Parses an expression statement.
   *
   * @return The parsed expression statement.
   */
  private Stmt expressionStatement() {
    Expr expr = expression();
    consume(TokenType.SEMICOLON, "Expected ';' after expression");
    return new Expression(expr);
  }

  // Expression parsing methods

  /**
   * Parses an expression.
   *
   * @return The parsed expression.
   */
  private Expr expression() {
    return assignment();
  }

  /**
   * Parses an assignment expression.
   *
   * @return The parsed assignment expression.
   */
  private Expr assignment() {
    Expr expr = ternary();

    if (match(TokenType.EQUAL)) {
      Token equals = previous();
      Expr value = assignment();

      if (expr instanceof Variable) {
        Token name = ((Variable) expr).name;
        return new Assign(name, value);
      } else if (expr instanceof Get get) {
        return new Set(get.object, get.name, value);
      }

      throw new ParsingError(
          "Invalid assignment target", equals.line, equals.col, lexer.getSourceLine(equals.line));
    }
    return expr;
  }

  /**
   * Parses a ternary expression.
   *
   * @return The parsed ternary expression.
   */
  private Expr ternary() {
    Expr expr = or();

    if (match(TokenType.QUESTION)) {
      Token leftOperator = previous();
      Expr thenBranch = or();

      if (this.match(TokenType.COLON)) {
        Token rightOperator = previous();
        Expr elseBranch = ternary();
        expr = new Ternary(expr, leftOperator, thenBranch, rightOperator, elseBranch);
      } else {
        throw new ParsingError(
            "Expected ':' after ternary operator '?'",
            peek().line,
            peek().col,
            lexer.getSourceLine(peek().line));
      }
    }
    return expr;
  }

  /**
   * Parses an OR expression.
   *
   * @return The parsed OR expression.
   */
  private Expr or() {
    Expr expr = and();

    while (match(TokenType.OR)) {
      Token operator = previous();
      Expr right = and();
      expr = new Logical(expr, operator, right);
    }

    return expr;
  }

  /**
   * Parses an AND expression.
   *
   * @return The parsed AND expression.
   */
  private Expr and() {
    Expr expr = equality();

    while (match(TokenType.AND)) {
      Token operator = previous();
      Expr right = equality();
      expr = new Logical(expr, operator, right);
    }

    return expr;
  }

  /**
   * Parses an equality expression.
   *
   * @return The parsed equality expression.
   */
  private Expr equality() {
    Expr expr = comparison();
    while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
      Token operator = previous();
      Expr right = comparison();
      expr = new Binary(expr, operator, right);
    }
    return expr;
  }

  /**
   * Parses a comparison expression.
   *
   * @return The parsed comparison expression.
   */
  private Expr comparison() {
    Expr expr = term();

    while (match(
        TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
      Token operator = previous();
      Expr right = term();
      expr = new Binary(expr, operator, right);
    }

    return expr;
  }

  /**
   * Parses a term expression.
   *
   * @return The parsed term expression.
   */
  private Expr term() {
    Expr expr = factor();

    while (match(TokenType.MINUS, TokenType.PLUS)) {
      Token operator = previous();
      Expr right = factor();
      expr = new Binary(expr, operator, right);
    }

    return expr;
  }

  /**
   * Parses a factor expression.
   *
   * @return The parsed factor expression.
   */
  private Expr factor() {
    Expr expr = exponent();

    while (match(TokenType.SLASH, TokenType.STAR)) {
      Token operator = previous();
      Expr right = exponent();
      expr = new Binary(expr, operator, right);
    }

    return expr;
  }

  /**
   * Parses an exponent expression.
   *
   * @return The parsed exponent expression.
   */
  private Expr exponent() {
    Expr expr = unary();

    while (match(TokenType.CARET)) {
      Token operator = previous();
      Expr right = unary();
      expr = new Binary(expr, operator, right);
    }

    return expr;
  }

  /**
   * Parses a unary expression.
   *
   * @return The parsed unary expression.
   */
  private Expr unary() {
    if (match(TokenType.BANG, TokenType.MINUS)) {
      Token operator = previous();
      Expr right = unary();
      return new Unary(operator, right);
    }

    return call();
  }

  /**
   * Parses a function call expression.
   *
   * @return The parsed function call expression.
   */
  private Expr call() {
    Expr expr = primary();

    while (true) {
      if (match(TokenType.LEFT_PARENTHESIS)) {
        expr = finishCall(expr);
      } else if (match(TokenType.DOT)) {
        Token name = consume(TokenType.IDENTIFIER, "Expected property name after '.'");
        expr = new Get(expr, name);
      } else {
        break;
      }
    }

    return expr;
  }

  /**
   * Finishes parsing a function call expression.
   *
   * @param callee The callee expression.
   * @return The parsed function call expression.
   */
  private Expr finishCall(Expr callee) {
    List<Expr> arguments = new ArrayList<>();
    if (!check(TokenType.RIGHT_PARENTHESIS)) {
      do {
        if (arguments.size() >= 255) {
          throw new ParsingError(
              "No more than 255 arguments",
              peek().line,
              peek().col,
              lexer.getSourceLine(peek().line));
        }
        arguments.add(expression());
      } while (match(TokenType.COMMA));
    }

    Token paren = consume(TokenType.RIGHT_PARENTHESIS, "Expected ')' after arguments.");

    return new Call(callee, paren, arguments);
  }

  /**
   * Parses a primary expression.
   *
   * @return The parsed primary expression.
   */
  private Expr primary() {
    if (match(TokenType.FALSE)) return new Literal(false, Type.BOOLEAN);
    if (match(TokenType.TRUE)) return new Literal(true, Type.BOOLEAN);
    if (match(TokenType.NULL)) return new Literal(null, Type.BOOLEAN);

    if (match(TokenType.NUMBER_LITERAL)) {
      return new Literal(previous().literal, Type.NUMBER);
    }

    if (match(TokenType.STRING_LITERAL)) {
      return new Literal(previous().literal, Type.STRING);
    }

    if (match(TokenType.SUPER)) {
      Token keyword = previous();
      consume(TokenType.DOT, "Expected '.' after 'super'");
      Token method = consume(TokenType.IDENTIFIER, "Expected superclass method name");
      return new Super(keyword, method);
    }

    if (match(TokenType.THIS)) return new This(previous());

    if (match(TokenType.IDENTIFIER)) {
      return new Variable(previous());
    }

    if (match(TokenType.LEFT_PARENTHESIS)) {
      Expr expr = expression();
      consume(TokenType.RIGHT_PARENTHESIS, "Expected ')' after expression");
      return new Grouping(expr);
    }

    throw new ParsingError(
        "Expected expression", peek().line, peek().col, lexer.getSourceLine(peek().line));
  }

  /**
   * Parses a function declaration.
   *
   * @param kind The kind of function (e.g., "function").
   * @return The parsed function declaration.
   */
  private Function function(String kind) {
    final Token name = consume(TokenType.IDENTIFIER, "Expected " + kind + " name");

    consume(TokenType.LEFT_PARENTHESIS, "Expected '(' after " + kind + " name");
    final List<Parameter> parameters = parameters();
    consume(TokenType.RIGHT_PARENTHESIS, "Expected ')' after parameters");

    consume(TokenType.RIGHT_ARROW, "Expected '->'");
    final TypeExpr return_type = typeExpression();
    consume(TokenType.LEFT_BRACE, "Expected '{' before " + kind + " body");

    List<Stmt> body = block();

    return new Function(name, parameters, return_type, body);
  }

  /**
   * Parses a block of statements.
   *
   * @return The parsed block of statements.
   */
  private List<Stmt> block() {
    List<Stmt> statements = new ArrayList<>();

    while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
      statements.add(declaration());
    }

    consume(TokenType.RIGHT_BRACE, "Expected '}' after block");
    return statements;
  }

  /**
   * Parses parameters of a function or method declaration.
   *
   * @return The parsed parameters.
   */
  private List<Parameter> parameters() {
    List<Parameter> parameters = new ArrayList<>();

    if (!check(TokenType.RIGHT_PARENTHESIS)) {
      do {
        if (parameters.size() >= 255) {
          throw new ParsingError(
              "No more than 255 parameters",
              peek().line,
              peek().col,
              lexer.getSourceLine(peek().line));
        }
        Token name = consume(TokenType.IDENTIFIER, "Expected parameter name");
        consume(TokenType.COLON, "Expected ':' after parameter name");
        TypeExpr type = typeExpression();
        parameters.add(new Parameter(name, type));
      } while (match(TokenType.COMMA));
    }

    return parameters;
  }

  /**
   * Parses a type expression.
   *
   * @return The parsed type expression.
   */
  private TypeExpr typeExpression() {
    if (!match(
        TokenType.BOOLEAN,
        TokenType.IDENTIFIER,
        TokenType.NUMBER,
        TokenType.STRING,
        TokenType.VOID)) {
      throw new ParsingError(
          "Expected type expression", peek().line, peek().col, lexer.getSourceLine(peek().line));
    }

    final Token token = previous();
    if (token.type == TokenType.IDENTIFIER) {
      return new IdentifierTypeExpr(token, Type.IDENTIFIER);
    } else {
      return switch (token.type) {
        case BOOLEAN -> new TypeExpr(Type.BOOLEAN);
        case NUMBER -> new TypeExpr(Type.NUMBER);
        case STRING -> new TypeExpr(Type.STRING);
        case VOID -> new TypeExpr(Type.VOID);
          // Unreachable
        default -> null;
      };
    }
  }

  /**
   * Checks if the current token matches any of the given token types and consumes it if it does.
   *
   * @param tokenTypes The token types to match against.
   * @return true if a match is found, false otherwise.
   */
  private boolean match(TokenType... tokenTypes) {
    for (TokenType tokenType : tokenTypes) {
      if (check(tokenType)) {
        advance();
        return true;
      }
    }
    return false;
  }

  /**
   * Consumes the current token if it matches the specified token type, otherwise throws an error.
   *
   * @param type The expected token type.
   * @param message The error message to display if the token type does not match.
   * @return The consumed token.
   */
  private Token consume(TokenType type, String message) {
    if (check(type)) return advance();

    throw new ParsingError(message, peek().line, peek().col, lexer.getSourceLine(peek().line));
  }

  /**
   * Checks if the current token's type matches the specified type.
   *
   * @param type The token type to check against.
   * @return true if the current token's type matches, false otherwise.
   */
  private boolean check(TokenType type) {
    if (isAtEnd()) return false;
    return peek().type == type;
  }

  /**
   * Advances to the next token and returns the previous token.
   *
   * @return The previous token.
   */
  private Token advance() {
    if (!isAtEnd()) current++;
    return previous();
  }

  /**
   * Checks if the current token is the end of input.
   *
   * @return true if the current token is the end of input, false otherwise.
   */
  private boolean isAtEnd() {
    return peek().type == TokenType.EOF;
  }

  /**
   * Returns the current token.
   *
   * @return The current token.
   */
  private Token peek() {
    return tokens.get(current);
  }

  /**
   * Returns the previous token.
   *
   * @return The previous token.
   */
  private Token previous() {
    return tokens.get(current - 1);
  }

  /** Discards tokens until a statement boundary is found. */
  private void synchronize() {
    advance();
    while (!isAtEnd()) {
      if (previous().type == TokenType.SEMICOLON) return;

      switch (peek().type) {
        case CLASS, FOR, DEF, IF, RETURN, VAL, WHILE -> {
          return;
        }
      }

      advance();
    }
  }
}
