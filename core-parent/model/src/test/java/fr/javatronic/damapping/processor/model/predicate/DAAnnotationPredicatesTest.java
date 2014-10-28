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

import fr.javatronic.damapping.annotation.Mapper;
import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.factory.DATypeFactory;

import org.testng.annotations.Test;

import static fr.javatronic.damapping.processor.model.predicate.DAAnnotationPredicates.isMapper;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * DAAnnotationPredicatesTest -
 *
 * @author Sébastien Lesaint
 */
public class DAAnnotationPredicatesTest {

  @Test
  public void testIsMapper() throws Exception {
    assertThat(isMapper().apply(null)).isFalse();
    assertThat(isMapper().apply(new DAAnnotation(DATypeFactory.declared("com.toto.Mapper")))).isFalse();
    assertThat(isMapper().apply(new DAAnnotation(DATypeFactory.declared("org.acme.zoom.Foo")))).isFalse();
    assertThat(isMapper().apply(new DAAnnotation(DATypeFactory.declared("Bar")))).isFalse();
    assertThat(isMapper().apply(new DAAnnotation(DATypeFactory.declared(Mapper.class.getName())))).isTrue();
  }

}
