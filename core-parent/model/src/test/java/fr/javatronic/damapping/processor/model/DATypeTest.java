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
package fr.javatronic.damapping.processor.model;

import fr.javatronic.damapping.processor.model.factory.DANameFactory;

import java.util.List;
import com.google.common.collect.ImmutableList;

import com.beust.jcommander.internal.Lists;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DATypeTest -
 *
 * @author Sébastien Lesaint
 */
public class DATypeTest {
  @Test
  public void isArray() throws Exception {
    assertThat(DAType.arrayBuilder(DATypeKind.DECLARED, DANameFactory.from("simpleName")).build().isArray()).isTrue();
    assertThat(DAType.typeBuilder(DATypeKind.DECLARED, DANameFactory.from("simpleName")).build().isArray()).isFalse();
  }

  @Test(dataProvider = "builder_makes_equals_objects_DP")
  public void builder_makes_equals_objects(DAType.Builder builder) throws Exception {
    DAType obj1 = builder.build();
    DAType obj2 = builder.build();
    assertThat(obj1).isEqualTo(obj2);
    assertThat(obj1).isNotSameAs(obj2);
  }

  @Test
  public void verify_equals() throws Exception {
    List<DAType> daTypeList = Lists.newArrayList();
    for (Object[] objects : builder_makes_equals_objects_DP()) {
      daTypeList.add(((DAType.Builder) objects[0]).build());
    }

    DAType[] daTypes = daTypeList.toArray(new DAType[daTypeList.size()]);
    for (int i = 0 ; i < daTypes.length ; i++) {
      for (int j = 0 ; j < daTypes.length ; j++) {
        if (j == i) {
          continue;
        }
        assertThat(daTypes[i]).as("i=%s, j=%s", i, j).isNotEqualTo(daTypes[j]);
      }
    }
  }

  @DataProvider
  private Object[][] builder_makes_equals_objects_DP() {
    DAType.Builder toto = DAType.typeBuilder(DATypeKind.DECLARED, DANameFactory.from("toto"));
    DAType.Builder tutu = DAType.typeBuilder(DATypeKind.DECLARED, DANameFactory.from("tutu"));
    return new Object[][]{
        {
            toto
        },
        {
            tutu
        },
        {
            DAType.typeBuilder(DATypeKind.DECLARED, DANameFactory.from("toto"))
                  .withQualifiedName(DANameFactory.from("com.acme.toto"))
        },
        {
            DAType.typeBuilder(DATypeKind.DECLARED, DANameFactory.from("toto"))
                  .withTypeArgs(ImmutableList.of(toto.build()))
        },
        {
            DAType.typeBuilder(DATypeKind.DECLARED, DANameFactory.from("toto"))
                  .withTypeArgs(ImmutableList.of(toto.build(), tutu.build()))
        },
        {
            DAType.typeBuilder(DATypeKind.DECLARED, DANameFactory.from("toto"))
                  .withTypeArgs(ImmutableList.of(tutu.build(), toto.build()))
        },
        {
            DAType.typeBuilder(DATypeKind.DECLARED, DANameFactory.from("toto"))
                  .withExtendsBound(toto.build())
        },
        {
            DAType.typeBuilder(DATypeKind.DECLARED, DANameFactory.from("toto"))
                  .withSuperBound(toto.build())
        },
        {
            DAType.typeBuilder(DATypeKind.DECLARED, DANameFactory.from("toto"))
                  .withExtendsBound(tutu.build())
                  .withSuperBound(toto.build())
        },
        {
            DAType.typeBuilder(DATypeKind.DECLARED, DANameFactory.from("toto"))
                  .withTypeArgs(ImmutableList.of(tutu.build()))
                  .withExtendsBound(tutu.build())
                  .withSuperBound(toto.build())
        }
    };
  }

}