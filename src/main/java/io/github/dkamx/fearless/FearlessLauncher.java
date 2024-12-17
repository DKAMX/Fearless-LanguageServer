package io.github.dkamx.fearless;

import java.util.concurrent.ExecutionException;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;

public class FearlessLauncher {

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    FearlessLanguageServer server = new FearlessLanguageServer();
    Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(server, System.in,
        System.out);
    server.connect(launcher.getRemoteProxy());
    launcher.startListening().get();
  }
}