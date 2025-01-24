package io.github.dkamx.fearless;

import io.github.dkamx.fearless.handler.CompilationHandler;
import io.github.dkamx.fearless.handler.CompletionHandler;
import io.github.dkamx.fearless.handler.SemanticTokensHandler;
import io.github.dkamx.fearless.handler.WorkspaceCacheStore;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

public class FearlessLanguageServer implements LanguageServer, LanguageClientAware {

  private static final Logger LOGGER = Logger.getLogger(FearlessLanguageServer.class.getName());
  private final TextDocumentService textDocumentService = new FearlessTextDocumentService(this);
  private final WorkspaceService workspaceService = new FearlessWorkspaceService(this);
  private final CompilationHandler compilationHandler = new CompilationHandler(this);
  private LanguageClient client;

  @Override
  public CompletableFuture<InitializeResult> initialize(InitializeParams initializeParams) {
    LOGGER.info("initialize\n%s".formatted(initializeParams));
    WorkspaceCacheStore.addWorkspaceFolder(initializeParams.getWorkspaceFolders());
    // register server capabilities
    var serverCapabilities = new ServerCapabilities();
    serverCapabilities.setCompletionProvider(CompletionHandler.PROVIDER);
    serverCapabilities.setSemanticTokensProvider(SemanticTokensHandler.PROVIDER);
    return CompletableFuture.completedFuture(new InitializeResult(serverCapabilities));
  }

  @Override
  public CompletableFuture<Object> shutdown() {
    LOGGER.info("shutdown");
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public void exit() {
    LOGGER.info("exit");
  }

  @Override
  public TextDocumentService getTextDocumentService() {
    LOGGER.info("getTextDocumentService");
    return this.textDocumentService;
  }

  @Override
  public WorkspaceService getWorkspaceService() {
    LOGGER.info("getWorkspaceService");
    return this.workspaceService;
  }

  @Override
  public void connect(LanguageClient languageClient) {
    this.client = languageClient;
  }

  public LanguageClient getClient() {
    return client;
  }

  public CompilationHandler getCompilationHandler() {
    return compilationHandler;
  }

  public void errorMessage(String message) {
    this.client.logMessage(new MessageParams(MessageType.Error, message));
  }

  public void infoMessage(String message) {
    this.client.logMessage(new MessageParams(MessageType.Info, message));
  }

  @JsonRequest("fearless/build")
  public CompletableFuture<Void> build(BuildParams params) {
    LOGGER.info("fearless/build");
    this.infoMessage("fearless/build");
    compilationHandler.build(params.fileUri());
    return CompletableFuture.completedFuture(null);
  }
}