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
package fr.javatronic.damapping.intellij.plugin.integration.psiparsing;

import fr.javatronic.damapping.intellij.plugin.integration.AbstractIntegrationTestCase;
import fr.javatronic.damapping.intellij.plugin.integration.psiparsing.impl.PsiParsingServiceImpl;
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.sourcegenerator.GeneratedFileDescriptor;
import fr.javatronic.damapping.processor.sourcegenerator.GenerationContext;
import fr.javatronic.damapping.processor.sourcegenerator.GenerationContextComputer;
import fr.javatronic.damapping.processor.sourcegenerator.GenerationContextComputerImpl;
import fr.javatronic.damapping.processor.sourcegenerator.SourceGenerationService;
import fr.javatronic.damapping.processor.sourcegenerator.SourceGenerationServiceImpl;
import fr.javatronic.damapping.processor.sourcegenerator.SourceWriterDelegate;
import fr.javatronic.damapping.processor.validator.DASourceClassValidator;
import fr.javatronic.damapping.processor.validator.DASourceClassValidatorImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nonnull;
import com.google.common.collect.Lists;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import org.codehaus.groovy.runtime.StringBufferWriter;

/**
 * AbstractPsiParsingAndGenerateSourceTest -
 *
 * @author Sébastien Lesaint
 */
public abstract class AbstractPsiParsingAndGenerateSourceTest extends AbstractIntegrationTestCase {
  private final File tgtFileDirPath;

  private final DASourceClassValidator sourceClassValidator = new DASourceClassValidatorImpl();
  private final GenerationContextComputer generationContextComputer = new GenerationContextComputerImpl();
  private final SourceGenerationService sourceGenerationService = new SourceGenerationServiceImpl();
  private final PsiParsingService psiParsingService = new PsiParsingServiceImpl();

  protected AbstractPsiParsingAndGenerateSourceTest(String moduleName) {
    super(moduleName);
    tgtFileDirPath = new File("integration-test/" + moduleName + "/src/test/resources");
  }

  /**
   * Tests Psi parsing, DASourceClass validation and generated class source generation for a @Mapper class which name
   * is guessed from the name of the test method
   */
  protected void doPsiParsingAndGenerateSourceTest() throws Exception {
    String javaSourceFileName = getTestName(false).replace('$', '/') + ".java";
    DASourceClass[] daSourceClasses = parsePsiClasses(javaSourceFileName);
    for (DASourceClass daSourceClass : daSourceClasses) {
      sourceClassValidator.validate(daSourceClass);
      GenerationContext generationContext = generationContextComputer.compute(daSourceClass);
      sourceGenerationService.generateAll(
          generationContext,
          new SourceWriterDelegate() {
            @Override
            public void generateFile(@Nonnull GeneratedFileDescriptor descriptor) throws IOException {
                StringBuffer buffer = new StringBuffer();
                descriptor.getSourceGenerator().writeFile(new BufferedWriter(new StringBufferWriter(buffer)));

                // sourceGenerator.fileName(context) actually returns the qualifiedName of the class...
                String qualifiedName = descriptor.getType().getSimpleName().getName();
                String tgtFileName = qualifiedName.replace(".", "/") + ".java.tgt";

                String expected = new String(FileUtil.loadFileText(new File(tgtFileDirPath, tgtFileName), "UTF-8"));

                assertEquals("Source does not match for " + qualifiedName, expected, buffer.toString());
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
