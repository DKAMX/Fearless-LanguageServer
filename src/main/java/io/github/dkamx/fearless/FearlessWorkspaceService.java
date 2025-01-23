package io.github.dkamx.fearless;

import java.util.logging.Logger;
import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.services.WorkspaceService;

public class FearlessWorkspaceService implements WorkspaceService {

  private static final Logger LOGGER = Logger.getLogger(FearlessWorkspaceService.class.getName());
  private final FearlessLanguageServer server;

  public FearlessWorkspaceService(FearlessLanguageServer server) {
    this.server = server;
  }

  @Override
  public void didChangeConfiguration(DidChangeConfigurationParams didChangeConfigurationParams) {
    LOGGER.info("didChangeConfiguration\n%s".formatted(didChangeConfigurationParams));
    server.infoMessage("didChangeConfiguration\n%s".formatted(didChangeConfigurationParams));
  }

  @Override
  public void didChangeWatchedFiles(DidChangeWatchedFilesParams didChangeWatchedFilesParams) {
    LOGGER.info("didChangeWatchedFiles\n%s".formatted(didChangeWatchedFilesParams));
    server.infoMessage("didChangeWatchedFiles\n%s".formatted(didChangeWatchedFilesParams));
  }
}