package io.github.dkamx.fearless.handler;

import generated.FearlessLexer;
import generated.FearlessParser;
import io.github.dkamx.fearless.visitor.grammar.MinimalEVisitor;
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

  public static Range findMinimalE(FearlessParser parser, Position position) {
    var visitor = new MinimalEVisitor(position);
    return visitor.visit(parser.nudeProgram());
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
}