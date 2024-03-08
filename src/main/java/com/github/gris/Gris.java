package com.github.gris;

import com.github.gris.ast.Stmt;
import com.github.gris.ast.printer.Printer;
import com.github.gris.lexer.Lexer;
import com.github.gris.parser.Parser;
import com.github.gris.token.Token;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.Callable;


// TODO: Add descriptions
@Command(name = "gris", mixinStandardHelpOptions = true, description = "")
public class Gris implements Callable<String> {

    @Parameters(index = "0", description = "")
    private File file;

    @Override
    public String call() throws Exception {
        String contents = Files.readString(file.toPath());
        Lexer lexer = new Lexer(contents);
        List<Token> tokens = lexer.scanTokens();
//        for(Token token : tokens) {
//            System.out.println(token);
//        }
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        for(Stmt statement : statements) {
            System.out.println(new Printer().print(statement));
        }


        return "";
    }

  public static void main(String... args) {
      int exitCode = new CommandLine(new Gris()).execute(args);
      System.exit(exitCode);
  }
}
