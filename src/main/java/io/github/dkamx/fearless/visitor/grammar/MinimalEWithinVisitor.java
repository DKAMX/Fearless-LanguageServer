package io.github.dkamx.fearless.visitor.grammar;

import generated.FearlessBaseVisitor;
import generated.FearlessParser.EContext;
import io.github.dkamx.fearless.handler.GrammarTreeHelper;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

public class MinimalEWithinVisitor extends FearlessBaseVisitor<Range> {

  private final Position position;
  private Range result;

  public MinimalEWithinVisitor(Position position) {
    this.position = position;
  }

  @Override
  public Range visitE(EContext ctx) {
    var start = ctx.getStart();
    var end = ctx.getStop();
    var startPosition = new Position(start.getLine() - 1, start.getCharPositionInLine());
    var endPosition = new Position(end.getLine() - 1,
        end.getCharPositionInLine() + end.getText().length());
    if (GrammarTreeHelper.isWithin(ctx, position)) {
      if (result == null) {
        result = new Range(startPosition, endPosition);
      }
      result = GrammarTreeHelper.isProperSubrange(result, new Range(startPosition, endPosition))
          ? result : new Range(startPosition, endPosition);
    }
    return this.visitChildren(ctx);
  }

  @Override
  public Range visitTerminal(TerminalNode node) {
    return result;
  }
}