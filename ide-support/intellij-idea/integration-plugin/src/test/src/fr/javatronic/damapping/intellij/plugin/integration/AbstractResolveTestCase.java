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

import java.io.IOException;

import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;

/**
 * AbstractResolveTestCase -
 *
 * //////////////////////////////
 * // THIS CLASS IS UNFNINISHED
 * //////////////////////////////
 *
 * @author Sébastien Lesaint
 */
public class AbstractResolveTestCase extends AbstractDAMappingTestCase {

  /**
   * Entry port for testing resolution of a class annoted with @Mapper.
   * The idea is to generate the source of a class which defines a variable of the type of the generated interface
   * (either a TotoMapper type or a TotoMapperFactory) and trigger the resolution of the type (I don't know yet how)
   * and see if the resolution is successfull.
   *
   * TODO : generate the file and parse it to a PsiFile
   * TODO : trigger the resolution (ie. calls to the DAMappingElementFinder class) (maybe with the findRefereenceAt method ?)
   */
  protected void doResolveTest(String className, boolean factory) throws IOException {
    for (FileSourceVariant variant : FileSourceVariant.values()) {
      String fileSource = variant.generateSource(className, factory);

//      FileType type = FileTypeRegistry.getInstance().getFileTypeByFileName(fileName);
//      return PsiFileFactory.getInstance(myFixture.getProject()).createFileFromText(fileName, type, fileSource);

//      PsiReference ref = psiFile.findReferenceAt(15/*offset*/);
//      psiFile.getText().indexOf("T");

    }
  }

  private static final String PACKAGE_TAG = "<package>";
  private static final String IMPORT_TAG = "<import>";
  private static final String TYPE_TAG = "<type>";
  private static final String TEST_PACKAGE = "fr.javatronic.damapping.test.use";
  private static final String OTHER_PACKAGE = "fr.javatronic.damapping.somepackage.other.deepdown";

  private static final String FILE_SOURCE_TEMPLATE = "package " + PACKAGE_TAG + ";\n" +
      "\n" +
      IMPORT_TAG +
      "public class UseMapper {\n" +
      "  private final " + TYPE_TAG + " mapper;\n" +
      "\n" +
      "  public UseEnumInstancedGuavaFunction(" + TYPE_TAG + " mapper) {\n" +
      "    this.mapper = mapper;\n" +
      "  }\n" +
      "}\n";
  private static final String IMPORT_TEMPLATE = "import " + IMPORT_TAG + ";\n" + "\n";

  private static interface SourceGenerator {
    String generateSource(String className, boolean factory);
  }

  private static enum FileSourceVariant {
    SAME_PACKAGE_WITHOUT_IMPORT() {
      @Override
      public String generateSource(String className, boolean factory) {
        return FILE_SOURCE_TEMPLATE
            .replace(PACKAGE_TAG, TEST_PACKAGE)
            .replace(IMPORT_TAG, "")
            .replace(TYPE_TAG, qualifiedName(simpleName(className, factory)));
      }
    },
    SAME_PACKAGE_WITH_IMPORT() {
      @Override
      public String generateSource(String className, boolean factory) {
        String simpleName = simpleName(className, factory);
        return FILE_SOURCE_TEMPLATE
            .replace(PACKAGE_TAG, TEST_PACKAGE)
            .replace(IMPORT_TAG, qualifiedName(simpleName))
            .replace(TYPE_TAG, simpleName);
      }
    },
    OTHER_PACKAGE_WITHOUT_IMPORT() {
      @Override
      String generateSource(String className, boolean factory) {
        return FILE_SOURCE_TEMPLATE
            .replace(PACKAGE_TAG, OTHER_PACKAGE)
            .replace(IMPORT_TAG, "")
            .replace(TYPE_TAG, qualifiedName(simpleName(className, factory)));
      }
    },
    OTHER_PACKAGE_WITH_IMPORT() {
      @Override
      String generateSource(String className, boolean factory) {
        String simpleName = simpleName(className, factory);
        return FILE_SOURCE_TEMPLATE
            .replace(PACKAGE_TAG, OTHER_PACKAGE)
            .replace(IMPORT_TAG, qualifiedName(simpleName))
            .replace(TYPE_TAG, simpleName);
      }
    };

    protected String qualifiedName(String simpleName) {
      return TEST_PACKAGE + "." + simpleName;
    }

    protected String simpleName(String className, boolean factory) {
      return className + (factory ? "MapperFactory" : "Mapper");
    }

    abstract String generateSource(String className, boolean factory);
  }

  private void doResolveTestImpl(String fileName, boolean factory) {




    // create a PsiFile in the package fr.javatronic.damapping.test.use
    // try resolving the Mapper or the MapperFactory with
    // 1) qualifiedName as type (so no import)
    // 2) simpleName as type and import

    PsiFile psiFile = loadToPsiFile(fileName);

    if (!(psiFile instanceof PsiJavaFile)) {
      fail("The test file type is not supported");
    }

    for (PsiField psiField : ((PsiJavaFile) psiFile).getClasses()[0].getFields()) {

      PsiType type = ((PsiTypeElement) ((PsiJavaFile) psiFile).getClasses()[0].getFields()[0].getChildren()[2])
          .getType(); //

      psiField.getName();
      psiField.getModifierList();
      psiField.getReference();
    }
  }
}
