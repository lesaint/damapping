package fr.phan.damapping.intellij.plugin.integration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.augment.PsiAugmentProvider;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.apache.log4j.Logger;

/**
 * DAMappingTestCase -
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
public class DAMappingTestCase extends LightCodeInsightFixtureTestCase {
  private static final Logger LOG = Logger.getLogger(DAMappingTestCase.class);
  private static final Set<String> modifiers = new HashSet<String>(Arrays.asList(
      PsiModifier.PUBLIC, PsiModifier.PACKAGE_LOCAL, PsiModifier.PROTECTED, PsiModifier.PRIVATE, PsiModifier.FINAL, PsiModifier.STATIC,
      PsiModifier.ABSTRACT, PsiModifier.SYNCHRONIZED, PsiModifier.TRANSIENT, PsiModifier.VOLATILE, PsiModifier.NATIVE));

  private static final String DAMAPPING_SRC_PATH = "core-parent/annotations/src/main/java";

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

  public void testEnumInstancedGuavaFunction() throws Exception {
    doTest();
  }

  public void doTest() throws IOException {
    doTest(getTestName(false).replace('$', '/') + ".java");
  }

  protected void doTest(String fileName) throws IOException {
    final PsiFile psiLombokFile = loadToPsiFile("before/" + fileName);

    if (!(psiLombokFile instanceof PsiJavaFile)) {
      fail("The test file type is not supported");
    }

    final PsiJavaFile intellij = (PsiJavaFile) psiLombokFile;

    PsiClass[] intellijClasses = intellij.getClasses();
    for (PsiClass intellijClass : intellijClasses) {
      PsiAugmentProvider.collectAugments(intellijClass, PsiField.class);
    }
  }

  private PsiFile loadToPsiFile(String fileName) {
    VirtualFile virtualFile = myFixture.copyFileToProject(getBasePath() + "/" + fileName, fileName);
    myFixture.configureFromExistingVirtualFile(virtualFile);
    return myFixture.getFile();
  }

  private void loadFilesFrom(final String srcPath) {
    List<File> filesByMask = FileUtil.findFilesByMask(Pattern.compile(".*\\.java"), new File(srcPath));
    for (File javaFile : filesByMask) {
      String filePath = javaFile.getPath().replace("\\", "/");
      String substring = filePath.substring(srcPath.length() + 1);
      LOG.error("filePath="+filePath+" substring="+substring);
      myFixture.copyFileToProject(filePath, substring);
    }
  }

}
