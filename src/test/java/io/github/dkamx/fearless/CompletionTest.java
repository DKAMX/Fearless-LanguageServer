package io.github.dkamx.fearless;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ast.Program;
import io.github.dkamx.fearless.handler.CompletionLogic;
import java.util.List;
import main.CompilerFrontEnd.ProgressVerbosity;
import main.CompilerFrontEnd.Verbosity;
import main.InputOutput;
import main.java.LogicMainJava;
import org.junit.jupiter.api.Test;

public class CompletionTest {

  public static Program build(String content) {
    var io = InputOutput.programmaticAuto(List.of(content));
    var main = LogicMainJava.of(io, new Verbosity(true, true, ProgressVerbosity.Full));
    var fullProgram = main.parse();
    main.wellFormednessFull(fullProgram);
    var program = main.inference(fullProgram);
    main.wellFormednessCore(program);
    return program;
  }

  @Test
  public void testCompletionLogic() {
    var program = build(Sample.HELLO);
    var info = CompletionLogic.collectProgramInfo(program);
    assertTrue(info.types().containsKey("test.Test"));
    assertEquals("[base.Main]", info.types().get("test.Test").toString());
  }

  @Test
  public void testProgramInfo() {
    var program = build(Sample.HELLO);
    var info = CompletionLogic.collectProgramInfo(program);
    assertEquals(18, info.getMethods("base.Str").size());
  }
}