/* Copyright (c) 2006-2013 by Public Library of Science

 * http://plos.org
 * http://ambraproject.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ambraproject.util;

import org.ambraproject.views.CategoryView;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import static org.testng.Assert.assertEquals;

public class CategoryUtilsTest {
  private static Logger log = LoggerFactory.getLogger(CategoryUtilsTest.class);

  @DataProvider(name = "makeMap")
  public Object[][] createMap() {
    return new Object[][]{
      {
        new ArrayList() {{
          add("/f");
          add("/a/b/c");
          add("/a/b/c/d");
          add("/g");
          add("/a/c/e");
          add("/a/b/c/d/e");
          add("/e");
          add("/z");
          add("/1/2/3");
          add("/x/y");
        }},
        new CategoryView("ROOT") {{
          addChild(new CategoryView("a") {{
            addChild(new CategoryView("b") {{
              addChild(new CategoryView("c") {{
                addChild(new CategoryView("d") {{
                  addChild(new CategoryView("e"));
                }});
              }});
            }});
            addChild(new CategoryView("c") {{
              addChild(new CategoryView("e"));
            }});
          }});
          addChild(new CategoryView("e"));
          addChild(new CategoryView("g"));
          addChild(new CategoryView("f"));
          addChild(new CategoryView("z"));
          addChild(new CategoryView("1") {{
            addChild(new CategoryView("2") {{
              addChild(new CategoryView("3"));
            }});
          }});
          addChild(new CategoryView("x") {{
            addChild(new CategoryView("y"));
          }});
        }}
      }
    };
  }


  @DataProvider(name = "filterMap")
  public Object[][] filterMap() {
    return new Object[][]{
      {
        "a",
        new CategoryView("ROOT") {{
          addChild(new CategoryView("a"));
          addChild(new CategoryView("d") {{
            addChild(new CategoryView("e") {{
              addChild(new CategoryView("f") {{
                addChild(new CategoryView("a"));
              }});
            }});
          }});

          addChild(new CategoryView("d") {{
            addChild(new CategoryView("e") {{
              addChild(new CategoryView("f") {{
                addChild(new CategoryView("a"));
              }});
            }});
          }});

          addChild(new CategoryView("b") {{
            addChild(new CategoryView("a"));
          }});
        }},
        new CategoryView("ROOT") {{
          addChild(new CategoryView("a") {{
            addChild(new CategoryView("b") {{
              addChild(new CategoryView("c"));
            }});
            addChild(new CategoryView("c") {{
              addChild(new CategoryView("e"));
            }});
          }});
          addChild(new CategoryView("d") {{
            addChild(new CategoryView("e") {{
              addChild(new CategoryView("f") {{
                addChild(new CategoryView("a"));
              }});
            }});
          }});
          addChild(new CategoryView("g") {{
            addChild(new CategoryView("f"));
          }});
          addChild(new CategoryView("a") {{
            addChild(new CategoryView("x"));
          }});
          addChild(new CategoryView("b") {{
            addChild(new CategoryView("a"));
          }});
        }}
      }
    };
  }

  @Test(dataProvider = "makeMap")
  public void testCreateMap(List<String> before, CategoryView expected) {
    for(String string : before) {
      log.debug(string);
    }

    CategoryView result = CategoryUtils.createMapFromStringList(before);

    if(log.isDebugEnabled()) {
      log.debug("Result Map:");
      printMap(result, 0);
    }

    if(log.isDebugEnabled()) {
      log.debug("Expected Map:");
      printMap(expected, 0);
    }

    //Compare both ways to get around testNG bug
    assertEqualRecursive(result, expected);
    assertEqualRecursive(expected, result);
  }

  @Test(dataProvider = "filterMap")
  @SuppressWarnings("unchecked")
  public void testFilterMap(String filter, CategoryView expected, CategoryView source) {
    CategoryView result = CategoryUtils.filterMap(source, new String[]{filter});

    log.debug("Source");
    printMap(source, 0);
    log.debug("Result");
    printMap(result, 0);

    //Compare both ways to get around testNG bug
    assertEqualRecursive(result, expected);
    assertEqualRecursive(expected, result);
  }

  private void assertEqualRecursive(CategoryView result, CategoryView expected) {
    assertEquals(result.getName(), expected.getName());

    for(String key : result.getChildren().keySet()) {
      assertEqualRecursive(result.getChild(key), expected.getChild(key));
    }
  }

  private void printMap(CategoryView view, int depth) {
    String spacer = StringUtils.repeat("-", depth);

    for(String key : view.getChildren().keySet()) {
      log.debug("{}Key: {}, Size: {}", new Object[] { spacer, key, view.getChild(key).getChildren().size() });

      if(view.getChild(key).getChildren().size() > 0) {
        printMap(view.getChild(key), depth + 1);
      }
    }
  }
}
