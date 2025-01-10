package io.github.dkamx.fearless;

import generated.FearlessLexer;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.eclipse.lsp4j.SemanticTokenTypes;
import org.eclipse.lsp4j.SemanticTokens;
import org.eclipse.lsp4j.SemanticTokensLegend;
import org.eclipse.lsp4j.SemanticTokensWithRegistrationOptions;

public class SemanticTokensHandler {

  public static final SemanticTokensWithRegistrationOptions PROVIDER = new SemanticTokensWithRegistrationOptions();

  public static final List<String> TOKEN_TYPES = List.of(SemanticTokenTypes.Class,
      SemanticTokenTypes.Comment, SemanticTokenTypes.Keyword, SemanticTokenTypes.Method);

  static {
    var legend = new SemanticTokensLegend();
    legend.setTokenTypes(TOKEN_TYPES);
    legend.setTokenModifiers(List.of());
    PROVIDER.setFull(true);
    PROVIDER.setLegend(legend);
    PROVIDER.setRange(false);
  }

  /**
   * Handles the semantic tokens request
   */
  public static SemanticTokens handle(String uri) throws IOException {
    var filePath = Path.of(URI.create(uri));
    var fileContent = Files.readString(filePath).replace("\r", "");
    return handleContent(fileContent);
  }

  /**
   * Handles the semantic tokens request
   */
  public static SemanticTokens handleContent(String content) {
    var lexer = new FearlessLexer(CharStreams.fromString(content));
    var tokens = lexer.getAllTokens().stream()
        .filter(token -> {
          var type = getTokenTypeByName(lexer.getVocabulary().getSymbolicName(token.getType()));
          return SemanticTokenTypes.Keyword.equals(type)
              || SemanticTokenTypes.Comment.equals(type);
        }).toList();
    return encodeTokens(tokens, lexer);
  }

  /**
   * Encodes the tokens into semantic tokens
   */
  public static SemanticTokens encodeTokens(List<? extends Token> tokens, FearlessLexer lexer) {
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
    return new SemanticTokens(data);
  }

  /**
   * Maps the ANTLR token type to the LSP token type
   */
  public static String getTokenTypeByName(String symbolicName) {
    return switch (symbolicName) {
      case "FullCM" -> SemanticTokenTypes.Class;
      case "BlockComment", "LineComment" -> SemanticTokenTypes.Comment;
      case "Mut", "ReadH", "MutH", "ReadImm", "Read", "Iso", "Imm", "Alias", "As", "Pack" ->
          SemanticTokenTypes.Keyword;
      case "MName" -> SemanticTokenTypes.Method;
      default -> null;
    };
  }
}