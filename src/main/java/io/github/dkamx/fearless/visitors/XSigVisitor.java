package io.github.dkamx.fearless.visitors;

import ast.E.Lambda;
import id.Id.GX;
import java.util.HashMap;
import java.util.Map;

/**
 * Collect all Xs and its type in a source file. Ignore Xs that end with "$".
 */
public class XSigVisitor extends PropagatorVisitor<Map<String, String>> {

  private final Map<String, String> map = new HashMap<>();

  @Override
  public Void visitLambda(Lambda lambda) {
    var sig = lambda.meths().stream()
        .flatMap(meth -> meth.sig().ts().stream())
        .map(t -> t.rt().match(GX::name, it -> it.name().name()))
        .toList();
    var xs = lambda.meths().stream()
        .flatMap(meth -> meth.xs().stream())
        .toList();
    for (int i = 0; i < xs.size(); i++) {
      var name = xs.get(i);
      if (name.endsWith("$")) {
        continue;
      }
      map.put(xs.get(i), sig.get(i)); // name, type
    }
    return super.visitLambda(lambda);
  }

  @Override
  public Map<String, String> getResult() {
    return this.map;
  }
}