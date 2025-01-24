package io.github.dkamx.fearless.handler;

import io.github.dkamx.fearless.FearlessLanguageServer;
import java.util.ArrayList;
import java.util.List;
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
    var program = WorkspaceCacheStore.getProgram(uri);
    if (program == null) {
      program = server.getCompilationHandler().build(uri);
    }
    var list = program.ds().values().stream()
        .map(dec -> dec.name().name())
        .distinct()
        .toList();
    // TODO cut full path with alias
    return createItemList(list, CompletionItemKind.Class);
  }

  public List<CompletionItem> handle(String uri, Position position) {
    LOGGER.info("handle\n%s %s".formatted(uri, position));
    server.infoMessage("handle\n%s %s".formatted(uri, position));
    var list = new ArrayList<CompletionItem>(completeAliasKeyword());
    list.addAll(completeKeyword());
    list.addAll(completeType(uri));
    return list;
  }
}