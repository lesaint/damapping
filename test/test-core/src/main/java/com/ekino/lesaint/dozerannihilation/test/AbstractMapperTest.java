package com.ekino.lesaint.dozerannihilation.test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.commons.io.FileUtils;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * AbstractMapperTest -
 *
 * @author Sébastien Lesaint
 */
public abstract class AbstractMapperTest {
    private static final String CHARSET_NAME = "UTF-8";

    private final Class<?> classUnderTest;

    public AbstractMapperTest(Class<?> classUnderTest) {
        this.classUnderTest = classUnderTest;
    }

    @Test
    public void check_generated_mapper_file() throws Exception {
        checkGeneratedFile("Mapper");
    }

    @Test
    public void check_generated_mapperImpl_file() throws Exception {
        checkGeneratedFile("MapperImpl");
    }

    @Test
    public void check_generated_mapperFactory_file() throws Exception {
        checkGeneratedFile("MapperFactory");
    }

    private void checkGeneratedFile(String suffix) throws URISyntaxException, IOException {
        String tgtName = classUnderTest.getSimpleName() + suffix + ".java.tgt";

        File tgtFile = new File(getClass().getResource(tgtName).toURI());
        // assuming tgtFile is in the form [path_to_clone_of_dozer-annihilation]/test/test-mapper-enum/target/test-classes/com/ekino/lesaint/dozerannihilation/test/ConstructorInstancedGuavaFunctionMapper.java.tgt
        File mavenTargetDir = tgtFile.getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getParentFile();
        File srcFile = new File(mavenTargetDir, "generated-sources/annotations/" + classUnderTest.getCanonicalName().replaceAll("\\.", "/") + suffix + ".java");

        assertThat(srcFile).usingCharset(CHARSET_NAME).hasContent(FileUtils.readFileToString(tgtFile, "UTF-8"));
    }
}
