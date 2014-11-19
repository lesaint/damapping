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
package fr.javatronic.damapping.processor.sourcegenerator.imports;

import fr.javatronic.damapping.processor.model.factory.DANameFactory;
import fr.javatronic.damapping.processor.model.factory.DATypeFactory;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static fr.javatronic.damapping.processor.model.factory.DANameFactory.from;
import static fr.javatronic.damapping.processor.model.factory.DATypeFactory.declared;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * ImportListBuilderTest -
 *
 * @author Sébastien Lesaint
 */
public class ImportListBuilderTest {

  private ImportListBuilder underTest;

  @BeforeMethod
  public void setUp() throws Exception {
    underTest = new ImportListBuilder();

  }

  @Test(expectedExceptions = NullPointerException.class)
  public void getImports_throws_npe_for_null_currentPackage_argument() throws Exception {
    underTest.getImports(null);
  }

  @Test
  public void getImports_returns_empty_list_when_builder_is_empty() throws Exception {
    assertThat(new ImportListBuilder().getImports("com.acme")).isEmpty();
    assertThat(new ImportListBuilder().getImports("")).isEmpty();
    assertThat(new ImportListBuilder().getImports("acme")).isEmpty();
  }

  @Test
  public void getImports_does_not_enforce_currentPackage_argument_to_be_a_valid_package_name() throws Exception {
    underTest.getImports("Fo%o");
    // invalid character in package name => no error
  }

  @Test
  public void getImports_uses_specified_packagename_to_choose_the_homonym_NOT_in_the_current_package() throws Exception {
    underTest.addImports(declared("com.acme.Name"));
    underTest.addImports(declared("com.toto.Name"));
    underTest.addImports(declared("com.acme.Foo"));

    assertThat(underTest.getImports("com.acme")).extracting("qualifiedName").containsOnly(
        DANameFactory.from("com.toto.Name")
    );
    assertThat(underTest.getImports("com.toto")).extracting("qualifiedName").containsOnly(DANameFactory.from("com.acme.Name"), DANameFactory.from("com.acme.Foo"));
  }

  @Test
  public void getImports_choose_homonym_from_subpackage_when_class_exists_in_current_package() throws Exception {
    underTest.addImports(declared("com.acme.Name"));
    underTest.addImports(declared("com.acme.bar.Name"));

    assertThat(underTest.getImports("com.acme")).extracting("qualifiedName").containsOnly(from("com.acme.bar.Name"));
  }

  @Test
  public void getImports_choose_homonym_first_per_defaut_string_sort_when_class_exists_in_current_package() throws Exception {
    underTest.addImports(declared("com.acme.Name"));
    underTest.addImports(declared("com.acme.bar.Name"));
    underTest.addImports(declared("com.Name"));

    assertThat(underTest.getImports("com.acme")).extracting("qualifiedName").containsOnly(from("com.Name"));
  }

  @Test
  public void getImports_for_default_package_choose_homonym_first_from_defaut_string_sort_when_class_exists_in_current_package() throws Exception {
    underTest.addImports(declared("Name"));
    underTest.addImports(declared("com.Name"));

    assertThat(underTest.getImports("")).extracting("qualifiedName").containsOnly(from("com.Name"));
  }

  @Test
  public void getImports_never_returns_imports_from_java_lang() throws Exception {
    underTest.addImports(DATypeFactory.from(String.class));

    assertThat(underTest.getImports("")).isEmpty();
    assertThat(underTest.getImports("com.acme")).isEmpty();

  }
}
