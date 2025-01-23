package io.github.dkamx.fearless;

import static io.github.dkamx.fearless.TestHelper.buildProgram;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ast.Program;
import failure.CompileError;
import org.junit.jupiter.api.Test;

public class DiagnosticsTest {

  @Test
  public void test() {
    try {
      Program program = buildProgram(Sample.HELLO_ERR);
    } catch (CompileError e) {
      assertTrue(e.pos().isPresent());
      var pos = e.pos().get();
      assertEquals(3, pos.line());
      assertEquals("syntaxError", e.name());
    }
  }
}