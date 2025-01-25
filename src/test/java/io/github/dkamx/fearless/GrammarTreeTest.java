package io.github.dkamx.fearless;

import static io.github.dkamx.fearless.TestHelper.createRange;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.dkamx.fearless.handler.GrammarTreeHelper;
import org.eclipse.lsp4j.Position;
import org.junit.jupiter.api.Test;

public class GrammarTreeTest {

  @Test
  public void testGrammarTree() {
    var parser = GrammarTreeHelper.createParser(Sample.LIST);
    var range = GrammarTreeHelper.findMinimalE(parser, new Position(9, 27));
    assertEquals(createRange(9, 26, 9, 27), range);
  }

  @Test
  public void testGrammarTree2() {
    var parser = GrammarTreeHelper.createParser(Sample.LIST);
    var range = GrammarTreeHelper.findMinimalE(parser, new Position(9, 28));
    assertEquals(createRange(9, 7, 9, 28), range);
  }

  @Test
  public void testGrammarTree3() {
    var parser = GrammarTreeHelper.createParser(Sample.LIST);
    var range = GrammarTreeHelper.findMinimalE(parser, new Position(9, 29));
    assertEquals(createRange(6, 18, 9, 29), range);
  }
}