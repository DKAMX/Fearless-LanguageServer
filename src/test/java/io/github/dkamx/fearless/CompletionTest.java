package io.github.dkamx.fearless;

import static io.github.dkamx.fearless.TestHelper.URI;
import static io.github.dkamx.fearless.TestHelper.buildProgram;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.dkamx.fearless.handler.CodeNode;
import io.github.dkamx.fearless.handler.CodeNode.Type;
import io.github.dkamx.fearless.handler.CodeNodeX;
import io.github.dkamx.fearless.handler.CompletionLogic;
import io.github.dkamx.fearless.visitors.XTypeVisitor;
import org.junit.jupiter.api.Test;

public class CompletionTest {

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

  @Test
  public void test6() {
    var program = buildProgram(Sample.LIST_CMPL);
    var decs = CompletionLogic.findDecs(program, URI);
    var lambda = decs.getFirst().lambda();
    var visitor = new XTypeVisitor();
    lambda.accept(visitor);
    assertEquals("{e=base.Nat}", visitor.getResult().toString());
  }

  @Test
  public void testAssistXType() {
    var node = CodeNode.createNode(Sample.LIST);
    var xNode = node.findNode(8, 27, Type.X);
    var content = CompletionLogic.assistType(Sample.LIST, (CodeNodeX) xNode);
    assertEquals(Sample.LIST_CMPL, content);
  }

  @Test
  public void testAssistXType2() {
    var node = CodeNode.createNode(Sample.LIST);
    var eNode = node.findNode(8, 29, Type.E);
    var content = CompletionLogic.assistType(Sample.LIST, eNode);
    assertEquals(Sample.LIST_CMPL_BLOCK, content);
  }

  @Test
  public void testCompleteMethod() {
    var program = buildProgram(Sample.LIST_CMPL);
    var map = CompletionLogic.collectMethod(program, URI);
    assertEquals(
        "[.byte, **, <=, .hash, .shiftRight, .assertEq, .bitwiseAnd, .abs, .str, .shiftLeft, .offset, .int, ==, .float, .nat, %, .bitwiseOr, *, +, -, /, .xor, <, !=, >, >=]",
        map.get("base.Nat").toString());
  }
}