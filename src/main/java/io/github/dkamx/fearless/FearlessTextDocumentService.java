package io.github.dkamx.fearless;

import io.github.dkamx.fearless.handler.CompletionHandler;
import io.github.dkamx.fearless.handler.SemanticTokensHandler;
import io.github.dkamx.fearless.handler.WorkspaceCacheStore;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.CompletionTriggerKind;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.SemanticTokens;
import org.eclipse.lsp4j.SemanticTokensParams;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;
import parser.Parser;

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
    var item = didOpenTextDocumentParams.getTextDocument();
    var uri = item.getUri();
    WorkspaceCacheStore.WORKSPACE_FILES.put(uri, Parser.of(Path.of(URI.create(uri))).content());
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
    var uri = didSaveTextDocumentParams.getTextDocument().getUri();
    var content = Parser.of(Path.of(URI.create(uri))).content();
    var previousContent = WorkspaceCacheStore.WORKSPACE_FILES.get(uri);
    if (content.equals(previousContent)) {
      return;
    }
    WorkspaceCacheStore.WORKSPACE_FILES.put(uri, content);
    server.getCompilationHandler().build(uri);
  }

  @Override
  public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(
      CompletionParams position) {
    LOGGER.info("completion\n%s".formatted(position));
    server.infoMessage("completion\n%s".formatted(position));
    var cursor = position.getPosition();
    var context = position.getContext();
    var isTriggerCharacter = context.getTriggerKind()
        .equals(CompletionTriggerKind.TriggerCharacter);
    if (isTriggerCharacter) {
      cursor = new Position(cursor.getLine(),
          cursor.getCharacter() - context.getTriggerCharacter().length());
    }
    var finalCursor = cursor;
    return CompletableFuture.supplyAsync(() -> Either.forLeft(completionHandler.handle(
        position.getTextDocument().getUri(), finalCursor, isTriggerCharacter)));
  }

  @Override
  public CompletableFuture<SemanticTokens> semanticTokensFull(SemanticTokensParams params) {
    LOGGER.info("semanticTokensFull\n%s".formatted(params));
    server.infoMessage("semanticTokensFull\n%s".formatted(params));
    return CompletableFuture.supplyAsync(
        () -> semanticTokensHandler.handle(params.getTextDocument().getUri()));
  }
}