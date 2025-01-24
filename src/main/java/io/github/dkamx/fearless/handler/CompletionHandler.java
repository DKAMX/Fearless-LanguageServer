package io.github.dkamx.fearless.handler;

import io.github.dkamx.fearless.FearlessLanguageServer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.CompletionOptions;
import org.eclipse.lsp4j.Position;

public class CompletionHandler {

  public static final CompletionOptions PROVIDER = new CompletionOptions(false, List.of());
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

  List<CompletionItem> completeMethod(String uri) {
    var info = WorkspaceCacheStore.getProgramInfo(uri);
    var list = info.types().keySet().stream().toList().stream()
        .map(info::getMethods)
        .flatMap(Set::stream)
        .toList();
    return createItemList(list, CompletionItemKind.Method);
  }

  public List<CompletionItem> handle(String uri, Position position) {
    LOGGER.info("handle\n%s %s".formatted(uri, position));
    server.infoMessage("handle\n%s %s".formatted(uri, position));
    var list = new ArrayList<CompletionItem>(completeAliasKeyword());
    list.addAll(completeKeyword());
    if (!WorkspaceCacheStore.hasProgram(uri)) {
      server.getCompilationHandler().build(uri);
    }
    list.addAll(completeType(uri));
    return list;
  }
}