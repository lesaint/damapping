package com.ekino.lesaint.dozerannihilation.test;

import java.io.File;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ComponentFunctionTest -
 *
 * @author Sébastien Lesaint
 */
public class ComponentFunctionTest extends AbstractMapperTest {
    public ComponentFunctionTest() {
        super(ComponentFunction.class);
    }

    @Test
    @Override
    public void check_generated_mapperFactory_file() throws Exception {
        String tgtFilename = buildTargetFilename("MapperFactory");

        assertThat(getClass().getResource(tgtFilename)).isNull();
    }
}
