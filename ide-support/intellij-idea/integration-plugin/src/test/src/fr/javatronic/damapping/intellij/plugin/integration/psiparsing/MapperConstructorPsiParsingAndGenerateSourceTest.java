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
package fr.javatronic.damapping.intellij.plugin.integration.psiparsing;

/**
 * MapperConstructorPsiParsingAndGenerateSourceTest - Psi Parsing, validation and source generation unit test for
 * integration-test module : mapper-constructor
 *
 * @author Sébastien Lesaint
 */
public class MapperConstructorPsiParsingAndGenerateSourceTest extends AbstractPsiParsingAndGenerateSourceTest {
  public MapperConstructorPsiParsingAndGenerateSourceTest() {
    super("mapper-constructor");
  }

  public void testConstructorInstancedGuavaFunction() throws Exception {
    doPsiParsingAndGenerateSourceTest();
  }

  public void testNonPublicMethods() throws Exception {
    doPsiParsingAndGenerateSourceTest();
  }

  public void testWildcardGenerics() throws Exception {
    doPsiParsingAndGenerateSourceTest();
  }

  public void testHandleCustomTypes() throws Exception {
    doPsiParsingAndGenerateSourceTest();
  }
}
