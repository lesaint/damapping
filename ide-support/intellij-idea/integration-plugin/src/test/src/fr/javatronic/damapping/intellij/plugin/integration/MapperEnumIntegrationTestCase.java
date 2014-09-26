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
package fr.javatronic.damapping.intellij.plugin.integration;

/**
 * MapperEnumIntegrationTestCase - Unit test for @Mapper classes in integration-test module "mapper-enum"
 *
 * @author Sébastien Lesaint
 */
public class MapperEnumIntegrationTestCase extends AbstractIntegrationTestCase {
  public MapperEnumIntegrationTestCase() {
    super("mapper-enum");
  }

  public void testEnumInstancedGuavaFunction() throws Exception {
    doAugmentTest();
  }

//  public void testEnumInstancedGuavaFunction_resolve() throws Exception {
//    doResolveTest("EnumInstancedGuavaFunction");
//  }
}
