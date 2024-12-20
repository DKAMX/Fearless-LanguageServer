package io.github.dkamx.fearless;

import generated.FearlessBaseVisitor;
import generated.FearlessParser.AliasContext;
import generated.FearlessParser.NudeProgramContext;
import generated.FearlessParser.TopDecContext;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.Token;

public class SemanticTokenVisitor extends FearlessBaseVisitor {

  private final List<SemanticToken> tokens = new ArrayList<>();

  public static int getTokenType(String text) {
    return FearlessTextDocumentService.TOKEN_TYPES.indexOf(text);
  }

  public List<SemanticToken> tokens() {
    return tokens;
  }

  @Override
  public Object visitNudeProgram(NudeProgramContext ctx) {
    // TODO
    return super.visitNudeProgram(ctx);
  }

  @Override
  public Object visitAlias(AliasContext ctx) {
    tokens.add(SemanticToken.of(ctx.Alias().getSymbol()));
    tokens.add(SemanticToken.of(ctx.As().getSymbol()));
    return super.visitAlias(ctx);
  }

  @Override
  public Object visitTopDec(TopDecContext ctx) {
    // TODO
    return super.visitTopDec(ctx);
  }
}

/**
 * Semantic token that follows the LSP format (integer encoding for tokens)
 *
 * @param line           line index of the token (0-indexed)
 * @param startCharacter start index of the token in the line (0-indexed)
 * @param length         length of the token
 * @param tokenType      index of registered token type
 * @param tokenModifiers bitset of token modifiers as integer
 */
record SemanticToken(int line, int startCharacter, int length, int tokenType, int tokenModifiers) {

  /**
   * Create a semantic token from an ANTLR token
   *
   * @param token token
   * @return semantic token
   */
  public static SemanticToken of(Token token) {
    return new SemanticToken(token.getLine(), token.getCharPositionInLine(),
        token.getText().length(), SemanticTokenVisitor.getTokenType(token.getText()), 0);
  }

  /**
   * @return data represent a semantic token (LSP format)
   */
  public int[] data() {
    return new int[]{line, startCharacter, length, tokenType, tokenModifiers};
  }
}