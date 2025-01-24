package io.github.dkamx.fearless;

import io.github.dkamx.fearless.handler.CompletionHandler;
import io.github.dkamx.fearless.handler.SemanticTokensHandler;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.SemanticTokens;
import org.eclipse.lsp4j.SemanticTokensParams;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;

public class FearlessTextDocumentService implements TextDocumentService {

  private static final Logger LOGGER = Logger.getLogger(
      FearlessTextDocumentService.class.getName());
  private final FearlessLanguageServer server;
  private final CompletionHandler completionHandler;
  private final SemanticTokensHandler semanticTokensHandler;

  public FearlessTextDocumentService(FearlessLanguageServer server) {
    this.server = server;
    this.completionHandler = new CompletionHandler(server);
    this.semanticTokensHandler = new SemanticTokensHandler(server);
  }

  @Override
  public void didOpen(DidOpenTextDocumentParams didOpenTextDocumentParams) {
    LOGGER.info("didOpen\n%s".formatted(didOpenTextDocumentParams));
    server.infoMessage("didOpen\n%s".formatted(didOpenTextDocumentParams));
  }

  @Override
  public void didChange(DidChangeTextDocumentParams didChangeTextDocumentParams) {
  }

  @Override
  public void didClose(DidCloseTextDocumentParams didCloseTextDocumentParams) {
    LOGGER.info("didClose\n%s".formatted(didCloseTextDocumentParams));
    server.infoMessage("didClose\n%s".formatted(didCloseTextDocumentParams));
  }

  @Override
  public void didSave(DidSaveTextDocumentParams didSaveTextDocumentParams) {
    LOGGER.info("didSave\n%s".formatted(didSaveTextDocumentParams));
    server.infoMessage("didSave\n%s".formatted(didSaveTextDocumentParams));
  }

  @Override
  public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(
      CompletionParams position) {
    LOGGER.info("completion\n%s".formatted(position));
    server.infoMessage("completion\n%s".formatted(position));
    return CompletableFuture.supplyAsync(() -> Either.forLeft(completionHandler.handle(
        position.getTextDocument().getUri(), position.getPosition())));
  }

  @Override
  public CompletableFuture<SemanticTokens> semanticTokensFull(SemanticTokensParams params) {
    LOGGER.info("semanticTokensFull\n%s".formatted(params));
    server.infoMessage("semanticTokensFull\n%s".formatted(params));
    return CompletableFuture.supplyAsync(
        () -> semanticTokensHandler.handle(params.getTextDocument().getUri()));
  }
}