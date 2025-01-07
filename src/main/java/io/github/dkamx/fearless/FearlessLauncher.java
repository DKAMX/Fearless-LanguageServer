package io.github.dkamx.fearless;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.util.concurrent.ExecutionException;
import org.eclipse.lsp4j.launch.LSPLauncher;

public class FearlessLauncher {

  public static void main(String[] args)
      throws ExecutionException, InterruptedException, IOException {
    remoteLaunch();
  }

  static void launch(InputStream input, OutputStream output)
      throws ExecutionException, InterruptedException {
    var server = new FearlessLanguageServer();
    var launcher = LSPLauncher.createServerLauncher(server, input, output);
    server.connect(launcher.getRemoteProxy());
    launcher.startListening().get();
  }

  static void remoteLaunch() throws IOException, ExecutionException, InterruptedException {
    var serverSocket = new ServerSocket(49160);
    var clientSocket = serverSocket.accept();

    System.out.println("Client connected");

    launch(clientSocket.getInputStream(), clientSocket.getOutputStream());

    serverSocket.close();
    clientSocket.close();
  }
}