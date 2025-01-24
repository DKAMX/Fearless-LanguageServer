package io.github.dkamx.fearless.handler;

import generated.FearlessLexer;
import io.github.dkamx.fearless.FearlessLanguageServer;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.eclipse.lsp4j.SemanticTokenTypes;
import org.eclipse.lsp4j.SemanticTokens;
import org.eclipse.lsp4j.SemanticTokensLegend;
import org.eclipse.lsp4j.SemanticTokensWithRegistrationOptions;
import parser.Parser;

public class SemanticTokensHandler {

  public static final SemanticTokensWithRegistrationOptions PROVIDER = new SemanticTokensWithRegistrationOptions();
  public static final List<String> TOKEN_TYPES = List.of(SemanticTokenTypes.Class,
      SemanticTokenTypes.Comment,
      SemanticTokenTypes.Keyword,
      SemanticTokenTypes.Method,
      SemanticTokenTypes.Variable);
  private static final Logger LOGGER = Logger.getLogger(SemanticTokensHandler.class.getName());

  static {
    var legend = new SemanticTokensLegend();
    legend.setTokenTypes(TOKEN_TYPES);
    legend.setTokenModifiers(List.of());
    PROVIDER.setFull(true);
    PROVIDER.setLegend(legend);
    PROVIDER.setRange(false);
  }

  private final FearlessLanguageServer server;

  public SemanticTokensHandler(FearlessLanguageServer server) {
    this.server = server;
  }

  /**
   * Encodes the tokens into data follows semantic token format
   */
  public static List<Integer> encodeTokens(FearlessLexer lexer, List<? extends Token> tokens) {
    var data = new ArrayList<Integer>();
    var prevLine = 0;
    var prevChar = 0;
    for (var token : tokens) {
      var line = token.getLine() - 1;
      var startChar = token.getCharPositionInLine();
      var type = getTokenTypeByName(lexer.getVocabulary().getSymbolicName(token.getType()));
      var parts = token.getText().split("\n"); // Handle multiline comments
      for (var i = 0; i < parts.length; i++, line++) {
        var text = parts[i];
        var deltaLine = line - prevLine;
        var deltaStartChar = deltaLine == 0 ? startChar - prevChar : startChar;
        data.add(deltaLine);
        data.add(deltaStartChar);
        data.add(text.length());
        data.add(TOKEN_TYPES.indexOf(type));
        data.add(0); // tokenModifiers
        prevLine = line;
        prevChar = startChar;
        startChar = 0; // Only the first line has a startChar
      }
    }
    return data;
  }

  /**
   * Maps the ANTLR token type to the LSP token type
   */
  public static String getTokenTypeByName(String symbolicName) {
    return switch (symbolicName) {
      case "FullCN" -> SemanticTokenTypes.Class;
      case "BlockComment", "LineComment" -> SemanticTokenTypes.Comment;
      case "Mut", "ReadH", "MutH", "ReadImm", "Read", "Iso", "Imm", "Alias", "As", "Pack" ->
          SemanticTokenTypes.Keyword;
//      case "MName" -> SemanticTokenTypes.Method;
//      case "X" -> SemanticTokenTypes.Variable;
      default -> null;
    };
  }

  public SemanticTokens handle(String uri) {
    LOGGER.info("handle");
    server.infoMessage("handle");
    return this.handleContent(Parser.of(Path.of(URI.create(uri))).content());
  }

  SemanticTokens handleContent(String content) {
    var lexer = new FearlessLexer(CharStreams.fromString(content));
    var tokens = lexer.getAllTokens().stream()
        .filter(token -> {
          var type = getTokenTypeByName(lexer.getVocabulary().getSymbolicName(token.getType()));
          if (type == null) {
            return false;
          }
          return TOKEN_TYPES.contains(type);
        })
        .toList();
    return new SemanticTokens(encodeTokens(lexer, tokens));
  }
}