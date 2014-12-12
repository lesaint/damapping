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
package fr.javatronic.damapping.test.mappermethod;

import fr.javatronic.damapping.test.AbstractMapperTest;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UsePackageTypeInjectableTest -
 *
 * @author Sébastien Lesaint
 */
public class EnumInjectableTest extends AbstractMapperTest {
  public EnumInjectableTest() {
    super(EnumInjectable.class);
  }

  @Test
  public void check_generated_mapper_file() throws Exception {
    testUtil.checkGeneratedFile(getClass(), "Mapper");
  }

  @Test
  public void check_generated_mapperImpl_file() throws Exception {
    testUtil.checkGeneratedFile(getClass(), "MapperImpl");
  }

  @Test
  public void check_generated_mapperFactory_file() throws Exception {
    String tgtFilename = testUtil.buildTargetFilename("MapperFactory");

    assertThat(getClass().getResource(tgtFilename)).isNull();
  }

}
