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
package fr.javatronic.damapping.processor.impl;

import fr.javatronic.damapping.processor.ProcessorClasspathChecker;
import fr.javatronic.damapping.processor.impl.javaxparsing.ElementUtils;
import fr.javatronic.damapping.processor.model.constants.Jsr305Constants;

import javax.annotation.Nonnull;

import static fr.javatronic.damapping.processor.model.constants.Jsr330Constants.INJECT_QUALIFIEDNAME;
import static fr.javatronic.damapping.processor.model.constants.Jsr330Constants.SCOPE_QUALIFIED_NAME;
import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * ElementsProcessorClasspathChecker -
 *
 * @author Sébastien Lesaint
 */
public class ElementsProcessorClasspathChecker implements ProcessorClasspathChecker {
  @Nonnull
  private final ElementUtils elements;

  public ElementsProcessorClasspathChecker(@Nonnull ElementUtils elements) {
    this.elements = checkNotNull(elements);
  }

  @Override
  public boolean isJSR330Present() {
    return elements.getTypeElement(INJECT_QUALIFIEDNAME) != null
        && elements.getTypeElement(SCOPE_QUALIFIED_NAME) != null;
  }

  @Override
  public boolean isNonnullPresent() {
    return elements.getTypeElement(Jsr305Constants.NONNULL_QUALIFIEDNAME) != null;
  }
}
