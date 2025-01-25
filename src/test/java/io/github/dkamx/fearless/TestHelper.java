package io.github.dkamx.fearless;

import ast.Program;
import java.util.List;
import main.CompilerFrontEnd.ProgressVerbosity;
import main.CompilerFrontEnd.Verbosity;
import main.InputOutput;
import main.java.LogicMainJava;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

public class TestHelper {

  /**
   * Sample URI used when code parsed from programmatic IO.
   */
  public static String URI = "file:///F:/NewDevelopment/Fearless-LanguageServer/Dummy0.fear";

  public static Program build(String content) {
    var io = InputOutput.programmaticAuto(List.of(content));
    var main = LogicMainJava.of(io, new Verbosity(true, true, ProgressVerbosity.Full));
    var fullProgram = main.parse();
    main.wellFormednessFull(fullProgram);
    var program = main.inference(fullProgram);
    main.wellFormednessCore(program);
    return program;
  }

  public static Range createRange(int startLine, int startChar, int endLine, int endChar) {
    return new Range(new Position(startLine, startChar), new Position(endLine, endChar));
  }
}