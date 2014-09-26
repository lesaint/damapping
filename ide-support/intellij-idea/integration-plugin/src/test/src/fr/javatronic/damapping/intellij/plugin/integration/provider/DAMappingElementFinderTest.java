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
package fr.javatronic.damapping.intellij.plugin.integration.provider;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * DAMappingElementFinderTest -
 *
 * @author Sébastien Lesaint
 */
public class DAMappingElementFinderTest {
  @Test
  public void qualifiedName_to_mapper_class_qualifiedName() throws Exception {
    String qualifiedName = "com.acme.toto.EnumTestMapper";
    String mapperInterfaceExtenstion = "Mapper";
    assertTrue(qualifiedName.endsWith(mapperInterfaceExtenstion));
    assertEquals(qualifiedName.substring(0, qualifiedName.length() - mapperInterfaceExtenstion.length()), "com.acme.toto.EnumTest");
  }
}
