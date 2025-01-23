package io.github.dkamx.fearless.handler;

import generated.FearlessLexer;
import generated.FearlessParser;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
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
  public String content;

  public CodeNode(Position start, Position end, Type type, String content) {
    this.start = start;
    this.end = end;
    this.type = type;
    this.content = content;
  }

  public static CodeNode createNode(String content) {
    var lexer = new FearlessLexer(CharStreams.fromString(content));
    var tokenStream = new CommonTokenStream(lexer);
    tokenStream.fill();
    var parser = new FearlessParser(tokenStream);
    var listener = new CodeNodeListener();
    var treeWalker = new ParseTreeWalker();
    treeWalker.walk(listener, parser.nudeProgram());
    return listener.root;
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

  public boolean onEdge(int line, int character) {
    return start.getLine() == line && start.getCharacter() == character
        || end.getLine() == line && end.getCharacter() == character;
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
    NudeProgram, Alias, TopDec, Block, Bblock, E, PostE, CallOp, X
  }
}