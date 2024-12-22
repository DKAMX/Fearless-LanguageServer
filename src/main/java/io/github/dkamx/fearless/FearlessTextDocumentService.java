package io.github.dkamx.fearless;

import generated.FearlessLexer;
import generated.FearlessParser;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.SemanticTokenTypes;
import org.eclipse.lsp4j.SemanticTokens;
import org.eclipse.lsp4j.SemanticTokensDelta;
import org.eclipse.lsp4j.SemanticTokensDeltaParams;
import org.eclipse.lsp4j.SemanticTokensLegend;
import org.eclipse.lsp4j.SemanticTokensParams;
import org.eclipse.lsp4j.SemanticTokensRangeParams;
import org.eclipse.lsp4j.SemanticTokensWithRegistrationOptions;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;

public class FearlessTextDocumentService implements TextDocumentService {

  public static final SemanticTokensWithRegistrationOptions SEMANTIC_TOKENS_PROVIDER;

  public static final List<String> TOKEN_TYPES = List.of(SemanticTokenTypes.Class,
      SemanticTokenTypes.Comment, SemanticTokenTypes.Keyword, SemanticTokenTypes.Method,
      SemanticTokenTypes.Namespace, // package name
      SemanticTokenTypes.Number, SemanticTokenTypes.Parameter, SemanticTokenTypes.String,
      SemanticTokenTypes.Type, // generic type
      SemanticTokenTypes.Variable);

  public static final Map<String, String> TOKEN_TYPE_MAPPING = Map.of("Alias",
      SemanticTokenTypes.Keyword, "As", SemanticTokenTypes.Keyword, "MName",
      SemanticTokenTypes.Method);

  static {
    // semantic tokens
    var legend = new SemanticTokensLegend();
    legend.setTokenTypes(TOKEN_TYPES);
    legend.setTokenModifiers(List.of());
    SEMANTIC_TOKENS_PROVIDER = new SemanticTokensWithRegistrationOptions();
    SEMANTIC_TOKENS_PROVIDER.setLegend(legend);
    SEMANTIC_TOKENS_PROVIDER.setRange(false);
    SEMANTIC_TOKENS_PROVIDER.setFull(true);
  }

  private FearlessLanguageServer server;

  public FearlessTextDocumentService(FearlessLanguageServer server) {
    this.server = server;
  }

  public static SemanticTokens encodeTokens(List<Token> tokens, FearlessLexer lexer) {
    var data = new ArrayList<Integer>();
    int prevLine = 1; // ?
    int prevChar = 0;

    for (var token : tokens) {
      // Calculate relative positions
      var line = token.getLine() - prevLine;
      var start = (line == 0) ? token.getCharPositionInLine() - prevChar
          : token.getCharPositionInLine();

      // Add token data
      data.add(line);
      data.add(start);
      data.add(token.getText().length());
      data.add(SemanticTokenVisitor.getTokenType(
          lexer.getVocabulary().getSymbolicName(token.getType())));
      data.add(0);

      // Update previous positions
      prevLine = token.getLine();
      prevChar = token.getCharPositionInLine();
    }

    return new SemanticTokens(data);
  }

  @Override
  public void didOpen(DidOpenTextDocumentParams didOpenTextDocumentParams) {
  }

  @Override
  public void didChange(DidChangeTextDocumentParams didChangeTextDocumentParams) {
  }

  @Override
  public void didClose(DidCloseTextDocumentParams didCloseTextDocumentParams) {
  }

  @Override
  public void didSave(DidSaveTextDocumentParams didSaveTextDocumentParams) {
  }

  @Override
  public CompletableFuture<SemanticTokens> semanticTokensFull(SemanticTokensParams params) {
    this.server.logMessage("semanticTokensFull: %s".formatted(params));

    try {
      var content = Files.readString(Path.of(URI.create(params.getTextDocument().getUri())));

      return CompletableFuture.supplyAsync(() -> {
        var lexer = new FearlessLexer(CharStreams.fromString(content.replace("\r", "")));
        var tokenStream = new CommonTokenStream(lexer);
        tokenStream.fill();

        var parser = new FearlessParser(tokenStream);
        var visitor = new SemanticTokenVisitor();
        visitor.visit(parser.nudeProgram());

        return encodeTokens(visitor.tokens(), lexer);
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public CompletableFuture<Either<SemanticTokens, SemanticTokensDelta>> semanticTokensFullDelta(
      SemanticTokensDeltaParams params) {
    this.server.logMessage("semanticTokensFullDelta: %s".formatted(params));

    return TextDocumentService.super.semanticTokensFullDelta(params);
  }

  @Override
  public CompletableFuture<SemanticTokens> semanticTokensRange(SemanticTokensRangeParams params) {
    this.server.logMessage("semanticTokensRange: %s".formatted(params));

    return TextDocumentService.super.semanticTokensRange(params);
  }
}