package io.github.dkamx.fearless;

import ast.Program;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import parser.Parser;

public class WorkspaceFileStore {

  public static Map<String, String> sourceFiles = new HashMap<>();
  public static Program programCache;

  public static void cacheFile(String uri) throws IOException {
    var path = Path.of(URI.create(uri));
    if (!path.toString().endsWith(".fear")) {
      return;
    }
    sourceFiles.put(uri, Parser.of(path).content());
  }
}