package io.github.dkamx.fearless;

import generated.FearlessBaseVisitor;
import generated.FearlessParser.AliasContext;
import generated.FearlessParser.MContext;
import generated.FearlessParser.NudeProgramContext;
import generated.FearlessParser.SigContext;
import generated.FearlessParser.TopDecContext;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.Token;
import org.eclipse.lsp4j.SemanticTokenTypes;

public class SemanticTokenVisitor extends FearlessBaseVisitor {

  private final List<Token> tokens = new ArrayList<>();

  private boolean visitSig = false;

  public static int getTokenType(String symbolicName) {
    return FearlessTextDocumentService.TOKEN_TYPES.indexOf(
        FearlessTextDocumentService.TOKEN_TYPE_MAPPING.getOrDefault(symbolicName,
            SemanticTokenTypes.Keyword));
  }

  public List<Token> tokens() {
    return tokens;
  }

  @Override
  public Object visitNudeProgram(NudeProgramContext ctx) {
    // TODO
    var token = ctx.Pack().getSymbol();
    if (token != null) {
      tokens.add(token);
    }
    return super.visitNudeProgram(ctx);
  }

  @Override
  public Object visitAlias(AliasContext ctx) {
    tokens.add(ctx.Alias().getSymbol());
    tokens.add(ctx.As().getSymbol());
    return super.visitAlias(ctx);
  }

  @Override
  public Object visitTopDec(TopDecContext ctx) {
    // TODO
    return super.visitTopDec(ctx);
  }

  @Override
  public Object visitSig(SigContext ctx) {
    this.visitSig = true;
    return super.visitSig(ctx);
  }

  @Override
  public Object visitM(MContext ctx) {
    // meth -> sig -> m -> MName
    if (this.visitSig) {
      var token = ctx.MName().getSymbol();
      if (token != null) {
        tokens.add(token);
      }
      this.visitSig = false;
    }

    return super.visitM(ctx);
  }
}