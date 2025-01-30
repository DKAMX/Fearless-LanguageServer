package io.github.dkamx.fearless.handler;

import generated.FearlessLexer;
import generated.FearlessParser;
import io.github.dkamx.fearless.visitor.grammar.MinimalEVisitor;
import io.github.dkamx.fearless.visitor.grammar.MinimalEWithinVisitor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

public class GrammarTreeHelper {

  public static FearlessParser createParser(String content) {
    var lexer = new FearlessLexer(CharStreams.fromString(content));
    return new FearlessParser(new CommonTokenStream(lexer));
  }

  public static Range findMinimalE(String content, Position position) {
    return new MinimalEVisitor(position).visit(createParser(content).nudeProgram());
  }

  public static Range findMinimalEWithin(String content, Position position) {
    return new MinimalEWithinVisitor(position).visit(createParser(content).nudeProgram());
  }

  public static boolean isTouchingTail(ParserRuleContext context, Position position) {
    var end = context.getStop();
    return end.getLine() - 1 == position.getLine()
        && end.getCharPositionInLine() + end.getText().length() == position.getCharacter();
  }

  public static boolean isWithin(ParserRuleContext context, Position position) {
    var start = context.getStart();
    var end = context.getStop();
    var line = position.getLine();
    var character = position.getCharacter();
    return start.getLine() - 1 <= line && line <= end.getLine() - 1
        && start.getCharPositionInLine() < character
        && character < end.getCharPositionInLine() + end.getText().length();
  }

  public static boolean isWithin(Range range, Position position) {
    var start = range.getStart();
    var end = range.getEnd();
    var line = position.getLine();
    var character = position.getCharacter();
    return start.getLine() <= line && line <= end.getLine()
        && start.getCharacter() < character // not touching head/tail
        && character < end.getCharacter();
  }

  /**
   * Check if range A is a proper sub-range of B.
   */
  public static boolean isProperSubrange(Range a, Range b) {
    var startA = a.getStart();
    var endA = a.getEnd();
    var startB = b.getStart();
    var endB = b.getEnd();
    return (startB.getLine() < startA.getLine()
        || (startB.getLine() == startA.getLine() && startB.getCharacter() < startA.getCharacter()))
        && (endB.getLine() > endA.getLine()
        || (endB.getLine() == endA.getLine() && endB.getCharacter() > endA.getCharacter()));
  }
}