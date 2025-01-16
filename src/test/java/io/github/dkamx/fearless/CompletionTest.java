package io.github.dkamx.fearless;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ast.Program;
import io.github.dkamx.fearless.handler.CompletionLogic;
import java.util.List;
import main.CompilerFrontEnd.ProgressVerbosity;
import main.CompilerFrontEnd.Verbosity;
import main.InputOutput;
import main.java.LogicMainJava;
import org.junit.jupiter.api.Test;

public class CompletionTest {

  static String URI = "file:///F:/NewDevelopment/Fearless-LanguageServer/Dummy0.fear";

  @Test
  public void test() {
    var program = buildProgram(Sample.HELLO);
    var list = CompletionLogic.collectX(program, URI);
    assertEquals("[sys]", list.toString());
  }

  @Test
  public void test2() {
    var program = buildProgram(Sample.HELLO_BLOCK1);
    var list = CompletionLogic.collectX(program, URI);
    assertEquals("[sys, io]", list.toString());
  }

  @Test
  public void test3() {
    var program = buildProgram(Sample.HELLO_BLOCK1);
    var map = CompletionLogic.collectXSig(program, URI);
    assertEquals("[io, sys]", map.keySet().toString());
  }

  @Test
  public void test4() {
    var program = buildProgram(Sample.HELLO_BLOCK1);
    var map = CompletionLogic.collectMethod(program, URI);
    assertEquals("[#]", map.get("base.Main").toString());
  }

  @Test
  public void test5() {
    var program = buildProgram(Sample.HELLO_BLOCK1);
    var mapMethod = CompletionLogic.collectMethod(program, URI);
    var mapXSig = CompletionLogic.collectXSig(program, URI);
    var list = mapXSig.entrySet().stream()
        .flatMap(entry -> mapMethod.get(entry.getValue()).stream()
            .map(meth -> entry.getKey() + meth))
        .toList();
    assertEquals(12, list.size());
  }

  Program buildProgram(String content) {
    var io = InputOutput.programmaticAuto(List.of(content));
    var main = LogicMainJava.of(io, new Verbosity(true, true, ProgressVerbosity.Full));
    var fullProgram = main.parse();
    main.wellFormednessFull(fullProgram);
    var program = main.inference(fullProgram);
    main.wellFormednessCore(program);
    return program;
  }
}