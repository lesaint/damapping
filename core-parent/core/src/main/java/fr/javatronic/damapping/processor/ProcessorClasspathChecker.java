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
package fr.javatronic.damapping.processor;

/**
 * ProcessorClasspathChecker - Exposes methods to check the Annotation Processor's classpath for classes/interfaces
 * availability.
 *
 * @author Sébastien Lesaint
 */
public interface ProcessorClasspathChecker {

  /**
   * Tells whether the JSR-330 classes and interfaces are available in the classpath of the annotation processor.
   */
  public boolean isJSR330Present();

  /**
   * Tells whether the {@code NonNull} annotation is available in the classpath of the annotation processor.
   */
  public boolean isNonnullPresent();
}
