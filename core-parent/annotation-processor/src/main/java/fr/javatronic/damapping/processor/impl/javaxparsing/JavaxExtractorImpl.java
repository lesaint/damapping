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
package fr.javatronic.damapping.processor.impl.javaxparsing;

import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.DAEnumValue;
import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.DAParameter;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.DATypeKind;
import fr.javatronic.damapping.processor.model.factory.DANameFactory;
import fr.javatronic.damapping.processor.model.factory.DATypeFactory;
import fr.javatronic.damapping.util.Function;
import fr.javatronic.damapping.util.Optional;
import fr.javatronic.damapping.util.Predicates;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Types;

import static fr.javatronic.damapping.util.FluentIterable.from;
import static fr.javatronic.damapping.util.Preconditions.checkNotNull;
import static fr.javatronic.damapping.util.Predicates.notNull;


/**
 * JavaxExtractorImpl - This implementation of {@link JavaxExtractor} supports replacing {@link DAType} created
 * from {@link TypeMirror} with type {@link TypeKind#ERROR} with "fixed" {@link DAType} objects from
 * {@link UnresolvedTypeScanResult}.
 *
 * @author Sébastien Lesaint
 */
public class JavaxExtractorImpl implements JavaxExtractor {
  @Nonnull
  private final Types typeUtils;
  @Nullable
  private final UnresolvedTypeScanResult scanResult;

  private final Function<AnnotationMirror, DAAnnotation> annotationMirrorToDAAnnotation = new Function<AnnotationMirror, DAAnnotation>() {
    @Nullable
    @Override
    public DAAnnotation apply(@Nullable AnnotationMirror input) {
      if (input == null) {
        return null;
      }
      return new DAAnnotation(extractType(input.getAnnotationType()));
    }
  };

  public JavaxExtractorImpl(@Nonnull Types typeUtils, @Nullable UnresolvedTypeScanResult scanResult) {
    this.typeUtils = checkNotNull(typeUtils);
    this.scanResult = scanResult;
  }

  @Override
  @Nonnull
  public DAType extractType(TypeMirror type) {
    Element element = typeUtils.asElement(type);
    if (type.getKind() == TypeKind.ARRAY) {
      element = typeUtils.asElement(((ArrayType) type).getComponentType());
    }

    DAType res = extractType(type, element);
    if (res.getKind() != DATypeKind.ERROR) {
      return res;
    }

    // FIXME here can not just return the type from the fixedResolutions map
    // current DAType may have specific typeArguments, bounds
    // we must take only the kind and qualified name from the DAType found in fixedResolution
    return findFixedResolution(type, element).or(res);
  }

  @Nonnull
  private Optional<DAType> findFixedResolution(TypeMirror type, Element element) {
    if (scanResult == null) {
      return Optional.absent();
    }

    DAName daName = extractQualifiedName(type, element);
    if (daName != null && !daName.getName().equals(element.getSimpleName().toString())) {
      return scanResult.findFixedByQualifiedName(daName.getName());
    }

    return scanResult.findFixedBySimpleName(element.getSimpleName().toString());
  }

  @Nonnull
  private DAType extractType(TypeMirror type, Element element) {
    if (type.getKind() == TypeKind.VOID) {
      return DATypeFactory.voidDaType();
    }
    if (type.getKind() == TypeKind.WILDCARD) {
      return extractWildcardType((WildcardType) type);
    }
    DAType.Builder builder = DAType
        .builder(
            TypeKindToDATypeKind.INSTANCE.apply(type.getKind()),
            extractSimpleName(type, element)
        )
        .withQualifiedName(extractQualifiedName(type, element))
        .withTypeArgs(extractTypeArgs(type));
    return builder.build();
  }

  private static enum TypeKindToDATypeKind implements Function<TypeKind, DATypeKind> {
    INSTANCE;

    @Nonnull
    @Override
    public DATypeKind apply(@Nonnull TypeKind typeKind) {
      return DATypeKind.valueOf(typeKind.name());
    }
  }

  @Override
  @Nonnull
  public DAType extractWildcardType(WildcardType wildcardType) {
    if (wildcardType.getSuperBound() != null) {
      return DATypeFactory.wildcardWithSuperBound(extractType(wildcardType.getSuperBound()));
    }
    if (wildcardType.getExtendsBound() != null) {
      return DATypeFactory.wildcardWithExtendsBound(extractType(wildcardType.getExtendsBound()));
    }
    throw new IllegalArgumentException("Unsupported WildcardType has neither superbound nor extends bound");
  }

  @Override
  @Nonnull
  public List<DAType> extractTypeArgs(TypeMirror typeMirror) {
    if (!(typeMirror instanceof DeclaredType)) {
      return Collections.emptyList();
    }

    List<? extends TypeMirror> typeArguments = ((DeclaredType) typeMirror).getTypeArguments();
    if (typeArguments == null) {
      return Collections.emptyList();
    }

    return from(typeArguments)
        .transform(new Function<TypeMirror, DAType>() {
          @Nullable
          @Override
          public DAType apply(@Nullable TypeMirror o) {
            if (o == null) {
              return null;
            }

            return extractType(o);
          }
        }
        )
        .filter(notNull())
        .toList();
  }

  @Override
  @Nonnull
  public DAType extractReturnType(ExecutableElement methodElement) {
    return extractType(methodElement.getReturnType());
  }

  @Override
  @Nonnull
  public Set<DAModifier> extractModifiers(ExecutableElement methodElement) {
    if (methodElement.getModifiers() == null) {
      return Collections.emptySet();
    }
    return from(methodElement.getModifiers()).transform(toDAModifier()).toSet();
  }

  @Override
  @Nonnull
  public Function<Modifier, DAModifier> toDAModifier() {
    return ModifierToDAModifier.INSTANCE;
  }

  @Nonnull
  @Override
  public Function<AnnotationMirror, DAAnnotation> toDAAnnotation() {
    return annotationMirrorToDAAnnotation;
  }


  @Override
  @Nullable
  public List<DAParameter> extractParameters(ExecutableElement methodElement) {
    if (methodElement.getParameters() == null) {
      return null;
    }

    return from(methodElement.getParameters())
        .transform(new Function<VariableElement, DAParameter>() {
          @Nullable
          @Override
          public DAParameter apply(@Nullable VariableElement o) {
            return DAParameter.builder(JavaxDANameFactory.from(o.getSimpleName()), extractType(o.asType()))
                              .withModifiers(from(o.getModifiers()).transform(toDAModifier()).toSet())
                              .withAnnotations(extractDAAnnotations(o))
                              .build();
          }
        }
        )
        .filter(notNull())
        .toList();
  }

  private static enum ModifierToDAModifier implements Function<Modifier, DAModifier> {
    INSTANCE;

    @Nonnull
    @Override
    public DAModifier apply(@Nonnull Modifier modifier) {
      return DAModifier.valueOf(modifier.name());
    }
  }

  @Override
  @Nullable
  public DAName extractSimpleName(TypeMirror type, Element element) {
    if (type.getKind().isPrimitive()) {
      return DANameFactory.fromPrimitiveKind(TypeKindToDATypeKind.INSTANCE.apply(type.getKind()));
    }
    if (type.getKind() == TypeKind.WILDCARD) {
      // wildward types do not have a name nor qualified name
      return null;
    }
    return JavaxDANameFactory.from(element.getSimpleName());
  }

  @Nullable
  private DAName extractQualifiedName(TypeMirror type, Element element) {
    if (type.getKind().isPrimitive()) {
      // primitive types do not have a qualifiedName by definition
      return null;
    }
    if (element instanceof QualifiedNameable) {
      return JavaxDANameFactory.from(((QualifiedNameable) element).getQualifiedName());
    }
    return null;
  }

  @Nullable
  @Override
  public List<DAEnumValue> extractEnumValues(@Nonnull TypeElement classElement) {
    if (classElement.getKind() != ElementKind.ENUM) {
      return null;
    }

    return from(classElement.getEnclosedElements())
        // enum values are VariableElement with kind=Kind.ENUM_CONSTANT
        .filter(
            Predicates.compose(
                Predicates.equalTo(ElementKind.ENUM_CONSTANT),
                new Function<Element, ElementKind>() {
                  @Nonnull
                  @Override
                  public ElementKind apply(@Nonnull Element o) {
                    return o.getKind();
                  }
                }
            )
        )
        .filter(VariableElement.class)
        .transform(new Function<VariableElement, DAEnumValue>() {
          @Nonnull
          @Override
          public DAEnumValue apply(@Nonnull VariableElement o) {
            return new DAEnumValue(o.getSimpleName().toString());
          }
        }
        )
        .toList();
  }

  @Nullable
  @Override
  public List<DAAnnotation> extractDAAnnotations(@Nullable TypeElement classElement) {
    if (classElement == null || classElement.getAnnotationMirrors() == null) {
      return null;
    }

    return from(classElement.getAnnotationMirrors()).transform(toDAAnnotation()).filter(notNull()).toList();
  }

  @Nullable
  @Override
  public List<DAAnnotation> extractDAAnnotations(@Nullable Element methodElement) {
    if (methodElement == null || methodElement.getAnnotationMirrors() == null) {
      return null;
    }
    return from(methodElement.getAnnotationMirrors()).transform(toDAAnnotation()).filter(notNull()).toList();
  }

}
