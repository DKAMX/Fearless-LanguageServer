package io.github.dkamx.fearless.handler;

import generated.FearlessBaseListener;
import generated.FearlessParser.AliasContext;
import generated.FearlessParser.BblockContext;
import generated.FearlessParser.BlockContext;
import generated.FearlessParser.NudeProgramContext;
import generated.FearlessParser.TopDecContext;
import io.github.dkamx.fearless.handler.CodeNode.Type;
import org.antlr.v4.runtime.ParserRuleContext;
import org.eclipse.lsp4j.Position;

public class CodeNodeListener extends FearlessBaseListener {

  public CodeNode root;

  private CodeNode parent;

  public CodeNode createNode(ParserRuleContext ctx, Type type) {
    var start = ctx.getStart().getLine() - 1;
    var startChar = ctx.getStart().getCharPositionInLine();
    var end = ctx.getStop().getLine() - 1;
    var endChar = ctx.getStop().getCharPositionInLine() + ctx.getStop().getText().length();
    return new CodeNode(new Position(start, startChar), new Position(end, endChar), type);
  }

  public void addNode(ParserRuleContext ctx, Type type) {
    var node = createNode(ctx, type);
    this.parent.addChild(node);
    this.parent = node;
  }

  @Override
  public void enterNudeProgram(NudeProgramContext ctx) {
    var node = createNode(ctx, Type.NudeProgram);
    this.root = node;
    this.parent = node;
  }

  @Override
  public void enterAlias(AliasContext ctx) {
    addNode(ctx, Type.Alias);
  }

  @Override
  public void enterTopDec(TopDecContext ctx) {
    addNode(ctx, Type.TopDec);
  }

  @Override
  public void enterBlock(BlockContext ctx) {
    addNode(ctx, Type.Block);
  }

  @Override
  public void enterBblock(BblockContext ctx) {
    addNode(ctx, Type.Bblock);
  }

  @Override
  public void exitAlias(AliasContext ctx) {
    this.parent = this.parent.parent;
  }

  @Override
  public void exitTopDec(TopDecContext ctx) {
    this.parent = this.parent.parent;
  }

  @Override
  public void exitBlock(BlockContext ctx) {
    this.parent = this.parent.parent;
  }

  @Override
  public void exitBblock(BblockContext ctx) {
    this.parent = this.parent.parent;
  }
}