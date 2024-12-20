package io.github.dkamx.fearless;

import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.services.WorkspaceService;

public class FearlessWorkspaceService implements WorkspaceService {

  private FearlessLanguageServer server;

  public FearlessWorkspaceService(FearlessLanguageServer server) {
    this.server = server;
  }

  @Override
  public void didChangeConfiguration(DidChangeConfigurationParams didChangeConfigurationParams) {
  }

  @Override
  public void didChangeWatchedFiles(DidChangeWatchedFilesParams didChangeWatchedFilesParams) {
  }
}