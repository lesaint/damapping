package fr.phan.damapping.intellij.plugin.integration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.augment.PsiAugmentProvider;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.apache.log4j.Logger;

/**
 * AbstractDAMappingTestCase -
 *
 * <p>
 *   Requirements to run this unit test :
 *   <ul>
 *     <li>working direcrtory of your test configuration must be the root of the maven project</li>
 *   </ul>
 * </p>
 *
 * @author Sébastien Lesaint
 */
public abstract class AbstractDAMappingTestCase extends LightCodeInsightFixtureTestCase {
  private static final Logger LOG = Logger.getLogger(AbstractDAMappingTestCase.class);

  private static final String DAMAPPING_SRC_PATH = "core-parent/annotations/src/main/java";
  public static final String MAIN_JAVA = "main/java/";

  @Override
  protected String getTestDataPath() {
    return "";
  }

  @Override
  protected String getBasePath() {
    return "ide-support/intellij-idea/integration-plugin/src/test/data";
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
    addDAMappingClassesToFixture();
  }

  private void addDAMappingClassesToFixture() {
    // Annotation must be added to the fixture for the AugmentProvider to be called
    loadFilesFrom(DAMAPPING_SRC_PATH);
  }

  protected void doTest() throws IOException {
    doTest(getTestName(false).replace('$', '/') + ".java");
  }

  private void doTest(String fileName) throws IOException {
    PsiFile psiFile = loadToPsiFile(fileName);

    if (!(psiFile instanceof PsiJavaFile)) {
      fail("The test file type is not supported");
    }

    PsiJavaFile intellij = (PsiJavaFile) psiFile;

    PsiClass[] intellijClasses = intellij.getClasses();
    for (PsiClass intellijClass : intellijClasses) {
      PsiAugmentProvider.collectAugments(intellijClass, PsiField.class);
    }
  }

  protected PsiFile loadToPsiFile(String fileName) {
    String filePath = getBasePath() + "/" + fileName;
    String relativeBase = getBasePath().substring(getBasePath().indexOf(MAIN_JAVA) + MAIN_JAVA.length());
    String relativePath = relativeBase + "/" + fileName;
    LOG.info("filePath=" + filePath + " relativePath=" + relativePath);
    VirtualFile virtualFile = myFixture.copyFileToProject(filePath, relativePath);
    myFixture.configureFromExistingVirtualFile(virtualFile);
    return myFixture.getFile();
  }

  private void loadFilesFrom(final String srcPath) {
    List<File> filesByMask = FileUtil.findFilesByMask(Pattern.compile(".*\\.java"), new File(srcPath));
    for (File javaFile : filesByMask) {
      String filePath = javaFile.getPath().replace("\\", "/");
      String substring = filePath.substring(srcPath.length() + 1);
      LOG.info("filePath=" + filePath + " substring=" + substring);
      myFixture.copyFileToProject(filePath, substring);
    }
  }

}
