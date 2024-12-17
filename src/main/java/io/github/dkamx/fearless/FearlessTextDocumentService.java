package io.github.dkamx.fearless;

import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.services.TextDocumentService;

public class FearlessTextDocumentService implements TextDocumentService {

  FearlessLanguageServer server;

  public FearlessTextDocumentService(FearlessLanguageServer server) {
    this.server = server;
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
}