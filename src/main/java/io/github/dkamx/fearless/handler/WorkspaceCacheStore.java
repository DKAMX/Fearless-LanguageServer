package io.github.dkamx.fearless.handler;

import ast.Program;
import java.net.URI;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.lsp4j.WorkspaceFolder;
import org.eclipse.lsp4j.WorkspaceFoldersChangeEvent;
import parser.Parser;

public class WorkspaceCacheStore {

  public static final Map<String, Program> PROGRAMS = new HashMap<>();
  public static final Map<String, ProgramInfo> PROGRAMS_INFO = new HashMap<>();
  public static final Map<String, String> WORKSPACE_FILES = new HashMap<>();
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

  public static Program getProgram(String fileUri) {
    return PROGRAMS.get(getWorkspaceFolder(fileUri));
  }

  public static ProgramInfo getProgramInfo(String fileUri) {
    return PROGRAMS_INFO.get(getWorkspaceFolder(fileUri));
  }

  public static boolean hasProgram(String fileUri) {
    return PROGRAMS.containsKey(getWorkspaceFolder(fileUri));
  }

  public static String getFileContent(String fileUri) {
    if (!WORKSPACE_FILES.containsKey(fileUri)) {
      WORKSPACE_FILES.put(fileUri, Parser.of(Path.of(URI.create(fileUri))).content());
    }
    return WORKSPACE_FILES.get(fileUri);
  }
}