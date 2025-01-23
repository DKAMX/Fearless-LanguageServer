package io.github.dkamx.fearless.handler;

import ast.E;
import ast.E.Meth;
import ast.E.X;
import ast.Program;
import ast.T.Dec;
import files.HasPos;
import id.Id.DecId;
import io.github.dkamx.fearless.visitors.PropagatorVisitor;
import io.github.dkamx.fearless.visitors.XSigVisitor;
import io.github.dkamx.fearless.visitors.XTypeVisitor;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Low level completion logic.
 */
public class CompletionLogic {

  /**
   * Collect all types and its methods in a source file.
   */
  public static Map<String, Set<String>> collectMethod(Program program, String uri) {
    assert !program.ds().isEmpty();
    return program.ds().values().stream()
        .filter(dec -> !dec.lambda().meths().isEmpty())
        .collect(Collectors.toMap(
            dec -> dec.name().name(),
//            dec -> dec.lambda().meths().stream()
//                .map(meth -> meth.name().name())
//                .collect(Collectors.toSet()),
            dec -> {
              var list = new ArrayList<Meth>();
              collectMethod(program, dec.name(), list);
              return list.stream()
                  .map(meth -> meth.name().name())
                  .collect(Collectors.toSet());
            },
            (a, b) -> Stream.concat(a.stream(), b.stream()).collect(Collectors.toSet())
        ));
  }

  static void collectMethod(Program program, DecId decId, ArrayList<Meth> list) {
    var lambda = program.ds().get(decId).lambda();
    list.addAll(lambda.meths());
    lambda.its().forEach(it -> collectMethod(program, it.name(), list));
  }

  /**
   * Collect all Xs appearances in a source file.
   */
  public static List<E> collectX(Program program, String uri) {
    var list = new ArrayList<E>();
    assert !program.ds().isEmpty();
    var decs = findDecs(program, uri);
    decs.forEach(dec -> dec.lambda().accept(new PropagatorVisitor<Void>() {
      @Override
      public Void visitX(X x) {
        if (x.pos().isPresent()) {
          list.add(x);
        }
        return null;
      }
    }));
    return list;
  }

  /**
   * Collect all Xs and its type in a source file.
   */
  public static Map<String, String> collectXSig(Program program, String uri) {
    assert !program.ds().isEmpty();
    var decs = findDecs(program, uri);
    var visitor = new XSigVisitor();
    decs.forEach(dec -> dec.lambda().accept(visitor));
    return visitor.getResult();
  }

  public static Map<String, String> collectXType(Program program, String uri) {
    assert !program.ds().isEmpty();
    var decs = findDecs(program, uri);
    var visitor = new XTypeVisitor();
    decs.forEach(dec -> dec.lambda().accept(visitor));
    return visitor.getResult();
  }

  public static List<Dec> findDecs(Program program, String uri) {
    return program.ds().values().stream()
        .filter(dec -> isCurrentFile(dec, uri))
        .toList();
  }

  public static String assistType(String content, CodeNode node) {
//    if (!XNode.type.equals(Type.X)) {
//      return content;
//    }

    var start = node.start;
    var end = node.end;
    var lines = content.split("\n");
    var before = new StringBuilder();
    for (int i = 0; i < start.getLine(); i++) {
      before.append(lines[i]).append("\n");
    }
    before.append(lines[start.getLine()], 0, start.getCharacter());
    var after = new StringBuilder();
    after.append(lines[end.getLine()].substring(end.getCharacter())).append("\n");
    for (int i = end.getLine() + 1; i < lines.length; i++) {
      after.append(lines[i]).append("\n");
    }

    return before + "base.Id.id(" + node.content + ")" + after;
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