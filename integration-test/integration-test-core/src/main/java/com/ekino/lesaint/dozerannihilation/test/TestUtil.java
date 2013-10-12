package com.ekino.lesaint.dozerannihilation.test;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

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
        // assuming tgtFile is in the form [path_to_clone_of_dozer-annihilation]/test/test-mapper-enum/target/test-classes/com/ekino/lesaint/dozerannihilation/test/ConstructorInstancedGuavaFunctionMapper.java.tgt
        File mavenTargetDir = tgtFile.getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getParentFile();
        File srcFile = new File(mavenTargetDir, "generated-sources/annotations/" + classUnderTest.getCanonicalName().replaceAll("\\.", "/") + suffix + ".java");

        assertThat(srcFile).usingCharset(CHARSET_NAME).hasContent(FileUtils.readFileToString(tgtFile, "UTF-8"));
    }

    String buildTargetFilename(String suffix) {
        return classUnderTest.getSimpleName() + suffix + ".java.tgt";
    }
}
