package io.github.dkamx.fearless;

import io.github.dkamx.fearless.handler.CompletionHandler;
import io.github.dkamx.fearless.handler.SemanticTokensHandler;
import java.io.IOException;
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
  private final CompletionHandler CompletionHandler;

  public FearlessTextDocumentService(FearlessLanguageServer server) {
    this.server = server;
    this.CompletionHandler = new CompletionHandler(server);
  }

  @Override
  public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(
      CompletionParams position) {
    var uri = position.getTextDocument().getUri();
    var pos = position.getPosition();
    if (WorkspaceFileStore.programCache == null) {
      WorkspaceFileStore.programCache = this.server.compilerLogic.build();
    }
    return CompletableFuture.supplyAsync(
        () -> Either.forLeft(CompletionHandler.handle(uri, pos.getLine(), pos.getCharacter() - 1)));
  }

  @Override
  public void didOpen(DidOpenTextDocumentParams didOpenTextDocumentParams) {
    this.server.infoMessage("didOpen: %s".formatted(didOpenTextDocumentParams));

    WorkspaceFileStore.cacheFile(didOpenTextDocumentParams.getTextDocument().getUri());
  }

  @Override
  public void didChange(DidChangeTextDocumentParams didChangeTextDocumentParams) {
//    this.server.logMessage("didChange: %s".formatted(didChangeTextDocumentParams));
//    var uri = didChangeTextDocumentParams.getTextDocument().getUri();
//    var content = didChangeTextDocumentParams.getContentChanges().getFirst().getText();
//    WorkspaceFileStore.cacheFile(uri, content);
  }

  @Override
  public void didClose(DidCloseTextDocumentParams didCloseTextDocumentParams) {
    this.server.infoMessage("didClose: %s".formatted(didCloseTextDocumentParams));
  }

  @Override
  public void didSave(DidSaveTextDocumentParams didSaveTextDocumentParams) {
    this.server.infoMessage("didSave: %s".formatted(didSaveTextDocumentParams));

    WorkspaceFileStore.cacheFile(didSaveTextDocumentParams.getTextDocument().getUri());
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