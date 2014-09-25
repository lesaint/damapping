/*
 * Copyright 2013 Sébastien Lesaint
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
package fr.javatronic.damapping.processor.sourcegenerator.imports;

import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.DATypeKind;
import fr.javatronic.damapping.processor.model.factory.DANameFactory;

import java.util.Collections;
import java.util.List;
import com.google.common.collect.ImmutableList;

import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

/**
 * DATypeImportComputerTest -
 *
 * @author Sébastien Lesaint
 */
public class DATypeImportComputerTest {

  @Test
  public void getImports_no_typeArgs() throws Exception {
    DAType daType = daType("test.Toto");
    Assertions.assertThat(DATypeImportComputer.computeImports(daType)).extracting("name").containsOnly("test.Toto");
  }

  @Test
  public void getImports_from_default_package_no_typeArgs() throws Exception {
    DAType daType = daType("Toto");
    Assertions.assertThat(DATypeImportComputer.computeImports(daType)).extracting("name").isEmpty();
  }

  @Test
  public void getImports_one_typeArg() throws Exception {
    DAType daType = daType(
        "test.Toto",
        ImmutableList.of(daType("test.Titi"))
    );
    Assertions.assertThat(DATypeImportComputer.computeImports(daType)).extracting("name").containsOnly("test.Toto", "test.Titi");
  }

  @Test
  public void getImports_one_typeArg_from_default_package() throws Exception {
    DAType daType = daType(
        "test.Toto",
        ImmutableList.of(daType("Titi"))
    );
    Assertions.assertThat(DATypeImportComputer.computeImports(daType)).extracting("name").containsOnly("test.Toto");
  }

  @Test
  public void getImports_multiple_typeArg() throws Exception {
    DAType daType = daType(
        "test.Toto",
        ImmutableList.of(daType("test.Titi"), daType("test.Tutu"))
    );
    Assertions.assertThat(DATypeImportComputer.computeImports(daType))
              .extracting("name")
              .containsOnly("test.Toto", "test.Titi", "test.Tutu");
  }

  @Test
  public void getImports_one_typeArg_recursive() throws Exception {
    DAType daType = daType(
        "test.Toto",
        ImmutableList.of(
            daType("test.Titi", ImmutableList.of(daType("test.Tutu")))
        )
    );
    Assertions.assertThat(DATypeImportComputer.computeImports(daType))
              .extracting("name")
              .containsOnly("test.Toto", "test.Titi", "test.Tutu");
  }

  @Test
  public void testGetImports() throws Exception {
    DAType daType = daType("test.Toto");
    Assertions.assertThat(DATypeImportComputer.computeImports(daType)).extracting("name").containsOnly("test.Toto");

    daType = daType("test.Toto", ImmutableList.of(daType("test.Titi")));
    Assertions.assertThat(DATypeImportComputer.computeImports(daType)).extracting("name").containsOnly("test.Toto", "test.Titi");
  }

  private static DAType daType(String name) {
    return daType(name, Collections.<DAType>emptyList());
  }

  private static DAType daType(String qualifiedName, List<DAType> typeArgs) {
    DAName daName = DANameFactory.from(qualifiedName);
    return DAType.builder(DATypeKind.DECLARED, DANameFactory.simpleFromQualified(daName))
                 .withQualifiedName(daName)
                 .withTypeArgs(typeArgs)
                 .build();
  }
}
