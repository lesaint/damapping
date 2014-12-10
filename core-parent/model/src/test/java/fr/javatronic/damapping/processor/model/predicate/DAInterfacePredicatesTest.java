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
package fr.javatronic.damapping.processor.model.predicate;

import fr.javatronic.damapping.processor.model.DAInterface;
import fr.javatronic.damapping.processor.model.DAInterfaceImpl;
import fr.javatronic.damapping.processor.model.DATypeImpl;
import fr.javatronic.damapping.processor.model.DATypeKind;
import fr.javatronic.damapping.processor.model.factory.DANameFactory;
import fr.javatronic.damapping.processor.model.factory.DATypeFactory;

import com.google.common.base.Function;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DAInterfacePredicatesTest -
 *
 * @author Sébastien Lesaint
 */
public class DAInterfacePredicatesTest {
  @Test
  public void guavaFunction_supports_null() throws Exception {
    assertThat(DAInterfacePredicates.isGuavaFunction().apply(null)).isFalse();
  }

  @Test
  public void guavaFunction_fails_if_DAType_has_no_declared_name() throws Exception {
    DAInterface noDeclaredName = new DAInterfaceImpl(DATypeImpl.typeBuilder(DATypeKind.CHAR, DANameFactory.from("char")).build());
    assertThat(DAInterfacePredicates.isGuavaFunction().apply(noDeclaredName)).isFalse();
  }

  @Test
  public void guavaFunction_success_only_if_declaredname_is_guava_function() throws Exception {
    DAInterface daInterface = new DAInterfaceImpl(DATypeFactory.from(String.class));
    assertThat(DAInterfacePredicates.isGuavaFunction().apply(daInterface)).isFalse();

    DAInterface guavaFunction = new DAInterfaceImpl(DATypeFactory.declared("com.google.common.base.Function"));
    assertThat(DAInterfacePredicates.isGuavaFunction().apply(guavaFunction)).isTrue();

    DAInterface guavaFunctionFromClass = new DAInterfaceImpl(DATypeFactory.from(Function.class));
    assertThat(DAInterfacePredicates.isGuavaFunction().apply(guavaFunctionFromClass)).isTrue();
  }
}
