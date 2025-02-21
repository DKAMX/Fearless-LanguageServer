package io.github.dkamx.fearless.visitor.ast;

import ast.E.Lambda;
import ast.E.MCall;
import ast.E.X;
import visitors.Visitor;

/**
 * A propagator visitor that helps visit all the sub expressions.
 */
public class PropagatorVisitor<T> implements Visitor<Void> {

  @Override
  public Void visitMCall(MCall mCall) {
    mCall.es().forEach(ei -> ei.accept(this));
    mCall.receiver().accept(this);
    return null;
  }

  @Override
  public Void visitX(X x) {
    return null;
  }

  @Override
  public Void visitLambda(Lambda lambda) {
    lambda.meths().forEach(meth -> meth.body().ifPresent(body -> body.accept(this)));
    return null;
  }

  /**
   * Overridden visit methods can compute a result and retrieve it using this method.
   *
   * @return the result of the visitor
   */
  public T getResult() {
    return null;
  }
}