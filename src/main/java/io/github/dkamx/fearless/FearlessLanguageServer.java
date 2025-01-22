package io.github.dkamx.fearless;

import io.github.dkamx.fearless.handler.CompilerLogic;
import io.github.dkamx.fearless.handler.CompletionHandler;
import io.github.dkamx.fearless.handler.SemanticTokensHandler;
import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.InitializedParams;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.SetTraceParams;
import org.eclipse.lsp4j.TextDocumentSyncKind;
import org.eclipse.lsp4j.WorkDoneProgressCancelParams;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.NotebookDocumentService;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

public class FearlessLanguageServer implements LanguageServer, LanguageClientAware {

  public LanguageClient client;
  public CompilerLogic compilerLogic = new CompilerLogic(this);
  FearlessTextDocumentService textDocumentService = new FearlessTextDocumentService(this);
  FearlessWorkspaceService workspaceService = new FearlessWorkspaceService(this);

  @Override
  public CompletableFuture<InitializeResult> initialize(InitializeParams initializeParams) {
    this.compilerLogic.setUriWorkspace(initializeParams.getWorkspaceFolders().getFirst().getUri());
    var capabilities = new ServerCapabilities();
    capabilities.setCompletionProvider(CompletionHandler.PROVIDER);
    capabilities.setSemanticTokensProvider(SemanticTokensHandler.PROVIDER);
    capabilities.setTextDocumentSync(TextDocumentSyncKind.Full);
    return CompletableFuture.completedFuture(new InitializeResult(capabilities));
  }

  @Override
  public void initialized(InitializedParams params) {
    this.infoMessage("initialized: %s".formatted(params));
  }

  @Override
  public CompletableFuture<Object> shutdown() {
    this.infoMessage("shutdown");

    return null;
  }

  @Override
  public void exit() {
  }

  @Override
  public NotebookDocumentService getNotebookDocumentService() {
    return LanguageServer.super.getNotebookDocumentService();
  }

  @Override
  public TextDocumentService getTextDocumentService() {
    this.infoMessage("getTextDocumentService");

    return this.textDocumentService;
  }

  @Override
  public WorkspaceService getWorkspaceService() {
    this.infoMessage("getWorkspaceService");

    return this.workspaceService;
  }

  @Override
  public void cancelProgress(WorkDoneProgressCancelParams params) {
    LanguageServer.super.cancelProgress(params);
  }

  @Override
  public void setTrace(SetTraceParams params) {
    LanguageServer.super.setTrace(params);
  }

  @Override
  public void connect(LanguageClient languageClient) {
    this.client = languageClient;
  }

  public void errorMessage(String message) {
    if (this.client == null) {
      return;
    }
    this.client.logMessage(new MessageParams(MessageType.Error, message));
  }

  public void infoMessage(String message) {
    if (this.client == null) {
      return;
    }
    this.client.logMessage(new MessageParams(MessageType.Info, message));
  }

  @JsonRequest("fearless/build")
  public CompletableFuture<String> build(BuildParams message) {
    this.infoMessage("Fearless build: %s".formatted(message.uri()));

    var program = this.compilerLogic.build();
    if (program == null) {
      return CompletableFuture.completedFuture("Build failed");
    }
    WorkspaceFileStore.programCache = program;
    return CompletableFuture.completedFuture("Build done");
  }
}