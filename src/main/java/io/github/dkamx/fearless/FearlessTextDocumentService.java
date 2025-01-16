package io.github.dkamx.fearless;

import io.github.dkamx.fearless.handler.CompletionHandler;
import io.github.dkamx.fearless.handler.SemanticTokensHandler;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.SemanticTokens;
import org.eclipse.lsp4j.SemanticTokensDelta;
import org.eclipse.lsp4j.SemanticTokensDeltaParams;
import org.eclipse.lsp4j.SemanticTokensParams;
import org.eclipse.lsp4j.SemanticTokensRangeParams;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;

public class FearlessTextDocumentService implements TextDocumentService {

  private final FearlessLanguageServer server;

  public FearlessTextDocumentService(FearlessLanguageServer server) {
    this.server = server;
  }

  @Override
  public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(
      CompletionParams position) {
    // TODO program should be cached automatically
    var uri = position.getTextDocument().getUri();
    var pos = position.getPosition();
    return CompletableFuture.supplyAsync(
        () -> Either.forLeft(CompletionHandler.handle(uri, pos.getLine(), pos.getCharacter())));
  }

  @Override
  public void didOpen(DidOpenTextDocumentParams didOpenTextDocumentParams) {
    this.server.infoMessage("didOpen: %s".formatted(didOpenTextDocumentParams));

    try {
      WorkspaceFileStore.cacheFile(didOpenTextDocumentParams.getTextDocument().getUri());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void didChange(DidChangeTextDocumentParams didChangeTextDocumentParams) {
//    this.server.logMessage("didChange: %s".formatted(didChangeTextDocumentParams));

    try {
      WorkspaceFileStore.cacheFile(didChangeTextDocumentParams.getTextDocument().getUri());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void didClose(DidCloseTextDocumentParams didCloseTextDocumentParams) {
    this.server.infoMessage("didClose: %s".formatted(didCloseTextDocumentParams));
  }

  @Override
  public void didSave(DidSaveTextDocumentParams didSaveTextDocumentParams) {
    this.server.infoMessage("didSave: %s".formatted(didSaveTextDocumentParams));

    try {
      WorkspaceFileStore.cacheFile(didSaveTextDocumentParams.getTextDocument().getUri());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public CompletableFuture<SemanticTokens> semanticTokensFull(SemanticTokensParams params) {
    this.server.infoMessage("semanticTokensFull: %s".formatted(params));

    return CompletableFuture.supplyAsync(
        () -> {
          try {
            return SemanticTokensHandler.handle(params.getTextDocument().getUri());
          } catch (IOException e) {
            this.server.errorMessage("Error: %s".formatted(e));
            return new SemanticTokens(List.of());
          }
        });
  }

  @Override
  public CompletableFuture<Either<SemanticTokens, SemanticTokensDelta>> semanticTokensFullDelta(
      SemanticTokensDeltaParams params) {
    return TextDocumentService.super.semanticTokensFullDelta(params);
  }

  @Override
  public CompletableFuture<SemanticTokens> semanticTokensRange(SemanticTokensRangeParams params) {
    return TextDocumentService.super.semanticTokensRange(params);
  }
}