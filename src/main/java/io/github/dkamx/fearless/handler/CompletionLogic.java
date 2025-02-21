package io.github.dkamx.fearless.handler;

import ast.E.Meth;
import ast.Program;
import ast.T.Dec;
import files.HasPos;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.lsp4j.Range;

public class CompletionLogic {

  public static String assistType(String content, Range range) {
    var start = range.getStart();
    var end = range.getEnd();
    var lines = content.split("\n");

    var before = new StringBuilder();
    for (int i = 0; i < start.getLine(); i++) {
      before.append(lines[i]).append("\n");
    }
    before.append(lines[start.getLine()], 0, start.getCharacter());

    var target = new StringBuilder();
    for (int i = start.getLine(); i <= end.getLine(); i++) {
      var line = lines[i];
      target.append(line,
          i == start.getLine() ? start.getCharacter() : 0,
          i == end.getLine() ? end.getCharacter() : line.length());
      if (i != end.getLine()) {
        target.append("\n");
      }
    }

    var after = new StringBuilder();
    after.append(lines[end.getLine()].substring(end.getCharacter())).append("\n");
    for (int i = end.getLine() + 1; i < lines.length; i++) {
      after.append(lines[i]).append("\n");
    }

    return before + "base.Id.id(" + target + ")" + after;
  }

  public static ProgramInfo collectProgramInfo(Program program) {
    var types = new HashMap<String, Set<String>>();
    var methods = new HashMap<String, List<Meth>>();
    program.ds().values().forEach(dec -> {
      var name = dec.name().name();
      var meths = dec.lambda().meths();
      var its = dec.lambda().its().stream()
          .map(it -> it.name().name())
          .collect(Collectors.toSet());
      types.put(name, its);
      var list = methods.getOrDefault(name, new ArrayList<>());
      // combine methods of type shares the same name
      list.addAll(meths);
      methods.put(name, list);
    });
    return new ProgramInfo(types, methods);
  }

  public static List<Dec> findDecs(Program program, String uri) {
    return program.ds().values().stream()
        .filter(dec -> isCurrentFile(dec, uri))
        .toList();
  }

  public static boolean isCurrentFile(HasPos dec, String uri) {
    if (dec.pos().isEmpty()) {
      return false;
    }
    var inputPath = Path.of(URI.create(uri));
    var decUri = dec.pos().get().fileName();
    var decPath = Path.of(decUri).toAbsolutePath();
    return decPath.equals(inputPath);
  }
}