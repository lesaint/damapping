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
package fr.phan.damapping.processor.impl.imports;

import fr.phan.damapping.processor.model.DAName;
import fr.phan.damapping.processor.model.DAType;
import fr.phan.damapping.processor.model.factory.DANameFactory;

import java.util.Collections;
import java.util.List;
import javax.lang.model.type.TypeKind;
import com.google.common.collect.ImmutableList;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DATypeImportComputerTest -
 *
 * @author Sébastien Lesaint
 */
public class DATypeImportComputerTest {

    @Test
    public void getImports_no_typeArgs() throws Exception {
        DAType daType = daType("toto");
        assertThat(DATypeImportComputer.computeImports(daType)).extracting("name").containsOnly("toto");
    }

    @Test
    public void getImports_one_typeArg() throws Exception {
        DAType daType = daType(
                "toto",
                ImmutableList.of(daType("titi"))
        );
        assertThat(DATypeImportComputer.computeImports(daType)).extracting("name").containsOnly("toto", "titi");
    }

    @Test
    public void getImports_multiple_typeArg() throws Exception {
        DAType daType = daType(
                "toto",
                ImmutableList.of(daType("titi"), daType("tutu"))
        );
        assertThat(DATypeImportComputer.computeImports(daType)).extracting("name").containsOnly("toto", "titi", "tutu");
    }

    @Test
    public void getImports_one_typeArg_recursive() throws Exception {
        DAType daType = daType(
                "toto",
                ImmutableList.of(
                        daType("titi", ImmutableList.of(daType("tutu")))
                )
        );
        assertThat(DATypeImportComputer.computeImports(daType)).extracting("name").containsOnly("toto", "titi", "tutu");
    }

    @Test
    public void testGetImports() throws Exception {
        DAType daType = daType("toto");
        assertThat(DATypeImportComputer.computeImports(daType)).extracting("name").containsOnly("toto");

        daType = daType("toto", ImmutableList.of(daType("titi")));
        assertThat(DATypeImportComputer.computeImports(daType)).extracting("name").containsOnly("toto", "titi");
    }

    private static DAType daType(String name) {
        return daType(name, Collections.<DAType>emptyList());
    }

    private static DAType daType(String name, List<DAType> typeArgs) {
        DAName daName = DANameFactory.from(name);
        return DAType.builder(TypeKind.DECLARED)
                .withSimpleName(daName)
                .withQualifiedName(daName)
                .withTypeArgs(typeArgs)
                .build();
    }
}
