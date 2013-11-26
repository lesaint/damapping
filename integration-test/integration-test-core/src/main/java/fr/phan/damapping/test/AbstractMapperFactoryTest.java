package fr.phan.damapping.test;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * AbstractMapperTest -
 *
 * @author Sébastien Lesaint
 */
public abstract class AbstractMapperFactoryTest {

    protected final TestUtil testUtil;

    public AbstractMapperFactoryTest(Class<?> classUnderTest) {
        this.testUtil = new TestUtil(classUnderTest);
    }

    @Test
    public void check_generated_mapper_file() throws Exception {
        testUtil.checkGeneratedFile("Mapper");
    }

    @Test
    public void check_generated_mapperFactoryImpl_file() throws Exception {
        testUtil.checkGeneratedFile("MapperFactoryImpl");
    }

    @Test
    public void check_generated_mapperFactory_file() throws Exception {
        testUtil.checkGeneratedFile("MapperFactory");
    }

}
