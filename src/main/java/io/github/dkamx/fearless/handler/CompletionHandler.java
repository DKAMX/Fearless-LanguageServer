package io.github.dkamx.fearless.handler;

import io.github.dkamx.fearless.FearlessLanguageServer;
import io.github.dkamx.fearless.WorkspaceFileStore;
import io.github.dkamx.fearless.handler.CodeNode.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.CompletionOptions;

public class CompletionHandler {

  public static final CompletionOptions PROVIDER = new CompletionOptions(false, List.of("."));

  FearlessLanguageServer server;

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

  static List<CompletionItem> completeAliasKeyword() {
    return createItemList(List.of("alias", "as"), CompletionItemKind.Keyword);
  }

  static List<CompletionItem> completeKeyword() {
    return createItemList(List.of("mut", "mutH", "read", "read/imm", "readH", "iso", "imm"),
        CompletionItemKind.Keyword);
  }

  static List<CompletionItem> completeMethod(String uri) {
    var mapMethod = CompletionLogic.collectMethod(WorkspaceFileStore.programCache, uri);
    var mapXSig = CompletionLogic.collectXSig(WorkspaceFileStore.programCache, uri);
    var method = mapMethod.entrySet().stream()
        .flatMap(entry -> entry.getValue().stream()
            .map(methName -> entry.getKey() + methName));
    var xMethod = mapXSig.entrySet().stream()
        .flatMap(entry -> mapMethod.get(entry.getValue()).stream()
            .map(meth -> entry.getKey() + meth));
    var list = Stream.concat(method, xMethod).toList();

    return createItemList(list, CompletionItemKind.Method);
  }

  static List<CompletionItem> completeType(String uri) {
    var list = WorkspaceFileStore.programCache.ds().values().stream()
        .map(dec -> dec.name().name())
        .distinct()
        .toList();

    return createItemList(list, CompletionItemKind.Class);
  }

  static List<CompletionItem> completeVariable(String uri, int line, int character) {
    return createItemList(List.of(), CompletionItemKind.Variable);
  }

  public List<CompletionItem> handle(String uri, int line, int character) {
    var list = new ArrayList<>(completeAliasKeyword());

    var content = WorkspaceFileStore.sourceFiles.get(uri);
    var rootNode = CodeNode.createNode(content);
    var node = rootNode.findNode(line, character, Type.X);

    if (node instanceof CodeNodeX xNode) {
      var xName = xNode.content;
      var assistContent = CompletionLogic.assistType(content, xNode);
      var program = this.server.compilerLogic.build(uri, assistContent);
      var mapXType = CompletionLogic.collectXType(program, uri);
      var mapMethod = CompletionLogic.collectMethod(WorkspaceFileStore.programCache, uri);
      var xMethod = mapMethod.get(mapXType.get(xName));
      list.addAll(createItemList(xMethod.stream().toList(), CompletionItemKind.Method));
    }

//    list.addAll(completeKeyword());
//    list.addAll(completeType(uri));
//    list.addAll(completeMethod(uri));

    return list;
  }
}