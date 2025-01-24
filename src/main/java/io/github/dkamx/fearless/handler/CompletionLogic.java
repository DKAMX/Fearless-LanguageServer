package io.github.dkamx.fearless.handler;

import ast.E.Meth;
import ast.Program;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CompletionLogic {

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
}