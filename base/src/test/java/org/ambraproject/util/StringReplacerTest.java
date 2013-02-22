package org.ambraproject.util;

import org.testng.annotations.Test;

import java.util.regex.Pattern;

import static org.testng.Assert.assertEquals;

public class StringReplacerTest {

  @Test
  public void testStringReplacer() {
    assertEquals(StringReplacer.builder().build().replace(""), "");
    assertEquals(StringReplacer.builder().build().replace("foo"), "foo");
    assertEquals(StringReplacer.builder().deleteExact("a").build().replace("bab"), "bb");
    assertEquals(StringReplacer.builder().deleteExact("a").build().replace("abab"), "bb");
    assertEquals(StringReplacer.builder().deleteRegex("a+").build().replace("babaab"), "bbb");
    assertEquals(StringReplacer.builder().replaceExact("a", "bb").build().replace("aca"), "bbcbb");
    assertEquals(StringReplacer.builder().replaceRegex("a+", "bb").build().replace("aaca"), "bbcbb");
    assertEquals(StringReplacer.builder().replaceRegex(Pattern.compile("a+", Pattern.CASE_INSENSITIVE), "bb")
        .build().replace("aAcAacA"), "bbcbbcbb");
    assertEquals(StringReplacer.builder().deleteRegex(Pattern.compile("a+", Pattern.CASE_INSENSITIVE))
        .build().replace("aAcAacA"), "cc");

    // Cases where sequential application matters
    assertEquals(StringReplacer.builder().replaceExact("a", "b").replaceExact("b", "c").build().replace("aaa"), "ccc");
    assertEquals(StringReplacer.builder().replaceExact("a", "b").replaceExact("b", "c").build().replace("aaba"), "cccc");
    assertEquals(StringReplacer.builder().replaceRegex("a+", "b").replaceRegex("bb+", "c").build().replace("aba"), "c");
    assertEquals(StringReplacer.builder().replaceRegex("bb+", "c").replaceRegex("a+", "b").build().replace("aba"), "bbb");
    assertEquals(StringReplacer.builder().deleteExact("a").replaceRegex("b+", "c").build().replace("bab"), "c");
    assertEquals(StringReplacer.builder().replaceRegex("b+", "c").deleteExact("a").build().replace("bab"), "cc");
    assertEquals(StringReplacer.builder().deleteRegex("aa|bb").build().replace("abba"), "aa");
    assertEquals(StringReplacer.builder().deleteRegex("aa|bb").deleteRegex("aa|bb").build().replace("abba"), "");
  }

}
