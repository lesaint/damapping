package fr.phan.damapping.processor.model.predicate;

import fr.phan.damapping.annotation.Mapper;
import fr.phan.damapping.processor.model.DAAnnotation;
import fr.phan.damapping.processor.model.DAName;

import javax.annotation.Nullable;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import org.springframework.stereotype.Component;

/**
 * DAAnnotationPredicates - Predicate factory for class DAAnnotation
 *
 * @author Sébastien Lesaint
 */
public final class DAAnnotationPredicates {

  private static final String MAPPER_ANNOTATION_QUALIFIEDNAME = Mapper.class.getName();
  private static final String SPRING_COMPONENT_ANNOTATION_QUALIFIEDNAME = Component.class.getName();

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
