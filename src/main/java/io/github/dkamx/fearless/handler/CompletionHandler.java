package io.github.dkamx.fearless.handler;

import io.github.dkamx.fearless.FearlessLanguageServer;
import io.github.dkamx.fearless.visitor.ast.IdTypeVisitor;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.CompletionOptions;
import org.eclipse.lsp4j.Position;

public class CompletionHandler {

  public static final CompletionOptions PROVIDER = new CompletionOptions(false, List.of("."));
  private static final Logger LOGGER = Logger.getLogger(CompletionHandler.class.getName());
  private final FearlessLanguageServer server;

  public CompletionHandler(FearlessLanguageServer server) {
    this.server = server;
  }

  public static List<CompletionItem> createItemList(List<String> keywords,
      CompletionItemKind kind) {
    return keywords.stream()
        .map(keyword -> {
          var item = new CompletionItem(keyword);
          item.setKind(kind);
          return item;
        })
        .toList();
  }

  List<CompletionItem> completeAliasKeyword() {
    return createItemList(List.of("alias", "as"), CompletionItemKind.Keyword);
  }

  List<CompletionItem> completeKeyword() {
    return createItemList(List.of("mut", "mutH", "read", "read/imm", "readH", "iso", "imm"),
        CompletionItemKind.Keyword);
  }

  List<CompletionItem> completeType(String uri) {
    // TODO cut full path with alias
    var list = WorkspaceCacheStore.getProgramInfo(uri).types().keySet().stream().toList();
    return createItemList(list, CompletionItemKind.Class);
  }

  public List<CompletionItem> completeMethod(String uri, Position position) {
    var list = new ArrayList<CompletionItem>();
    var content = WorkspaceCacheStore.getFileContent(uri);
    // wrap selected code with helper method (base.Id.id)
    var range = GrammarTreeHelper.findMinimalE(content, position);
    if (range == null) {
      return list;
    }
    content = CompletionLogic.assistType(content, range);
    var program = server.getCompilationHandler().build(uri, content);
    if (program == null) {
      return list;
    }
    // resolve type information
    var decs = CompletionLogic.findDecs(program, uri);
    var visitor = new IdTypeVisitor();
    decs.forEach(dec -> dec.lambda().accept(visitor));
    var idTypes = visitor.getResult();
    idTypes.values().stream().findFirst().ifPresent(
        type -> {
          WorkspaceCacheStore.getProgramInfo(uri).getMethods(type).forEach(
              method -> {
                var item = new CompletionItem(method);
                item.setKind(CompletionItemKind.Method);
                list.add(item);
              }
          );
        }
    );
    return list;
  }

  public List<CompletionItem> handle(String uri, Position position, boolean isTriggerCharacter) {
    LOGGER.info("handle\n%s %s".formatted(uri, position));
    server.infoMessage("handle\n%s %s".formatted(uri, position));
    var list = new ArrayList<CompletionItem>(completeAliasKeyword());
    list.addAll(completeKeyword());
    if (!WorkspaceCacheStore.hasProgram(uri)) {
      server.getCompilationHandler().build(uri);
    }
    if (isTriggerCharacter) {
      // type assist for variable
      return completeMethod(uri, position);
    }
    list.addAll(completeType(uri));
    return list;
  }
}