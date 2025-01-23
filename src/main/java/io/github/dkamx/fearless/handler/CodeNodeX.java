package io.github.dkamx.fearless.handler;

import org.eclipse.lsp4j.Position;

public class CodeNodeX extends CodeNode {

  public CodeNodeX(Position start, Position end, String content) {
    super(start, end, Type.X, content);
  }
}