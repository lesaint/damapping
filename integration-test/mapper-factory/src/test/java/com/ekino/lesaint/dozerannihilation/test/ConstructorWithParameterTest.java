package com.ekino.lesaint.dozerannihilation.test;

import org.testng.annotations.Test;

/**
 * ConstructorWithParameterTest -
 *
 * @author Sébastien Lesaint
 */
public class ConstructorWithParameterTest {

    protected final TestUtil testUtil;

    public ConstructorWithParameterTest() {
        this.testUtil = new TestUtil(ConstructorWithParameter.class);
    }

    @Test
    public void check_generated_mapper_file() throws Exception {
        testUtil.checkGeneratedFile("Mapper");
    }

    @Test
    public void check_generated_mapperFactory_file() throws Exception {
        testUtil.checkGeneratedFile("MapperFactory");
    }

    @Test
    public void check_generated_mapperFactoryImpl_file() throws Exception {
        testUtil.checkGeneratedFile("MapperFactoryImpl");
    }
}
