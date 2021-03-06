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

import fr.javatronic.damapping.processor.ProcessorClasspathChecker;

import javax.annotation.Nonnull;

import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * AbstractSourceGenerator -
 *
 * @author Sébastien Lesaint
 */
abstract class AbstractSourceGenerator implements SourceGenerator {

  static final String DAMAPPING_ANNOTATION_PROCESSOR_QUALIFIED_NAME = "fr.javatronic.damapping.processor.DAAnnotationProcessor";

  @Nonnull
  protected final GeneratedFileDescriptor descriptor;
  @Nonnull
  protected final SourceGeneratorSupport support;
  @Nonnull
  protected final ProcessorClasspathChecker classpathChecker;

  protected AbstractSourceGenerator(@Nonnull GeneratedFileDescriptor descriptor,
                                    @Nonnull SourceGeneratorSupport support,
                                    @Nonnull ProcessorClasspathChecker classpathChecker) {
    this.descriptor = checkNotNull(descriptor);
    this.support = checkNotNull(support);
    this.classpathChecker = checkNotNull(classpathChecker);
  }
}
