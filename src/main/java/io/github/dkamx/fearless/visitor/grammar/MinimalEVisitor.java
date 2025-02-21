package io.github.dkamx.fearless.visitor.grammar;

import generated.FearlessBaseVisitor;
import generated.FearlessParser.EContext;
import io.github.dkamx.fearless.handler.GrammarTreeHelper;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

public class MinimalEVisitor extends FearlessBaseVisitor<Range> {

  private final Position position;
  private Range result;

  public MinimalEVisitor(Position position) {
    this.position = position;
  }

  public void setResult(Range result) {
    if (this.result == null) {
      this.result = result;
    }
  }

  @Override
  public Range visitE(EContext ctx) {
    var start = ctx.getStart();
    var end = ctx.getStop();
    var startPosition = new Position(start.getLine() - 1, start.getCharPositionInLine());
    if (GrammarTreeHelper.isTouchingTail(ctx, position)) {
      var endPosition = new Position(end.getLine() - 1,
          end.getCharPositionInLine() + end.getText().length());
      setResult(new Range(startPosition, endPosition));
    }
    if (GrammarTreeHelper.isWithin(ctx, position)) {
      // e: postE callOp*
      for (var child : ctx.callOp()) {
        if (GrammarTreeHelper.isTouchingTail(child, position)) {
          var stop = child.getStop();
          var endPosition = new Position(stop.getLine() - 1,
              stop.getCharPositionInLine() + stop.getText().length());
          setResult(new Range(startPosition, endPosition));
        }
      }
    }
    return this.visitChildren(ctx);
  }

  @Override
  public Range visitTerminal(TerminalNode node) {
    return result;
  }
}