package fr.javatronic.damapping.processor.impl.javaxparsing;

import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.DAEnumValue;
import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.DAParameter;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.util.Function;

import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;

/**
 * JavaxExtractor - Extracts objects of the DAMapping model from the objects of the Javax model.
 *
 * @author Sébastien Lesaint
 */
public interface JavaxExtractor {
  @Nonnull
  DAType extractType(TypeMirror type);

  @Nonnull
  DAType extractWildcardType(WildcardType wildcardType);

  @Nonnull
  DAType extractReturnType(ExecutableElement methodElement);

  @Nonnull
  List<DAType> extractTypeArgs(TypeMirror typeMirror);

  @Nonnull
  Set<DAModifier> extractModifiers(ExecutableElement methodElement);

  @Nonnull
  Function<Modifier, DAModifier> toDAModifier();

  @Nonnull
  Function<AnnotationMirror,DAAnnotation> toDAAnnotation();

  @Nullable
  List<DAParameter> extractParameters(ExecutableElement methodElement);

  @Nullable
  DAName extractSimpleName(TypeMirror type, Element element);

  @Nullable
  List<DAEnumValue> extractEnumValues(@Nonnull TypeElement classElement);

  @Nullable
  List<DAAnnotation> extractDAAnnotations(@Nullable TypeElement classElement);

  @Nullable
  List<DAAnnotation> extractDAAnnotations(@Nullable Element methodElement);

  @Nonnull
  Set<DAType> getUnresolvedTypes();
}
