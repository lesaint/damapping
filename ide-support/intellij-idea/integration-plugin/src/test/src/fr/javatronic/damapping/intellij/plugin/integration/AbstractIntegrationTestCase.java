/**
 * Copyright (C) 2013 Sébastien Lesaint (http://www.javatronic.fr/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.javatronic.damapping.intellij.plugin.integration;

import java.io.File;
import java.io.IOException;

/**
 * AbstractIntegrationTestCase -
 *
 * @author Sébastien Lesaint
 */
public abstract class AbstractIntegrationTestCase extends AbstractDAMappingTestCase {
  private final String module;

  public AbstractIntegrationTestCase(String module) {
    this.module = module;
  }

  private String getModule() {
    return module;
  }

  @Override
  protected String getBasePath() {
    return "integration-test/" + module + "/src/main/java/fr/javatronic/damapping/test";
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
  }

  protected void doTest(String... paths) throws IOException {
    String baseAbsolutePath = new File(getBasePath()).getPath().replace("\\", "/");
    for (String path : paths) {
      myFixture.copyFileToProject(baseAbsolutePath + "/" + path, path);
    }
    doTest(getTestName(false).replace('$', '/') + ".java");
  }
}
