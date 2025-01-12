package io.github.dkamx.fearless;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.eclipse.lsp4j.Position;

/**
 * A node that represents alias, topDec and other distinctive components of the ANTLR grammar.
 */
public class CodeNode {

  public CodeNode parent = null;
  public Set<CodeNode> children = new HashSet<>();
  public Position start;
  public Position end;
  public Type type;

  public CodeNode(Position start, Position end, Type type) {
    this.start = start;
    this.end = end;
    this.type = type;
  }

  public void setParent(CodeNode parent) {
    this.parent = parent;
  }

  public void addChild(CodeNode child) {
    if (child == null) {
      return;
    }
    child.setParent(this);
    this.children.add(child);
  }

  /**
   * Check if the node is placed before the given line and character.
   */
  public boolean before(int line, int character) {
    return end.getLine() < line || (end.getLine() == line && end.getCharacter() < character);
  }

  /**
   * Check if the node is placed after the given line and character.
   */
  public boolean after(int line, int character) {
    return start.getLine() > line || (start.getLine() == line && start.getCharacter() > character);
  }

  public boolean contains(int line, int character) {
    return !before(line, character) && !after(line, character);
  }

  public CodeNode findNode(int line, int character, Type type) {
    if (this.type == type && this.contains(line, character)) {
      return this;
    }
    if (this.children.isEmpty()) {
      return this;
    }
    for (var child : this.children) {
      if (child.contains(line, character)) {
        return child.findNode(line, character, type);
      }
    }
    return this;
  }

  @Override
  public final boolean equals(Object o) {
    if (!(o instanceof CodeNode codeNode)) {
      return false;
    }

    return Objects.equals(start, codeNode.start) && Objects.equals(end, codeNode.end);
  }

  @Override
  public int hashCode() {
    int result = Objects.hashCode(start);
    result = 31 * result + Objects.hashCode(end);
    return result;
  }

  public enum Type {
    NudeProgram, Alias, TopDec, Block, Bblock
  }
}