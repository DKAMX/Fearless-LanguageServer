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
import main.CompilerFrontEnd.ProgressVerbosity;
import main.CompilerFrontEnd.Verbosity;
import main.InputOutput;
import main.InputOutput.FieldsInputOutput;
import main.java.LogicMainJava;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.Range;
import parser.Parser;

public class CompilerLogic {

  FearlessLanguageServer server;
  /**
   * Track files that had errors. When error is fixed, the file list will be pushed to remove
   * previous error marks.
   */
  Set<String> trackedFiles = new HashSet<>();
  String uriWorkspace;

  public CompilerLogic(FearlessLanguageServer server) {
    this.server = server;
  }

  public static Range toRange(Pos pos) {
    return new Range(new Position(pos.line(), pos.column()),
        new Position(pos.line(), pos.column()));
  }

  public void setUriWorkspace(String uriWorkspace) {
    this.uriWorkspace = uriWorkspace;
  }

  /**
   * Handel both compile and publish diagnostics.
   */
  public Program build() throws CompileError {
    Program program = null;
    try {
      var userFolder = Path.of(URI.create(this.uriWorkspace));
      var io = InputOutput.userFolder(null, List.of(), userFolder);
      program = internalBuild(io);

      this.trackedFiles.forEach( // Clear previous error marks
          file -> this.server.client.publishDiagnostics(
              new PublishDiagnosticsParams(file, List.of())));
    } catch (CompileError e) {
      e.pos().ifPresentOrElse(pos -> {
        var diag = new Diagnostic(toRange(pos), e.name() + ": " + e.getMessage());
        var params = new PublishDiagnosticsParams(pos.fileName().toString(), List.of(diag));
        this.server.client.publishDiagnostics(params);
        this.trackedFiles.add(pos.fileName().toString());
      }, () -> this.server.errorMessage("Error without position: %s".formatted(e)));
    }
    return program;
  }

  public Program build(String uriAssist, String contentAssist) {
    Program program = null;
    try {
      var userFolder = Path.of(URI.create(this.uriWorkspace));
      var io = InputOutput.userFolder(null, List.of(), userFolder);

      io = replaceUriOnCurrentFile(io, uriAssist, contentAssist);
      program = internalBuild(io);

      this.trackedFiles.forEach( // Clear previous error marks
          file -> this.server.client.publishDiagnostics(
              new PublishDiagnosticsParams(file, List.of())));
    } catch (CompileError e) {
      e.pos().ifPresentOrElse(pos -> {
        var diag = new Diagnostic(toRange(pos), e.name() + ": " + e.getMessage());
        var params = new PublishDiagnosticsParams(pos.fileName().toString(), List.of(diag));
        this.server.client.publishDiagnostics(params);
        this.trackedFiles.add(pos.fileName().toString());
      }, () -> this.server.errorMessage("Error without position: %s".formatted(e)));
    }
    return program;
  }

  Program internalBuild(InputOutput io) throws CompileError {
    Program program = null;
    var main = LogicMainJava.of(io, new Verbosity(true, true, ProgressVerbosity.Full));
    var fullProgram = main.parse();
    main.wellFormednessFull(fullProgram);
    program = main.inference(fullProgram);
    main.wellFormednessCore(program);
    return program;
  }

  InputOutput replaceUriOnCurrentFile(InputOutput io, String uriAssist, String contentAssist) {
    var inputFiles = io.inputFiles().stream()
        .map(parser -> {
          if (parser.fileName().equals(Path.of(URI.create(uriAssist)))) {
            return new Parser(parser.fileName(), contentAssist);
          }
          return parser;
        })
        .toList();
    io = new FieldsInputOutput(io.entry(), io.commandLineArguments(), io.baseDir(), io.magicDir(),
        inputFiles, io.output(), io.cachedBase(), io.cachedFiles(), io.defaultAliases());
    return io;
  }
}