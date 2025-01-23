package io.github.dkamx.fearless;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.dkamx.fearless.handler.CodeNode;
import io.github.dkamx.fearless.handler.CodeNode.Type;
import io.github.dkamx.fearless.handler.CodeNodeX;
import org.junit.jupiter.api.Test;

public class CodeNodeTest {

  @Test
  public void test() {
    var node = CodeNode.createNode(Sample.LIST);
    assertEquals(12, node.end.getLine());
    CodeNodeX child = (CodeNodeX) node.findNode(8, 27, Type.X);
    assertEquals("e", child.content);
  }
}