package io.github.dkamx.fearless;

import static io.github.dkamx.fearless.TestHelper.createRange;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.dkamx.fearless.handler.GrammarTreeHelper;
import org.eclipse.lsp4j.Position;
import org.junit.jupiter.api.Test;

public class GrammarTreeTest {

  @Test
  public void testFindMinimalE() {
    var range = GrammarTreeHelper.findMinimalE(Sample.LIST, new Position(9, 27));
    assertEquals(createRange(9, 26, 9, 27), range);
  }

  @Test
  public void testFindMinimalE2() {
    var range = GrammarTreeHelper.findMinimalE(Sample.LIST, new Position(9, 28));
    assertEquals(createRange(9, 7, 9, 28), range);
  }

  @Test
  public void testFindMinimalE3() {
    var range = GrammarTreeHelper.findMinimalE(Sample.LIST, new Position(9, 29));
    assertEquals(createRange(6, 18, 9, 29), range);
  }

  @Test
  public void testFindMinimalE4() {
    var range = GrammarTreeHelper.findMinimalE(Sample.LIST2, new Position(9, 31));
    assertEquals(createRange(9, 26, 9, 31), range);
  }

  @Test
  public void testFindMinimalEWithin() {
    var range = GrammarTreeHelper.findMinimalEWithin(Sample.LIST2_CMPL, new Position(9, 33));
    assertEquals(createRange(9, 26, 9, 43), range);
  }
}