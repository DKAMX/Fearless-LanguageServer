package io.github.dkamx.fearless;

import java.util.logging.Logger;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.services.TextDocumentService;

public class FearlessTextDocumentService implements TextDocumentService {

  private static final Logger LOGGER = Logger.getLogger(
      FearlessTextDocumentService.class.getName());
  private final FearlessLanguageServer server;

  public FearlessTextDocumentService(FearlessLanguageServer server) {
    this.server = server;
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
}