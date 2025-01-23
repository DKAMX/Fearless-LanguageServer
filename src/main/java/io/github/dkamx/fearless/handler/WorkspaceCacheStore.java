package io.github.dkamx.fearless.handler;

import ast.Program;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.lsp4j.WorkspaceFolder;
import org.eclipse.lsp4j.WorkspaceFoldersChangeEvent;

public class WorkspaceCacheStore {

  public static final Map<String, Program> PROGRAMS = new HashMap<>();
  private static final Set<String> WORKSPACE_FOLDERS = new HashSet<>();

  public static void addWorkspaceFolder(List<WorkspaceFolder> folders) {
    folders.forEach(folder -> WORKSPACE_FOLDERS.add(folder.getUri()));
  }

  public static void handleWorkspaceFoldersChange(WorkspaceFoldersChangeEvent event) {
    event.getAdded().forEach(folder -> WORKSPACE_FOLDERS.add(folder.getUri()));
    event.getRemoved().forEach(folder -> WORKSPACE_FOLDERS.remove(folder.getUri()));
  }

  public static String getWorkspaceFolder(String fileUri) {
    return WORKSPACE_FOLDERS.stream()
        .filter(fileUri::startsWith)
        .findFirst()
        .orElse(null);
  }
}