/**
 * Copyright (C) 2013 Sébastien Lesaint (http://www.javatronic.fr/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.javatronic.damapping.processor;

import org.testng.annotations.Test;

/**
 * DefaultPackageDedicatedClassTest - Tests generating classes from a dedicated class in the default package.
 *
 * @author Sébastien Lesaint
 */
public class DefaultPackageDedicatedClassTest extends AbstractCompilationTest {
  @Test
  public void compiling_mapper_in_default_package_is_successfull() throws Exception {
    assertThat("MostSimple",
        "import fr.javatronic.damapping.annotation.Mapper;",
        "",
        "@Mapper",
        "public class MostSimple {",
        "  public String map(Integer input) {",
        "    return null; // implementation does not matter",
        "  }",
        "}"
    ).compilesWithoutError();
  }

  @Test
  public void compiling_mapperFactory_in_default_package_is_successfull() throws Exception {
    assertThat("MostSimpleFactory",
        "import fr.javatronic.damapping.annotation.Mapper;",
        "import fr.javatronic.damapping.annotation.MapperFactory;",
        "",
        "@Mapper",
        "public class MostSimpleFactory {",
        "  @MapperFactory",
        "  public MostSimpleFactory(boolean a) {",
        "    // implementation does not matter",
        "  }",
        "",
        "  public String apply(Integer input) {",
        "    return null; // implementation does not matter",
        "  }",
        "}"
    ).compilesWithoutError();
  }

}
