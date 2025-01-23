package io.github.dkamx.fearless.handler;

import org.eclipse.lsp4j.Position;

public class CodeNodeE extends CodeNode {

  public CodeNodeE(Position start, Position end, String content) {
    super(start, end, Type.E, content);
  }

  public boolean noTouchChild(int line, int character) {
    for (CodeNode child : children) {
      if (!child.onEdge(line, character)) {
        return false;
      }
    }
    return true;
  }
}