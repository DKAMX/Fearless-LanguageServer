package io.github.dkamx.fearless.visitor.ast;

import ast.E.Lambda;
import ast.E.MCall;
import files.Pos;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.lsp4j.Position;

/**
 * Collect all parameter type of method base.Id.id
 */
public class IdTypeVisitor extends PropagatorVisitor<Map<Position, String>> {

  private Map<Position, String> result = new HashMap<>();

  public static Position toPosition(Pos pos) {
    return new Position(pos.line() - 1, pos.column());
  }

  @Override
  public Void visitMCall(MCall mCall) {
    if (mCall.pos().isEmpty()) {
      return super.visitMCall(mCall);
    }
    if (mCall.receiver() instanceof Lambda lambda) {
      var typeName = lambda.its().getFirst().name().name();
      var methodName = mCall.name().name();
      if (!typeName.equals("base.Id") && !methodName.equals(".id")) {
        return super.visitMCall(mCall);
      }
      var type = mCall.ts().getFirst().rt().match(_ -> null, it -> it.name().name());
      if (type != null) {
        result.put(toPosition(mCall.pos().get()), type);
      }
    }
    return super.visitMCall(mCall);
  }

  @Override
  public Map<Position, String> getResult() {
    return result;
  }
}