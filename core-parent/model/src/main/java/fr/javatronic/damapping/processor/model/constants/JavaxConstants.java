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
package fr.javatronic.damapping.processor.model.constants;

import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.impl.DAAnnotationImpl;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.factory.DATypeFactory;

import javax.annotation.Nullable;
import javax.annotation.Resource;

/**
 * JAVA_LANG_CONSTANTS -
 *
 * @author Sébastien Lesaint
 */
public final class JavaxConstants {
  public static final DAType NULLABLE_TYPE = DATypeFactory.from(Nullable.class);
  public static final DAAnnotation NULLABLE_ANNOTATION = new DAAnnotationImpl(NULLABLE_TYPE);

  public static final DAType RESOURCE_TYPE = DATypeFactory.from(Resource.class);
  public static final DAAnnotation RESOURCE_ANNOTATION = new DAAnnotationImpl(RESOURCE_TYPE);

  private JavaxConstants() {
    // prevents instantiation
  }

}
