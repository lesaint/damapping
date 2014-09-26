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
package fr.javatronic.damapping.intellij.plugin.integration.provider;

import fr.javatronic.damapping.annotation.Mapper;
import fr.javatronic.damapping.intellij.plugin.integration.psiparsing.PsiAnnotationUtil;
import fr.javatronic.damapping.intellij.plugin.integration.psiparsing.PsiParsingService;
import fr.javatronic.damapping.intellij.plugin.integration.psiparsing.impl.PsiParsingServiceImpl;
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.sourcegenerator.GenerationContext;
import fr.javatronic.damapping.processor.sourcegenerator.SourceGenerationService;
import fr.javatronic.damapping.processor.sourcegenerator.SourceGenerationServiceImpl;
import fr.javatronic.damapping.processor.validator.DASourceClassValidator;
import fr.javatronic.damapping.processor.validator.DASourceClassValidatorImpl;
import fr.javatronic.damapping.processor.validator.ValidationError;

import java.util.Collections;
import java.util.List;
import com.google.common.collect.Lists;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.augment.PsiAugmentProvider;
import org.jetbrains.annotations.NotNull;

/**
 * DAMappingAugmentProvider - IDEA extension responsible for providing feedback to the developer on whether her usage
 * of the @Mapper annotation is valid or not.
 *
 * @author Sébastien Lesaint
 */
public class DAMappingAugmentProvider extends PsiAugmentProvider {
  private static final Logger LOGGER = Logger.getInstance(DAMappingAugmentProvider.class.getName());

  private final PsiParsingService parsingService;
  private final DASourceClassValidator sourceClassValidator;
  private final SourceGenerationService sourceGenerationService;

  public DAMappingAugmentProvider() {
    this(new PsiParsingServiceImpl(), new DASourceClassValidatorImpl(), new SourceGenerationServiceImpl());
  }

  public DAMappingAugmentProvider(PsiParsingService parsingService, DASourceClassValidator sourceClassValidator,
                                  SourceGenerationService sourceGenerationService) {
    this.parsingService = parsingService;
    this.sourceClassValidator = sourceClassValidator;
    this.sourceGenerationService = sourceGenerationService;
    LOGGER.debug("DAMappingAugmentProvider created");
  }

  @NotNull
  @Override
  public <Psi extends PsiElement> List<Psi> getAugments(@NotNull PsiElement element, @NotNull Class<Psi> type) {
    // Expecting that we are only augmenting an PsiClass
    // Don't filter !isPhysical elements or code auto completion will not work
    if (!(element instanceof PsiClass) || !element.isValid()) {
      LOGGER.debug("Not a PsiClass or no valid : " + element);
      return Collections.emptyList();
    }
    // skip processing during index rebuild
    final Project project = element.getProject();
    if (DumbService.getInstance(project).isDumb()) {
      LOGGER.debug("Index rebuilding in progress");
      return Collections.emptyList();
    }
    // TODO skip processing if plugin is disabled
    final PsiClass psiClass = (PsiClass) element;
    if (!PsiAnnotationUtil.isAnnotatedWith(psiClass, Mapper.class)) {
      LOGGER.debug("Class is not annoted with @Mapper");
      return Collections.emptyList();
    }
    PsiJavaFile javaFile = (PsiJavaFile) element.getContainingFile();
    PsiModifierList modifierList = psiClass.getModifierList();
    PsiAnnotation[] annotations = modifierList.getAnnotations();

    return createMapper(psiClass, project, element);
//    JavaPsiFacade.getElementFactory(project)
//                 .createClassFromText(mapperSrc, element /*TODO verify what is this second argument*/);
//
////        PsiClass res = new LightClass(JavaPsiFacade.getElementFactory(project).createClass(psiClass.getName() +
//// "Mapper"));
//
//    return Collections.emptyList();
  }

  private <Psi extends PsiElement> List<Psi> createMapper(PsiClass psiClass, final Project project, final PsiElement element) {
    DASourceClass daSourceClass = parsingService.parse(psiClass);
    try {
      sourceClassValidator.validate(daSourceClass);
    } catch (ValidationError validationError) {
      // TODO transform ValidationError into a message displayed on the annotation and IDEA's console
      LOGGER.error("Validation failed", validationError);
    }

    final List<Psi> res = Lists.newArrayList();
//    try {
//      sourceGenerationService.generateSourceFiles(
//          new DefaultGenerationContext(daSourceClass),
//          new SourceWriterDelegate() {
//            @Override
//            public void generateFile(SourceGenerator sourceGenerator, GenerationContext context) throws IOException {
//              StringBuffer buffer = new StringBuffer();
//              sourceGenerator.writeFile(new BufferedWriter(new StringBufferWriter(buffer)), context);
//              PsiClass classFromText = JavaPsiFacade.getElementFactory(project)
//                                                    .createClassFromText(buffer.toString(),
//                                                        element /*TODO verify what is this second argument*/);
//
////              res.add(classFromText);
////              generatedFile(sourceGenerator.fileName(context), buffer.toString(), context);
//            }
//
//          }
//      );
//    } catch (IOException e) {
//      LOGGER.error("Failed to generate source files");
//    }

    // convert PsiClass to DASource class
    // use Writer to create String instead of file from DASource class and return it
    return res;
  }

  private void generatedFile(String fileName, String fileContent, GenerationContext context) {
    // TODO feed the new file to
  }
}
