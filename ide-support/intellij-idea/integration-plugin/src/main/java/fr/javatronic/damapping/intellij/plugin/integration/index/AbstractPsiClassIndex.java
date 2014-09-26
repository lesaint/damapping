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
package fr.javatronic.damapping.intellij.plugin.integration.index;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.util.indexing.DataIndexer;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.indexing.FileContent;
import com.intellij.util.indexing.ScalarIndexExtension;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import org.jetbrains.annotations.NotNull;

/**
 * AbstractPsiClassIndex -
 *
 * @author Sébastien Lesaint
 */
public abstract class AbstractPsiClassIndex extends ScalarIndexExtension<String> {

  private static final FileBasedIndex.InputFilter JAVA_SOURCE_FILE_INPUT_FILTER = new FileBasedIndex.InputFilter() {
    @Override
    public boolean acceptInput(VirtualFile file) {
      return file.getFileType() instanceof JavaFileType;
    }
  };

  @NotNull
  @Override
  public DataIndexer<String, Void, FileContent> getIndexer() {
    return new DataIndexer<String, Void, FileContent>() {
      @NotNull
      @Override
      public Map<String, Void> map(FileContent inputData) {
        PsiJavaFile psiJavaFile = (PsiJavaFile) inputData.getPsiFile();
        Map<String, Void> res = new HashMap<String, Void>(psiJavaFile.getClasses().length);
        for (PsiClass psiClass : psiJavaFile.getClasses()) {
          if (filter(psiClass)) {
            Set<String> keys = getKeys(psiClass);
            for (String key : keys) {
              res.put(key, null);
            }
          }
        }
        return res;
      }
    };
  }

  /**
   * Allows filtering which class should be indexed. Typically, only PsiClass annoted with @Mapper.
   */
  protected abstract boolean filter(PsiClass psiClass);

  /**
   * Builds the Set of keys for the specified PsiClass (which has first been tested with
   * {@link #filter(com.intellij.psi.PsiClass)} and returned {@code true}).
   */
  protected abstract Set<String> getKeys(PsiClass psiClass);

  @Override
  public KeyDescriptor<String> getKeyDescriptor() {
    return new EnumeratorStringDescriptor();
  }

  @Override
  public FileBasedIndex.InputFilter getInputFilter() {
    return JAVA_SOURCE_FILE_INPUT_FILTER;
  }

  @Override
  public boolean dependsOnFileContent() {
    return true;
  }
}
