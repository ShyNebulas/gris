package com.github.gris;

import com.github.gris.ast.stmt.Stmt;
import com.github.gris.lexer.Lexer;
import com.github.gris.lexer.LexingError;
import com.github.gris.parser.Parser;
import com.github.gris.lexer.Token;
import com.github.gris.parser.ParsingError;
import com.github.gris.resolver.Resolver;
import com.github.gris.runtime.Interpreter;
import com.github.gris.typing.Typing;
import com.github.gris.typing.TypingError;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

/**
 * Main class for the Gris interpreter, which analyzes, interprets, and resolves Gris programming
 * language files.
 */
public class Gris {
  /**
   * Main method to execute Gris from the command line.
   *
   * @param args Command-line arguments.
   */
  public static void main(String... args) throws Exception {
    File file = new File("./samples/palindrome.gris");

    if (!file.toPath().getFileName().toString().endsWith(".gris"))
      throw new Exception("Expected '.gris' file.");
    if (file.length() == 0) throw new Exception("File can't be empty!");

    String contents = Files.readString(file.toPath());

    Lexer lexer = new Lexer(contents);
    try {
      List<Token> tokens = lexer.scanTokens();

      Parser parser = new Parser(lexer, tokens);
      List<Stmt> statements = parser.parse();

      Interpreter interpreter = new Interpreter(lexer);

      Resolver resolver = new Resolver(lexer, interpreter);
      resolver.resolve(statements);

      final Typing typing = new Typing(lexer, interpreter);
      typing.check(statements);

      interpreter.interpret(statements);
    } catch (LexingError | ParsingError | TypingError error) {
      System.err.println(error);
    }
  }
}
