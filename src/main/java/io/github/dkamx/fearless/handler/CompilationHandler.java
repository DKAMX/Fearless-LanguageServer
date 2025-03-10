package io.github.dkamx.fearless.handler;

import ast.Program;
import failure.CompileError;
import files.Pos;
import io.github.dkamx.fearless.FearlessLanguageServer;
import java.net.URI;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import main.CompilerFrontEnd.ProgressVerbosity;
import main.CompilerFrontEnd.Verbosity;
import main.InputOutput;
import main.InputOutput.FieldsInputOutput;
import main.java.LogicMainJava;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.Range;
import parser.Parser;

/**
 * Handle both compilation of the program and publish diagnostics that relate to the code.
 */
public class CompilationHandler {

  private static final Logger LOGGER = Logger.getLogger(CompilationHandler.class.getName());
  private final FearlessLanguageServer server;
  private final Set<String> diagnosedFiles = new HashSet<>();

  public CompilationHandler(FearlessLanguageServer server) {
    this.server = server;
  }

  public static Range toRange(Pos pos) {
    return new Range(new Position(pos.line(), pos.column()),
        new Position(pos.line(), pos.column()));
  }

  static InputOutput replaceFileContent(InputOutput io, String uri, String content) {
    var inputFiles = io.inputFiles().stream()
        .map(parser -> {
          if (parser.fileName().equals(Path.of(URI.create(uri)))) {
            return new Parser(parser.fileName(), content);
          }
          return parser;
        })
        .toList();
    io = new FieldsInputOutput(io.entry(), io.commandLineArguments(), io.baseDir(), io.magicDir(),
        inputFiles, io.output(), io.cachedBase(), io.cachedFiles(), io.defaultAliases());
    return io;
  }

  /**
   * Build the program with substituted file content. This is intended for type assist with
   * variable.
   */
  public Program build(String uri, String content) {
    LOGGER.info("build (type assist)\n%s".formatted(uri));
    server.infoMessage("build (type assist)\n%s".formatted(uri));
    var folderUri = WorkspaceCacheStore.getWorkspaceFolder(uri);
    Program program = null;
    if (folderUri == null) {
      return program;
    }
    var io = InputOutput.userFolder(null, List.of(), Path.of(URI.create(folderUri)));
    io = replaceFileContent(io, uri, content);
    program = internalBuild(io);
    return program;
  }

  /**
   * Build the program and update error display (if build fails).
   */
  public Program build(String uri) {
    LOGGER.info("build\n%s".formatted(uri));
    server.infoMessage("build\n%s".formatted(uri));
    var folderUri = WorkspaceCacheStore.getWorkspaceFolder(uri);
    Program program = null;
    if (folderUri == null) {
      return program;
    }
    var io = InputOutput.userFolder(null, List.of(), Path.of(URI.create(folderUri)));
    program = internalBuild(io);
    if (program != null) {
      WorkspaceCacheStore.PROGRAMS.put(folderUri, program);
      WorkspaceCacheStore.PROGRAMS_INFO.put(folderUri, CompletionLogic.collectProgramInfo(program));
    }
    return program;
  }

  Program internalBuild(InputOutput io) {
    Program program = null;
    try {
      var main = LogicMainJava.of(io, new Verbosity(true, true, ProgressVerbosity.Full));
      var fullProgram = main.parse();
      main.wellFormednessFull(fullProgram);
      program = main.inference(fullProgram);
      main.wellFormednessCore(program);
      publishDiagnostics();
      server.getClient().showMessage(new MessageParams(MessageType.Info, "Build successful"));
    } catch (CompileError e) {
      publishDiagnostics(e);
    }
    return program;
  }

  void publishDiagnostics() {
    diagnosedFiles.forEach(
        uri -> server.getClient().publishDiagnostics(new PublishDiagnosticsParams(uri, List.of())));
  }

  void publishDiagnostics(CompileError e) {
    var message = e.name() + ": " + e.getMessage();
    if (e.pos().isEmpty()) {
      server.getClient().showMessage(new MessageParams(MessageType.Error, message));
      return;
    }
    var pos = e.pos().get();
    var uri = pos.fileName().toString();
    diagnosedFiles.add(uri);
    server.getClient().publishDiagnostics(
        new PublishDiagnosticsParams(uri, List.of(new Diagnostic(toRange(pos), message))));
  }
}