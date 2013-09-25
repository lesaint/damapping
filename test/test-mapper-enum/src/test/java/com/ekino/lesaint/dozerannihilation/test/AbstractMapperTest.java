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
 * @author f6
 * @version $Id: File Header.java 143213 2013-02-04 10:53:03Z lesaint $
 */
public abstract class AbstractMapperTest {
    protected final Class<?> classUnderTest;
    private final String CHARSET_NAME = "UTF-8";

    public AbstractMapperTest(Class<?> classUnderTest) {
        this.classUnderTest = classUnderTest;
    }

    @Test
    public void check_generated_mapper_file() throws Exception {
        checkGeneratedFile("Mapper", classUnderTest);
    }

    @Test
    public void check_generated_mapperImpl_file() throws Exception {
        checkGeneratedFile("MapperImpl", classUnderTest);
    }

    @Test
    public void check_generated_mapperFactory_file() throws Exception {
        checkGeneratedFile("MapperFactory", classUnderTest);
    }

    private void checkGeneratedFile(String suffix, Class<?> constructorInstancedGuavaFunctionClass) throws URISyntaxException, IOException {
        String tgtName = constructorInstancedGuavaFunctionClass.getSimpleName() + suffix + ".java.tgt";

        File tgtFile = new File(getClass().getResource(tgtName).toURI());
        File mavenTargetDir = tgtFile.getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getParentFile();
        File srcFile = new File(mavenTargetDir, "generated-sources/annotations/" + constructorInstancedGuavaFunctionClass.getCanonicalName().replaceAll("\\.", "/") + suffix + ".java");

        //System.err.println("mavenTargetDir=" + mavenTargetDir+ " src=" + src);
        assertThat(srcFile).usingCharset(CHARSET_NAME).hasContent(FileUtils.readFileToString(tgtFile, "UTF-8"));
    }
}
