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
package fr.phan.damapping.test;

import org.testng.annotations.Test;

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
