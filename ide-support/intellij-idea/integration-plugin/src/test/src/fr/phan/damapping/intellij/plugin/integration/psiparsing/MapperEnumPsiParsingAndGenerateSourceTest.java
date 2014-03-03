package fr.phan.damapping.intellij.plugin.integration.psiparsing;

import fr.phan.damapping.intellij.plugin.integration.AbstractIntegrationTestCase;
import fr.phan.damapping.processor.model.DASourceClass;
import fr.phan.damapping.processor.sourcegenerator.DefaultFileGeneratorContext;
import fr.phan.damapping.processor.sourcegenerator.FileGeneratorContext;
import fr.phan.damapping.processor.sourcegenerator.SourceGenerationService;
import fr.phan.damapping.processor.sourcegenerator.SourceGenerationServiceImpl;
import fr.phan.damapping.processor.sourcegenerator.SourceGenerator;
import fr.phan.damapping.processor.sourcegenerator.SourceWriterDelegate;
import fr.phan.damapping.processor.validator.DASourceClassValidator;
import fr.phan.damapping.processor.validator.DASourceClassValidatorImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;
import com.google.common.collect.Lists;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import org.codehaus.groovy.runtime.StringBufferWriter;

/**
 * MapperEnumPsiParsingAndGenerateSourceTest -
 *
 * @author Sébastien Lesaint
 */
public class MapperEnumPsiParsingAndGenerateSourceTest extends AbstractIntegrationTestCase {

  public static final String MODULE_NAME = "mapper-enum";
  private final DASourceClassValidator sourceClassValidator = new DASourceClassValidatorImpl();
  private final SourceGenerationService sourceGenerationService = new SourceGenerationServiceImpl();
  private final PsiParsingService psiParsingService = new PsiParsingServiceImpl();

  private final File tgtFileDirPath;

  public MapperEnumPsiParsingAndGenerateSourceTest() {
    super(MODULE_NAME);
    tgtFileDirPath = new File("integration-test/" + MODULE_NAME + "/src/test/resources");
  }

  public void testEnumInstancedGuavaFunction() throws Exception {
    doPsiParsingAndGenerateSourceTest();
  }

  protected void doPsiParsingAndGenerateSourceTest() throws Exception {
    String javaSourceFileName = getTestName(false).replace('$', '/') + ".java";
    DASourceClass[] daSourceClasses = parsePsiClasses(javaSourceFileName);
    for (DASourceClass daSourceClass : daSourceClasses) {
      sourceClassValidator.validate(daSourceClass);
      sourceGenerationService.generateSourceFiles(
          new DefaultFileGeneratorContext(daSourceClass),
          new SourceWriterDelegate() {
            @Override
            public void generateFile(SourceGenerator sourceGenerator, FileGeneratorContext context) throws IOException {
              StringBuffer buffer = new StringBuffer();
              sourceGenerator.writeFile(new BufferedWriter(new StringBufferWriter(buffer)), context);

              // sourceGenerator.fileName(context) actually returns the qualifiedName of the class...
              String tgtFileName = sourceGenerator.fileName(context).replace(".", "/") + ".java.tgt";

              String expected = new String(FileUtil.loadFileText(new File(tgtFileDirPath, tgtFileName) , "UTF-8"));

              assertEquals(expected, buffer.toString());
            }
          }
      );
    }
  }

  protected DASourceClass[] parsePsiClasses(String javaSourceFileName) {
    PsiFile psiFile = loadToPsiFile(javaSourceFileName);

    if (!(psiFile instanceof PsiJavaFile)) {
      fail("The test file type is not supported");
    }

    PsiJavaFile intellij = (PsiJavaFile) psiFile;

    List<DASourceClass> res = Lists.newArrayList();
    PsiClass[] intellijClasses = intellij.getClasses();
    for (PsiClass intellijClass : intellijClasses) {
      DASourceClass daSourceClass = psiParsingService.parse(intellijClass);
      res.add(daSourceClass);
    }
    return res.toArray(new DASourceClass[res.size()]);
  }

}
