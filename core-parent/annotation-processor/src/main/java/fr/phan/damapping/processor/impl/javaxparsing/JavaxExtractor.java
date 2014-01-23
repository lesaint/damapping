package fr.phan.damapping.processor.impl.javaxparsing;

import fr.phan.damapping.processor.model.DAEnumValue;
import fr.phan.damapping.processor.model.DAModifier;
import fr.phan.damapping.processor.model.DAName;
import fr.phan.damapping.processor.model.DAParameter;
import fr.phan.damapping.processor.model.DAType;

import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import com.google.common.base.Function;

/**
 * JavaxExtractor - Extracts objects of the DAMapping model from the objects of the Javax model.
 *
 * @author: Sébastien Lesaint
 */
public interface JavaxExtractor {
  @Nonnull
  DAType extractType(TypeMirror type);

  @Nonnull
  DAType extractType(TypeMirror type, Element element);

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

  @Nullable
  List<DAParameter> extractParameters(ExecutableElement methodElement);

  @Nullable
  DAName extractSimpleName(TypeMirror type, Element element);

  @Nullable
  DAName extractQualifiedName(TypeMirror type, Element element);

  @Nullable
  DAName extractQualifiedName(DeclaredType o);

  @Nullable
  List<DAEnumValue> extractEnumValues(@Nonnull TypeElement classElement);
}
