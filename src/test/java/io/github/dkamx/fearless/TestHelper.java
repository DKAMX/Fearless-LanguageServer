package io.github.dkamx.fearless;

import ast.Program;
import java.util.List;
import main.CompilerFrontEnd.ProgressVerbosity;
import main.CompilerFrontEnd.Verbosity;
import main.InputOutput;
import main.java.LogicMainJava;

public class TestHelper {

  static String URI = "file:///F:/NewDevelopment/Fearless-LanguageServer/Dummy0.fear";

  public static Program buildProgram(String content) {
    var io = InputOutput.programmaticAuto(List.of(content));
    var main = LogicMainJava.of(io, new Verbosity(true, true, ProgressVerbosity.Full));
    var fullProgram = main.parse();
    main.wellFormednessFull(fullProgram);
    var program = main.inference(fullProgram);
    main.wellFormednessCore(program);
    return program;
  }
}