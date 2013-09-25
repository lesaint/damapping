package com.ekino.lesaint.dozerannihilation.test;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ConstructorInstancedGuavaFunctionTest -
 *
 * @author lesaint
 */
public class ConstructorInstancedGuavaFunctionTest {

    private final String CHARSET_NAME = "UTF-8";

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
        String tgtName = ConstructorInstancedGuavaFunction.class.getSimpleName() + suffix + ".java.tgt";

        File tgtFile = new File(getClass().getResource(tgtName).toURI());
        File mavenTargetDir = tgtFile.getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getParentFile();
        File srcFile = new File(mavenTargetDir, "generated-sources/annotations/" + ConstructorInstancedGuavaFunction.class.getCanonicalName().replaceAll("\\.", "/") + suffix + ".java");

        //System.err.println("mavenTargetDir=" + mavenTargetDir+ " src=" + src);
        assertThat(srcFile).usingCharset(CHARSET_NAME).hasContent(FileUtils.readFileToString(tgtFile, "UTF-8"));
    }
}
