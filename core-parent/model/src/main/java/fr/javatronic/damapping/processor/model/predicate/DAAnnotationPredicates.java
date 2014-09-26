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
package fr.javatronic.damapping.processor.model.predicate;

import fr.javatronic.damapping.annotation.Mapper;
import fr.javatronic.damapping.annotation.MapperFactory;
import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.util.Optional;
import fr.javatronic.damapping.util.Predicate;

import javax.annotation.Nullable;

/**
 * DAAnnotationPredicates - Predicate factory for class DAAnnotation
 *
 * @author Sébastien Lesaint
 */
public final class DAAnnotationPredicates {

  private static final String MAPPER_ANNOTATION_QUALIFIEDNAME = Mapper.class.getName();
  private static final String MAPPERFACTORYMETHOD_ANNOTATION_QUALIFIEDNAME = MapperFactory.class.getName();
  private static final String SPRING_COMPONENT_ANNOTATION_QUALIFIEDNAME = "org.springframework.stereotype.Component";

  private DAAnnotationPredicates() {
    // prevents instantiation
  }

  /**
   * Predicate to find the @Mapper annotation
   */
  public static Predicate<DAAnnotation> isMapper() {
    return MapperPredicate.INSTANCE;
  }

  private static enum MapperPredicate implements Predicate<DAAnnotation> {
    INSTANCE;

    @Override
    public boolean apply(@Nullable DAAnnotation input) {
      Optional<DAName> daNameOptional = extractQualifiedName(input);
      if (daNameOptional.isPresent()) {
        return MAPPER_ANNOTATION_QUALIFIEDNAME.equals(daNameOptional.get().getName());
      }
      return false;
    }
  }

  public static Predicate<DAAnnotation> isMapperFactoryMethod() {
    return MapperFactoryPredicate.INSTANCE;
  }

  private static enum MapperFactoryPredicate implements Predicate<DAAnnotation> {
    INSTANCE;

    @Override
    public boolean apply(@Nullable DAAnnotation input) {
      Optional<DAName> daNameOptional = extractQualifiedName(input);
      if (daNameOptional.isPresent()) {
        return MAPPERFACTORYMETHOD_ANNOTATION_QUALIFIEDNAME.equals(daNameOptional.get().getName());
      }
      return false;
    }
  }

  /**
   * Predicate to find the @Component annotation of Spring
   */
  public static Predicate<DAAnnotation> isSpringComponent() {
    return SpringComponentPredicate.INSTANCE;
  }

  private static enum SpringComponentPredicate implements Predicate<DAAnnotation> {
    INSTANCE;
    @Override
    public boolean apply(@Nullable DAAnnotation input) {
      Optional<DAName> daNameOptional = extractQualifiedName(input);
      if (daNameOptional.isPresent()) {
        return SPRING_COMPONENT_ANNOTATION_QUALIFIEDNAME.equals(daNameOptional.get().getName());
      }
      return false;
    }

  }

  private static Optional<DAName> extractQualifiedName(DAAnnotation daAnnotation) {
    if (daAnnotation == null || daAnnotation.getType() == null) {
      return Optional.absent();
    }
    return Optional.fromNullable(daAnnotation.getType().getQualifiedName());
  }
}
