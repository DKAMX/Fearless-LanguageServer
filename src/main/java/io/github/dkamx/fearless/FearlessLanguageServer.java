package io.github.dkamx.fearless;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import main.CompilerFrontEnd.ProgressVerbosity;
import main.CompilerFrontEnd.Verbosity;
import main.InputOutput;
import main.java.LogicMainJava;
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

  LanguageClient client;
  FearlessTextDocumentService textDocumentService = new FearlessTextDocumentService(this);
  FearlessWorkspaceService workspaceService = new FearlessWorkspaceService(this);

  @Override
  public CompletableFuture<InitializeResult> initialize(InitializeParams initializeParams) {
    var capabilities = new ServerCapabilities();
    capabilities.setCompletionProvider(CompletionHandler.PROVIDER);
    capabilities.setSemanticTokensProvider(SemanticTokensHandler.PROVIDER);
    capabilities.setTextDocumentSync(TextDocumentSyncKind.Full);
    return CompletableFuture.completedFuture(new InitializeResult(capabilities));
  }

  @Override
  public void initialized(InitializedParams params) {
    this.logMessage("initialized: %s".formatted(params));
  }

  @Override
  public CompletableFuture<Object> shutdown() {
    this.logMessage("shutdown");

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
    this.logMessage("getTextDocumentService");

    return this.textDocumentService;
  }

  @Override
  public WorkspaceService getWorkspaceService() {
    this.logMessage("getWorkspaceService");

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

  public void logMessage(String message) {
    if (this.client == null) {
      return;
    }
    this.client.logMessage(new MessageParams(MessageType.Info, message));
  }

  @JsonRequest("fearless/build")
  public CompletableFuture<Void> build(BuildParams message) {
    this.logMessage("Fearless build: %s".formatted(message.uri()));

    var packagePath = Path.of(URI.create(message.uri()));
    var io = InputOutput.userFolder(null, List.of(), packagePath);
    var main = LogicMainJava.of(io, new Verbosity(true, true, ProgressVerbosity.Full));
    var fullProgram = main.parse();
    main.wellFormednessFull(fullProgram);
    var program = main.inference(fullProgram);
    main.wellFormednessCore(program);
    WorkspaceFileStore.programCache = program;

    return CompletableFuture.completedFuture(null);
  }
}