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
package fr.javatronic.damapping.processor.sourcegenerator;

import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.DAImport;
import fr.javatronic.damapping.processor.model.DAMethod;
import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.function.DAAnnotationFunctions;
import fr.javatronic.damapping.processor.model.impl.DAImportImpl;
import fr.javatronic.damapping.util.Lists;
import fr.javatronic.damapping.util.Predicate;
import fr.javatronic.damapping.util.Predicates;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static fr.javatronic.damapping.processor.model.constants.JavaLangConstants.OVERRIDE_ANNOTATION;
import static fr.javatronic.damapping.util.FluentIterable.from;
import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * SourceGeneratorSupport -
 *
 * @author Sébastien Lesaint
 */
public class SourceGeneratorSupport {
  private static final List<DAAnnotation> OVERRIDE_ANNOTATION_AS_LIST = Lists.of(OVERRIDE_ANNOTATION);
  private static final Predicate<DAAnnotation> NOT_OVERRIDE_ANNOTATION = Predicates.not(
      Predicates.compose(Predicates.equalTo(OVERRIDE_ANNOTATION.getType()), DAAnnotationFunctions.toType())
  );

  /**
   * Compute the annotations of the a method overriding another one.
   * <p>
   * The returned list of composed of the {@code @Override} annotation followed by the annotations of the specified method.
   * </p>
   * <p>
   * The {@code @Override} annotation is always the first annnotation and is removed from annotations on the mapper
   * method on the DASourceClass to avoid duplicates.
   * </p>
   *
   * @param mapperMethod a {@link fr.javatronic.damapping.processor.model.DAMethod}
   *
   * @return a {@link fr.javatronic.damapping.util.Lists} of {@link fr.javatronic.damapping.processor.model.DAAnnotation}
   */
  @Nonnull
  public List<DAAnnotation> computeOverrideMethodAnnotations(@Nonnull DAMethod mapperMethod) {
    List<DAAnnotation> annotations = mapperMethod.getAnnotations();
    if (annotations.isEmpty() || (annotations.size() == 1 && OVERRIDE_ANNOTATION.equals(annotations.get(0)))) {
      return OVERRIDE_ANNOTATION_AS_LIST;
    }

    List<DAAnnotation> res = new ArrayList<DAAnnotation>();
    res.add(OVERRIDE_ANNOTATION);
    res.addAll(from(annotations).filter(NOT_OVERRIDE_ANNOTATION).toList());
    return res;
  }

  @Nonnull
  public List<DAAnnotation> removeOverrideAnnotation(@Nonnull DAMethod daMethod) {
    return from(daMethod.getAnnotations())
        .filter(NOT_OVERRIDE_ANNOTATION)
        .toList();
  }

  @Nonnull
  public List<DAImport> appendImports(@Nonnull List<DAImport> imports, @Nullable DAName... names) {
    checkNotNull(imports);
    if (names == null) {
      return imports;
    }

    List<DAImport> res = Lists.copyOf(imports);
    for (DAName name : names) {
      if (name == null) {
        continue;
      }
      res.add(DAImportImpl.from(name));
    }
    return res;
  }
}
