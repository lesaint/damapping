package fr.phan.damapping.intellij.plugin.integration;

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
    return "integration-test/" + module + "/src/main/java/fr/phan/damapping/test";
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
