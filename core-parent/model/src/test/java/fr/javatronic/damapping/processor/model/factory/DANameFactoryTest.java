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
package fr.javatronic.damapping.processor.model.factory;

import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.DATypeKind;

import java.util.Locale;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DANameFactoryTest -
 *
 * @author Sébastien Lesaint
 */
public class DANameFactoryTest {

  private static final String TOTO = "toto";

  @Test
  public void from_string() throws Exception {
    assertThat(DANameFactory.from(TOTO).getName()).isEqualTo(TOTO);
  }

  @Test
  public void fromPrimitiveKind() throws Exception {
    for (DATypeKind typeKind : DATypeKind.values()) {
      if (typeKind.isPrimitive()) {
        DAName daName = DANameFactory.fromPrimitiveKind(typeKind);
        assertThat(daName.getName()).isEqualTo(typeKind.name().toLowerCase(Locale.US));
      }
    }
  }

  @Test
  public void simpleFromQualified() throws Exception {
    assertThat(DANameFactory.simpleFromQualified(DANameFactory.from(TOTO)).getName()).isEqualTo(TOTO);
    assertThat(DANameFactory.simpleFromQualified(DANameFactory.from("a." + TOTO)).getName()).isEqualTo(TOTO);
    assertThat(DANameFactory.simpleFromQualified(DANameFactory.from("d.b.a." + TOTO)).getName()).isEqualTo(TOTO);
  }

  @Test
  public void wildcard() throws Exception {
    assertThat(DANameFactory.wildcard().getName()).isEqualTo("?");
  }

  @Test
  public void voidName() throws Exception {
    assertThat(DANameFactory.voidDAName().getName()).isEqualTo("void");
  }
}
