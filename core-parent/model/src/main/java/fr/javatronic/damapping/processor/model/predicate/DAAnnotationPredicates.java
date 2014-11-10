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

import fr.javatronic.damapping.annotation.Injectable;
import fr.javatronic.damapping.annotation.Mapper;
import fr.javatronic.damapping.annotation.MapperFactory;
import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.constants.Jsr330Constants;
import fr.javatronic.damapping.util.Optional;
import fr.javatronic.damapping.util.Predicate;

import javax.annotation.Nullable;

import static fr.javatronic.damapping.util.FluentIterable.from;

/**
 * DAAnnotationPredicates - Predicate factory for class DAAnnotation
 *
 * @author Sébastien Lesaint
 */
public final class DAAnnotationPredicates {

  private static final String MAPPER_ANNOTATION_QUALIFIEDNAME = Mapper.class.getName();
  private static final String MAPPERFACTORYMETHOD_ANNOTATION_QUALIFIEDNAME = MapperFactory.class.getName();

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

  private static Optional<DAName> extractQualifiedName(DAAnnotation daAnnotation) {
    if (daAnnotation == null || daAnnotation.getType() == null) {
      return Optional.absent();
    }
    return Optional.fromNullable(daAnnotation.getType().getQualifiedName());
  }

  public static Predicate<DAAnnotation> isInjectable() {
    return InjectablePredicate.INSTANCE;
  }

  private static enum InjectablePredicate implements Predicate<DAAnnotation> {
    INSTANCE;

    @Override
    public boolean apply(@Nullable DAAnnotation daAnnotation) {
      return daAnnotation != null && Injectable.class.getCanonicalName().contentEquals(daAnnotation.getType().getQualifiedName());
    }
  }

  /**
   * Precicate to find annotations with annotated with {@link Jsr330Constants#QUALIFIER_QUALIFIED_NAME} directly or
   * indirectly.
   */
  public static Predicate<DAAnnotation> isQualifier() {
    return IsQualifierPredicate.INSTANCE;
  }

  private static enum IsQualifierPredicate implements Predicate<DAAnnotation> {
    INSTANCE;

    @Override
    public boolean apply(@Nullable DAAnnotation daAnnotation) {
      return isQualifier(daAnnotation) || from(daAnnotation.getAnnotations()).firstMatch(INSTANCE).isPresent();
    }

    private boolean isQualifier(DAAnnotation daAnnotation) {
      Optional<DAName> qualifiedName = extractQualifiedName(daAnnotation);
      return qualifiedName.isPresent() && qualifiedName.get().compareTo(Jsr330Constants.QUALIFIER_DANAME) == 0;
    }
  }

  /**
   * Precicate to find annotations with annotated with {@link Jsr330Constants#SCOPE_QUALIFIED_NAME} directly or
   * indirectly.
   */
  public static Predicate<DAAnnotation> isScope() {
    return IsScopePredicate.INSTANCE;
  }

  private static enum IsScopePredicate implements Predicate<DAAnnotation> {
    INSTANCE;

    @Override
    public boolean apply(@Nullable DAAnnotation daAnnotation) {
      return isQualifier(daAnnotation) || from(daAnnotation.getAnnotations()).firstMatch(INSTANCE).isPresent();
    }

    private boolean isQualifier(DAAnnotation daAnnotation) {
      Optional<DAName> qualifiedName = extractQualifiedName(daAnnotation);
      return qualifiedName.isPresent() && qualifiedName.get().compareTo(Jsr330Constants.SCOPE_DANAME) == 0;
    }
  }
}
