package io.github.dkamx.fearless.visitors;

import ast.E.MCall;
import ast.E.X;
import id.Id.GX;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class XTypeVisitor extends PropagatorVisitor<Map<String, String>> {

  private final Map<String, String> map = new HashMap<>();

  @Override
  public Void visitMCall(MCall mCall) {
    var es = mCall.es();
    var ts = mCall.ts();
    if (es.size() == ts.size()) {
      var names = IntStream.range(0, es.size())
          .filter(i -> es.get(i) instanceof X)
          .mapToObj(i -> ((X) es.get(i)).name())
          .toList();
      var types = IntStream.range(0, es.size())
          .mapToObj(i -> ts.get(i).rt().match(GX::name, it -> it.name().name()))
          .toList();
      for (int i = 0; i < names.size(); i++) {
        var name = names.get(i);
        if (name.endsWith("$")) {
          continue;
        }
        map.put(name, types.get(i));
      }
    }

    return super.visitMCall(mCall);
  }

  @Override
  public Map<String, String> getResult() {
    return this.map;
  }
}