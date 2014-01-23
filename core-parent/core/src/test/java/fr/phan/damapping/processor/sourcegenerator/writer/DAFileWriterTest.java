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
package fr.phan.damapping.processor.sourcegenerator.writer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import fr.phan.damapping.processor.model.DAModifier;
import fr.phan.damapping.processor.model.DAName;
import fr.phan.damapping.processor.model.factory.DANameFactory;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DAFileWriterTest -
 *
 * @author Sébastien Lesaint
 */
public class DAFileWriterTest {

    public static final DAName PACKAGE_NAME = DANameFactory.from("com.acme.toto");

    @Test
    public void empty_file() throws Exception {
        TestWriters testWriters = new TestWriters();
        new DAFileWriter(testWriters.bw);

        assertThat(testWriters.getRes()).isEqualTo("");
    }

    @Test
    public void package_only() throws Exception {
        TestWriters testWriters = new TestWriters();
        new DAFileWriter(testWriters.bw).appendPackage(PACKAGE_NAME);

        assertThat(testWriters.getRes()).isEqualTo("package com.acme.toto;" + DAWriterTestUtil.LINE_SEPARATOR + DAWriterTestUtil.LINE_SEPARATOR);
    }

    @Test
    public void package_imports_filtered_file() throws Exception {
        TestWriters testWriters = new TestWriters();
        new DAFileWriter(testWriters.bw)
                .appendPackage(PACKAGE_NAME)
                .appendImports(ImmutableSet.<DAName>of(
                        DAWriterTestUtil.FUNCTION_INTEGER_TO_STRING_INTERFACE.getQualifiedName(),
                        DAWriterTestUtil.BIDON_INTEGER_TO_STRING_ABSTRACT_CLASS.getQualifiedName(),
                        DAWriterTestUtil.OVERRIDE_ANNOTATION.getQualifiedName()
                ));

        assertThat(testWriters.getRes()).isEqualTo("package com.acme.toto;" + DAWriterTestUtil.LINE_SEPARATOR
                + DAWriterTestUtil.LINE_SEPARATOR
                + "import com.acme.Bidon;" + DAWriterTestUtil.LINE_SEPARATOR
                + "import com.google.common.base.Function;" + DAWriterTestUtil.LINE_SEPARATOR
                + DAWriterTestUtil.LINE_SEPARATOR);
    }

    @Test
    public void empty_class_file() throws Exception {
        TestWriters testWriters = new TestWriters();
        new DAFileWriter(testWriters.bw)
                .appendPackage(PACKAGE_NAME)
                .newClass(DAWriterTestUtil.NAME_DATYPE)
                .withModifiers(ImmutableSet.of(DAModifier.PUBLIC))
                .start()
                .end();

        assertThat(testWriters.getRes()).isEqualTo("package com.acme.toto;" + DAWriterTestUtil.LINE_SEPARATOR
                + DAWriterTestUtil.LINE_SEPARATOR
                + "public class Name {" + DAWriterTestUtil.LINE_SEPARATOR
                + DAWriterTestUtil.LINE_SEPARATOR
                + "}" + DAWriterTestUtil.LINE_SEPARATOR
        );
    }

    @Test
    public void empty_interface_file() throws Exception {
        TestWriters testWriters = new TestWriters();
        new DAFileWriter(testWriters.bw)
                .appendPackage(PACKAGE_NAME)
                .newInterface("name")
                .start()
                .end();

        assertThat(testWriters.getRes()).isEqualTo("package com.acme.toto;" + DAWriterTestUtil.LINE_SEPARATOR
                + DAWriterTestUtil.LINE_SEPARATOR
                + "interface name {" + DAWriterTestUtil.LINE_SEPARATOR
                + DAWriterTestUtil.LINE_SEPARATOR
                + "}" + DAWriterTestUtil.LINE_SEPARATOR
        );
    }

    @Test(expectedExceptions = IOException.class, expectedExceptionsMessageRegExp = "Stream closed")
    public void end_closes_writer() throws Exception {
        TestWriters testWriters = new TestWriters();
        new DAFileWriter(testWriters.bw).end();

        testWriters.bw.append("toto"); // raises IOException
    }

    @Test
    public void appendImports_emptyCollection_prints_nothing() throws Exception {
        TestWriters testWriters = new TestWriters();
        new DAFileWriter(testWriters.bw).appendImports(Collections.<DAName>emptyList());

        assertThat(testWriters.getRes()).isEqualTo("");
    }

    @Test
    public void appendImports_emptyCollection_after_filtering_prints_nothing() throws Exception {
        TestWriters testWriters = new TestWriters();
        new DAFileWriter(testWriters.bw).appendImports(ImmutableList.of(DANameFactory.from(String.class.getName())));

        assertThat(testWriters.getRes()).isEqualTo("");
    }

    @Test
    public void appendImports_filters_out_null_DAName_when_no_package_is_specified() throws Exception {
        TestWriters testWriters = new TestWriters();
        new DAFileWriter(testWriters.bw)
                .appendImports(Collections.singletonList((DAName) null));

        assertThat(testWriters.getRes()).isEqualTo("");
    }

    @Test
    public void appendImports_filters_out_null_DAName_when_package_is_specified() throws Exception {
        TestWriters testWriters = new TestWriters();
        new DAFileWriter(testWriters.bw)
                .appendPackage(DANameFactory.from("com.acme"))
                .appendImports(Collections.singletonList((DAName) null));

        assertThat(testWriters.getRes()).isEqualTo("package com.acme;" + DAWriterTestUtil.LINE_SEPARATOR + DAWriterTestUtil.LINE_SEPARATOR);
    }

    @Test
    public void appendImports_filters_out_same_package_DAName_when_package_is_specified() throws Exception {
        TestWriters testWriters = new TestWriters();
        new DAFileWriter(testWriters.bw)
                .appendPackage(DANameFactory.from("com.acme"))
                .appendImports(ImmutableList.of(DANameFactory.from("com.acme.Some"), DANameFactory.from("Simon")));

        assertThat(testWriters.getRes()).isEqualTo("package com.acme;" + DAWriterTestUtil.LINE_SEPARATOR
                + DAWriterTestUtil.LINE_SEPARATOR
                + "import Simon;" + DAWriterTestUtil.LINE_SEPARATOR
                + DAWriterTestUtil.LINE_SEPARATOR);
    }

    @Test
    public void appendWarningComment_adds_text_and_newLine() throws Exception {
        TestWriters testWriters = new TestWriters();
        new DAFileWriter(testWriters.bw).appendWarningComment();

        assertThat(testWriters.getRes()).isEqualTo("// GENERATED CODE, DO NOT MODIFY, THIS WILL BE OVERRIDE" + DAWriterTestUtil.LINE_SEPARATOR);
    }
}
