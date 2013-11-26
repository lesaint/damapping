package fr.phan.damapping.test;

import fr.phan.damapping.test.AbstractMapperTest;
import fr.phan.damapping.test.ComponentFunction;

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
        String tgtFilename = testUtil.buildTargetFilename("MapperFactory");

        assertThat(getClass().getResource(tgtFilename)).isNull();
    }
}
