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
package fr.javatronic.damapping.processor.impl.javaxparsing;

import fr.javatronic.damapping.util.Optional;

import java.lang.annotation.Annotation;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

/**
 * JavaxUtil -
 *
 * @author Sébastien Lesaint
 */
public interface JavaxUtil {
  @Nonnull
  Optional<AnnotationMirror> getAnnotationMirror(Element classElement,
                                                 Class<? extends Annotation> annotationClass);

  @Nullable
  String getEnumNameElementValue(AnnotationMirror annotationMirror, String elementName);
}
