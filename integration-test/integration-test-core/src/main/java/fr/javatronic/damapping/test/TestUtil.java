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
package fr.javatronic.damapping.test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.commons.io.FileUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TestUtil -
 *
 * @author Sébastien Lesaint
 */
class TestUtil {
  private static final String CHARSET_NAME = "UTF-8";

  private final Class<?> classUnderTest;

  TestUtil(Class<?> classUnderTest) {
    this.classUnderTest = classUnderTest;
  }

  void checkGeneratedFile(String suffix) throws URISyntaxException, IOException {
    String tgtName = buildTargetFilename(suffix);

    File tgtFile = new File(getClass().getResource(tgtName).toURI());
    // assuming tgtFile is in the form [path_to_clone_of_dozer-annihilation]/test/test-mapper-enum/target/test
    // -classes/fr/javatronic/damapping/test/ConstructorInstancedGuavaFunctionMapper.java.tgt
    File mavenTargetDir = tgtFile.getParentFile()
                                 .getParentFile()
                                 .getParentFile()
                                 .getParentFile()
                                 .getParentFile()
                                 .getParentFile();
    File srcFile = new File(mavenTargetDir,
        "generated-sources/annotations/" + classUnderTest.getCanonicalName().replaceAll("\\.", "/") + suffix + ".java"
    );

    assertThat(srcFile).usingCharset(CHARSET_NAME).hasContent(FileUtils.readFileToString(tgtFile, "UTF-8"));
  }

  String buildTargetFilename(String suffix) {
    return classUnderTest.getSimpleName() + suffix + ".java.tgt";
  }
}
