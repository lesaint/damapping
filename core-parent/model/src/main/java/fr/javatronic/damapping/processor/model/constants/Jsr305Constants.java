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
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.factory.DATypeFactory;
import fr.javatronic.damapping.processor.model.impl.DAAnnotationImpl;

/**
 * Jsr305Constants -
 *
 * @author Sébastien Lesaint
 */
public interface Jsr305Constants {
  public static final String NULLABLE_QUALIFIEDNAME = "javax.annotation.Nullable";
  public static final DAType NULLABLE_TYPE = DATypeFactory.declared(NULLABLE_QUALIFIEDNAME);
  public static final DAAnnotation NULLABLE_ANNOTATION = new DAAnnotationImpl(NULLABLE_TYPE);

  public static final String NONNULL_QUALIFIEDNAME = "javax.annotation.Nonnull";
  public static final DAType NONNULL_TYPE = DATypeFactory.declared(NONNULL_QUALIFIEDNAME);
  public static final DAAnnotation NONNULL_ANNOTATION = new DAAnnotationImpl(NONNULL_TYPE);

  public static final String RESOURCE_QUALIFIEDNAME = "javax.annotation.Resource";
  public static final DAType RESOURCE_TYPE = DATypeFactory.declared(RESOURCE_QUALIFIEDNAME);
  public static final DAAnnotation RESOURCE_ANNOTATION = new DAAnnotationImpl(RESOURCE_TYPE);

}
