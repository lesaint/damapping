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
package fr.javatronic.damapping.processor.sourcegenerator.writer;

import fr.javatronic.damapping.processor.model.DAImport;
import fr.javatronic.damapping.processor.model.impl.DAImportImpl;
import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.factory.DANameFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import javax.annotation.processing.Completion;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import org.testng.annotations.Test;

import static fr.javatronic.damapping.processor.model.constants.JavaLangConstants.OVERRIDE_ANNOTATION;
import static fr.javatronic.damapping.processor.sourcegenerator.writer.DAWriterTestUtil.LINE_SEPARATOR;
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
    FileContextTestImpl fileContext = new FileContextTestImpl();
    new DAFileWriter(fileContext.getWriter());

    assertThat(fileContext.getRes()).isEqualTo("");
  }

  @Test
  public void package_only() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    new DAFileWriter(fileContext.getWriter()).appendPackage(PACKAGE_NAME);

    assertThat(fileContext.getRes()).isEqualTo(
        "package com.acme.toto;" + LINE_SEPARATOR + LINE_SEPARATOR
    );
  }

  @Test
  public void package_imports_filtered_file() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    new DAFileWriter(fileContext.getWriter())
        .appendPackage(PACKAGE_NAME)
        .appendImports(ImmutableSet.<DAImport>of(
            DAImportImpl.from(DAWriterTestUtil.FUNCTION_INTEGER_TO_STRING_INTERFACE.getQualifiedName()),
            DAImportImpl.from(DAWriterTestUtil.BIDON_INTEGER_TO_STRING_ABSTRACT_CLASS.getQualifiedName()),
            DAImportImpl.from(OVERRIDE_ANNOTATION.getType().getQualifiedName())
        )
        );

    assertThat(fileContext.getRes()).isEqualTo("package com.acme.toto;" + LINE_SEPARATOR
        + LINE_SEPARATOR
        + "import com.acme.Bidon;" + LINE_SEPARATOR
        + "import com.google.common.base.Function;" + LINE_SEPARATOR
        + LINE_SEPARATOR
    );
  }

  @Test
  public void empty_class_file() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    new DAFileWriter(fileContext.getWriter())
        .appendPackage(PACKAGE_NAME)
        .newClass(DAWriterTestUtil.NAME_DATYPE)
        .withModifiers(DAModifier.PUBLIC)
        .start()
        .end();

    assertThat(fileContext.getRes()).isEqualTo("package com.acme.toto;" + LINE_SEPARATOR
        + LINE_SEPARATOR
        + "public class Name {" + LINE_SEPARATOR
        + "}" + LINE_SEPARATOR
    );
  }

  @Test
  public void empty_interface_file() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    new DAFileWriter(fileContext.getWriter())
        .appendPackage(PACKAGE_NAME)
        .newInterface("name")
        .start()
        .end();

    assertThat(fileContext.getRes()).isEqualTo("package com.acme.toto;" + LINE_SEPARATOR
        + LINE_SEPARATOR
        + "interface name {" + LINE_SEPARATOR
        + "}"
    );
  }

  @Test(expectedExceptions = IOException.class, expectedExceptionsMessageRegExp = "Stream closed")
  public void end_closes_writer() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    new DAFileWriter(fileContext.getWriter()).end();

    fileContext.getWriter().append("toto"); // raises IOException
  }

  @Test
  public void appendImports_emptyCollection_prints_nothing() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    new DAFileWriter(fileContext.getWriter()).appendImports(Collections.<DAImport>emptyList());

    assertThat(fileContext.getRes()).isEqualTo("");
  }

  @Test
  public void appendImports_emptyCollection_after_filtering_prints_nothing() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    new DAFileWriter(fileContext.getWriter()).appendImports(
        ImmutableList.of(DAImportImpl.from(DANameFactory.from(String.class.getName())))
    );

    assertThat(fileContext.getRes()).isEqualTo("");
  }

  @Test
  public void appendImports_filters_out_null_DAName_when_no_package_is_specified() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    new DAFileWriter(fileContext.getWriter())
        .appendImports(Collections.singletonList((DAImport) null));

    assertThat(fileContext.getRes()).isEqualTo("");
  }

  @Test
  public void appendImports_filters_out_null_DAName_when_package_is_specified() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    new DAFileWriter(fileContext.getWriter())
        .appendPackage(DANameFactory.from("com.acme"))
        .appendImports(Collections.singletonList((DAImport) null));

    assertThat(fileContext.getRes()).isEqualTo(
        "package com.acme;" + LINE_SEPARATOR + LINE_SEPARATOR
    );
  }

  @Test
  public void appendGeneratedAnnotation_from_String_adds_generated_annotation_and_newLine() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    new DAFileWriter(fileContext.getWriter()).appendGeneratedAnnotation(MyProcessor.class.getCanonicalName());

    assertThat(fileContext.getRes()).isEqualTo(
        "@javax.annotation.Generated(\"" + MyProcessor.class.getCanonicalName() + "\")" + LINE_SEPARATOR
    );
  }

  @Test
  public void appendGeneratedAnnotation_from_Class_adds_generated_annotation_with_class_qualifiedName_and_newLine()
      throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    new DAFileWriter(fileContext.getWriter()).appendGeneratedAnnotation(MyProcessor.class);

    assertThat(fileContext.getRes()).isEqualTo(
        "@javax.annotation.Generated(\"" + MyProcessor.class.getCanonicalName() + "\")" + LINE_SEPARATOR
    );
  }

  private static class MyProcessor implements Processor {
    @Override
    public Set<String> getSupportedOptions() {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void init(ProcessingEnvironment processingEnv) {
      //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
      return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotation,
                                                         ExecutableElement member, String userText) {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
  }

}
