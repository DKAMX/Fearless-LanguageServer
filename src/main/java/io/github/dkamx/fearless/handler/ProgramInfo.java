package io.github.dkamx.fearless.handler;

import ast.E.Meth;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Type inheritance and method information for the program.
 */
public record ProgramInfo(Map<String, Set<String>> types, Map<String, List<Meth>> methods) {

  public Set<String> getMethods(String type) {
    var set = methods.get(type).stream()
        .map(meth -> meth.name().name())
        .collect(Collectors.toCollection(HashSet::new));
    types.get(type).stream()
        .filter(inheritedType -> !type.equals(inheritedType))
        .forEach(inheritedType -> set.addAll(getMethods(inheritedType)));
    return set;
  }
}