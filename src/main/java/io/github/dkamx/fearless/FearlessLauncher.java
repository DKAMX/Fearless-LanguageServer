package io.github.dkamx.fearless;

import java.util.concurrent.ExecutionException;
import org.eclipse.lsp4j.launch.LSPLauncher;

public class FearlessLauncher {

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    var server = new FearlessLanguageServer();
    var launcher = LSPLauncher.createServerLauncher(server, System.in,
        System.out);
    server.connect(launcher.getRemoteProxy());
    launcher.startListening().get();
  }
}