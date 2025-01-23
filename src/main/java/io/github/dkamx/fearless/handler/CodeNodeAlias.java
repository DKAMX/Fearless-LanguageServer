package io.github.dkamx.fearless.handler;

import org.eclipse.lsp4j.Position;

public class CodeNodeAlias extends CodeNode {

  public CodeNodeAlias(Position start, Position end, String content) {
    super(start, end, Type.Alias, content);
  }
}