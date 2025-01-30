package io.github.dkamx.fearless;

import static io.github.dkamx.fearless.TestHelper.URI;
import static io.github.dkamx.fearless.TestHelper.build;
import static io.github.dkamx.fearless.TestHelper.createRange;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.dkamx.fearless.handler.CompletionLogic;
import io.github.dkamx.fearless.visitor.ast.IdTypeVisitor;
import org.junit.jupiter.api.Test;

public class CompletionLogicTest {

  @Test
  public void testCollectProgramInfo() {
    var program = build(Sample.HELLO);
    var info = CompletionLogic.collectProgramInfo(program);
    assertTrue(info.types().containsKey("test.Test"));
    assertEquals("[base.Main]", info.types().get("test.Test").toString());
  }

  @Test
  public void testCollectProgramInfo2() {
    var program = build(Sample.HELLO);
    var info = CompletionLogic.collectProgramInfo(program);
    assertEquals(18, info.getMethods("base.Str").size());
  }

  @Test
  public void testFindDecs() {
    var program = build(Sample.HELLO_BLOCK1);
    var decs = CompletionLogic.findDecs(program, URI);
    assertEquals(1, decs.size());
  }

  @Test
  public void testAssistType() {
    var content = CompletionLogic.assistType(Sample.LIST, createRange(9, 26, 9, 27));
    assertEquals(Sample.LIST_CMPL, content);
  }

  @Test
  public void testAssistType2() {
    var content = CompletionLogic.assistType(Sample.LIST, createRange(6, 18, 9, 29));
    assertEquals(Sample.LIST_CMPL_BLOCK, content);
  }

  @Test
  public void testAssistType3() {
    var content = CompletionLogic.assistType(Sample.LIST2, createRange(9, 26, 9, 31));
    assertEquals(Sample.LIST2_CMPL, content);
  }

  @Test
  public void testCompletion() {
    var program = build(Sample.LIST_CMPL);
    var decs = CompletionLogic.findDecs(program, URI);
    var visitor = new IdTypeVisitor();
    decs.forEach(dec -> dec.lambda().accept(visitor));
    var result = visitor.getResult();
    assertEquals("[base.Nat]", result.values().toString());
  }

  @Test
  public void testCompletion2() {
    var program = build(Sample.LIST2_CMPL);
    var decs = CompletionLogic.findDecs(program, URI);
    var visitor = new IdTypeVisitor();
    decs.forEach(dec -> dec.lambda().accept(visitor));
    var result = visitor.getResult();
    assertEquals("[base.Str]", result.values().toString());
  }

  @Test
  public void testCompletion3() {
    var program = build(Sample.LIST_CMPL_BLOCK);
    var decs = CompletionLogic.findDecs(program, URI);
    var visitor = new IdTypeVisitor();
    decs.forEach(dec -> dec.lambda().accept(visitor));
    var result = visitor.getResult();
    assertEquals("[base.Str]", result.values().toString());
  }
}