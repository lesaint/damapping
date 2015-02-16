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

import fr.javatronic.damapping.processor.impl.javaxparsing.generics.DeclaredTypeArgument;
import fr.javatronic.damapping.processor.impl.javaxparsing.generics.GenericTypeContext;
import fr.javatronic.damapping.processor.impl.javaxparsing.generics.GenericTypeContextImpl;
import fr.javatronic.damapping.processor.impl.javaxparsing.generics.SelfTypeArgument;
import fr.javatronic.damapping.processor.impl.javaxparsing.generics.TypeArgument;
import fr.javatronic.damapping.processor.impl.javaxparsing.model.JavaxDAAnnotation;
import fr.javatronic.damapping.processor.impl.javaxparsing.visitor.QualifiedNameExtractor;
import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.DAAnnotationMember;
import fr.javatronic.damapping.processor.model.DAEnumValue;
import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.DAParameter;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.DATypeKind;
import fr.javatronic.damapping.processor.model.factory.DANameFactory;
import fr.javatronic.damapping.processor.model.factory.DATypeFactory;
import fr.javatronic.damapping.processor.model.impl.DAAnnotationImpl;
import fr.javatronic.damapping.processor.model.impl.DAAnnotationMemberImpl;
import fr.javatronic.damapping.processor.model.impl.DAEnumValueImpl;
import fr.javatronic.damapping.processor.model.impl.DAParameterImpl;
import fr.javatronic.damapping.processor.model.impl.DATypeImpl;
import fr.javatronic.damapping.util.Function;
import fr.javatronic.damapping.util.Optional;
import fr.javatronic.damapping.util.Predicate;
import fr.javatronic.damapping.util.Predicates;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.SimpleTypeVisitor6;

import static fr.javatronic.damapping.processor.impl.javaxparsing.visitor.QualifiedDANameExtractor.QUALIFIED_DANAME_EXTRACTOR;
import static fr.javatronic.damapping.util.FluentIterable.from;
import static fr.javatronic.damapping.util.Preconditions.checkNotNull;
import static fr.javatronic.damapping.util.Predicates.equalTo;
import static fr.javatronic.damapping.util.Predicates.notNull;


/**
 * JavaxExtractorImpl - This implementation of {@link JavaxExtractor} supports creating {@link DAType} from Element with
 * unresolved references (ie. {@link TypeMirror} with type {@link TypeKind#ERROR}) using the fixed resolution provided
 * by a {@link ReferenceScanResult} instance.
 * <p>
 * If an unresolved references has no fix provided by the {@link ReferenceScanResult} object, an
 * {@link IllegalStateException} will be raised: <strong>no {@link fr.javatronic.damapping.processor.model.impl
 * .DATypeImpl} is supposed to be built unless all
 * references are valid or fixed</strong>.
 * </p>
 *
 * @author Sébastien Lesaint
 */
public class JavaxExtractorImpl implements JavaxExtractor {
  private static final Predicate<Element> NOT_AN_ENUM_CONSTANT =
      Predicates.compose(equalTo(ElementKind.ENUM_CONSTANT),ElementToElementKind.INSTANCE);

  private final AnnotationValueEntryToDAAnnotationMember annotationValueEntryToDAAnnotationMember =
      new AnnotationValueEntryToDAAnnotationMember();

  private final Function<AnnotationMirror, DAAnnotation> annotationMirrorToDAAnnotation =
      new AnnotationMirrorToDAAnnotation();
  private final DATypeExtractor daTypeExtractor = new DATypeExtractor();
  private final TypeArgsExtractor typeArgsExtractor = new TypeArgsExtractor();

  @Nonnull
  private final ProcessingEnvironmentWrapper processingEnv;
  @Nonnull
  private final ReferenceScanResult scanResult;

  public JavaxExtractorImpl(@Nonnull ProcessingEnvironmentWrapper processingEnv,
                            @Nonnull ReferenceScanResult scanResult) {
    this.processingEnv = checkNotNull(processingEnv);
    this.scanResult = checkNotNull(scanResult);
  }

  @Override
  @Nonnull
  public DAType extractType(TypeMirror typeMirror) {
    return extractType(typeMirror, GenericTypeContextImpl.emptyContext());
  }

  @Override
  @Nonnull
  public DAType extractType(@Nonnull TypeMirror typeMirror, @Nonnull GenericTypeContext genericTypeContext) {
    checkNotNull(genericTypeContext);
    return typeMirror.accept(daTypeExtractor, genericTypeContext);
  }

  /**
   * Look for a DAType for the specified Element of the TypeMirror of kind ERROR.
   * <p>
   * <ol>
   * <li>look for the fixed reference in the current {@link ReferenceScanResult} for the qualified reference in
   * Element</li>
   * <li>look for the fixed reference in the current {@link ReferenceScanResult} for the import (explicit or
   * implicit) with the simpleName of the specified Element</li>
   * <li>if neither search returned a DAType, throw a {@link IllegalStateException}, this is not supposed to happen</li>
   * </ol>
   * </p>
   *
   * @param element a {@link Element}
   *
   * @return a {@link DAType}
   *
   * @throws IllegalStateException if no {@link DAType} can be found
   */
  @Nonnull
  private DAType findFixedResolution(Element element) {
    Name qualifiedName = element.accept(QualifiedNameExtractor.QUALIFIED_NAME_EXTRACTOR, null);
    // qualified reference to Type in code
    if (qualifiedName != null && !qualifiedName.contentEquals(element.getSimpleName())) {
      return ensureNonnull(scanResult.findFixedByQualifiedName(qualifiedName.toString()), element);
    }

    Optional<String> importQualifiedName = scanResult.getImports().findBySimpleName(element.getSimpleName());
    if (!importQualifiedName.isPresent()) {
      throw new IllegalStateException("Type for Element " + element + " is neither imported nor explicitly qualified");
    }

    return ensureNonnull(scanResult.findFixedByQualifiedName(importQualifiedName.get()), element);
  }

  @Nonnull
  private DAType extractArrayType(ArrayType arrayType, GenericTypeContext parentContext) {
    TypeMirror componentType = arrayType.getComponentType();
    DATypeKind daTypeKind = TypeKindToDATypeKind.INSTANCE.apply(componentType.getKind());
    GenericTypeContext genericTypeContext =
        componentType instanceof DeclaredType ? parentContext.subContext((DeclaredType) componentType) : parentContext;
    DATypeImpl.Builder builder = DATypeImpl.arrayBuilder(daTypeKind, extractSimpleName(componentType))
                                           .withQualifiedName(extractQualifiedName(componentType))
                                           .withTypeArgs(extractTypeArgs(componentType, genericTypeContext));
    return builder.build();
  }

  private DAType ensureNonnull(Optional<DAType> fixedByQualifiedName, Element element) {
    if (!fixedByQualifiedName.isPresent()) {
      IllegalArgumentException illegalArgumentException = new IllegalArgumentException(
          "Can not find resolved Element for element " + element
      );
      illegalArgumentException.printStackTrace();
      throw illegalArgumentException;
    }
    return fixedByQualifiedName.get();
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
  public List<DAType> extractTypeArgs(TypeMirror typeMirror, GenericTypeContext genericTypeContext) {
    return typeMirror.accept(typeArgsExtractor, genericTypeContext);
  }

  @Override
  @Nonnull
  public DAType extractReturnType(ExecutableElement methodElement, GenericTypeContext genericTypeContext) {
    return extractType(methodElement.getReturnType(), genericTypeContext);
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
  public List<DAParameter> extractParameters(ExecutableElement methodElement, GenericTypeContext genericTypeContext) {
    if (methodElement.getParameters() == null) {
      return null;
    }

    return from(methodElement.getParameters())
        .transform(new VariableElementToDAParameter(genericTypeContext))
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
  public DAName extractSimpleName(TypeMirror type) {
    if (type.getKind().isPrimitive()) {
      return DANameFactory.fromPrimitiveKind(TypeKindToDATypeKind.INSTANCE.apply(type.getKind()));
    }

    if (type.getKind() == TypeKind.WILDCARD) {
      // wildward types do not have a name nor qualified name
      return null;
    }

    Element element = processingEnv.getTypeUtils().asElement(type);
    if (element == null) {
      return null;
    }
    return JavaxDANameFactory.from(element.getSimpleName());
  }

  @Nullable
  private DAName extractQualifiedName(@Nonnull TypeMirror type) {
    Element element = processingEnv.getTypeUtils().asElement(type);
    if (element == null) {
      return null;
    }

    return element.accept(QUALIFIED_DANAME_EXTRACTOR, null);
  }

  @Nullable
  @Override
  public List<DAEnumValue> extractEnumValues(@Nonnull TypeElement classElement) {
    if (classElement.getKind() != ElementKind.ENUM) {
      return null;
    }

    return from(classElement.getEnclosedElements())
        .filter(VariableElement.class)
         // enum values are VariableElement with kind=Kind.ENUM_CONSTANT
        .filter(NOT_AN_ENUM_CONSTANT)
        .transform(VariableElementToDAEnumValue.INSTANCE)
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

  private static enum ElementToElementKind implements Function<Element, ElementKind> {
    INSTANCE;

    @Nonnull
    @Override
    public ElementKind apply(@Nonnull Element o) {
      return o.getKind();
    }
  }

  private static enum VariableElementToDAEnumValue implements Function<VariableElement, DAEnumValue> {
    INSTANCE;

    @Nonnull
    @Override
    public DAEnumValue apply(@Nonnull VariableElement o) {
      return new DAEnumValueImpl(o.getSimpleName().toString());
    }
  }

  private class AnnotationMirrorToDAAnnotation implements Function<AnnotationMirror, DAAnnotation> {
    @Nullable
    @Override
    public DAAnnotation apply(@Nullable AnnotationMirror input) {
      if (input == null) {
        return null;
      }
      DeclaredType annotationType = input.getAnnotationType();
      DAType daType = extractType(annotationType);
      DAAnnotationImpl daAnnotation = new DAAnnotationImpl(daType,
          toDAAnnotations(daType, annotationType.asElement().getAnnotationMirrors()),
          toDAAnnotationMembers(input.getElementValues())
      );
      return new JavaxDAAnnotation(daAnnotation, input);
    }

    @Nullable
    private List<DAAnnotationMember> toDAAnnotationMembers(
        Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues) {
      if (elementValues == null || elementValues.isEmpty()) {
        return null;
      }

      return from(elementValues.entrySet()).transform(annotationValueEntryToDAAnnotationMember).toList();
    }

    @Nullable
    private List<DAAnnotation> toDAAnnotations(@Nonnull DAType daType,
                                               @Nullable List<? extends AnnotationMirror> annotationMirrors) {
      if (annotationMirrors == null || annotationMirrors.isEmpty()) {
        return null;
      }
      // no need to return the annotations on the Java language annotations, especially because some of them (e.g.
      // Documented) are recursive.
      if (daType.getQualifiedName() != null
          && daType.getQualifiedName().getName().startsWith("java.lang.annotation.")) {
        return null;
      }
      return from(annotationMirrors).transform(annotationMirrorToDAAnnotation).toList();
    }
  }

  private class VariableElementToDAParameter implements Function<VariableElement, DAParameter> {
    private final GenericTypeContext genericTypeContext;

    private VariableElementToDAParameter(GenericTypeContext genericTypeContext) {
      this.genericTypeContext = genericTypeContext;
    }

    @Nullable
    @Override
    public DAParameter apply(@Nullable VariableElement o) {
      DAName simpleName = JavaxDANameFactory.from(o.getSimpleName());
      DAType type = extractType(o.asType(), genericTypeContext);
      return DAParameterImpl.builder(simpleName, type)
                            .withModifiers(from(o.getModifiers()).transform(toDAModifier()).toSet())
                            .withAnnotations(extractDAAnnotations(o))
                            .build();
    }
  }

  private class AnnotationValueEntryToDAAnnotationMember
      implements Function<Map.Entry<? extends ExecutableElement, ? extends AnnotationValue>, DAAnnotationMember> {

    @Nullable
    @Override
    public DAAnnotationMember apply(
        @Nullable Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry) {
      if (entry == null) {
        return null;
      }

      return new DAAnnotationMemberImpl(
          entry.getKey().getSimpleName().toString(),
          extractType(entry.getKey().getReturnType()),
          entry.getValue().toString()
      );
    }
  }

  private class DATypeExtractor extends SimpleTypeVisitor6<DAType, GenericTypeContext> {

    private final Map<DeclaredType, DAType> daTypeCache = new HashMap<>();

    @Override
    public DAType visitError(ErrorType errorType, GenericTypeContext genericTypeContext) {
      return findFixedResolution(processingEnv.getTypeUtils().asElement(errorType));
    }

    @Override
    public DAType visitNoType(NoType noType, GenericTypeContext genericTypeContext) {
      return DATypeFactory.voidDaType();
    }

    @Override
    public DAType visitWildcard(WildcardType wildcardType, GenericTypeContext genericTypeContext) {
      return extractWildcardType(wildcardType);
    }

    @Override
    public DAType visitArray(ArrayType arrayType, GenericTypeContext genericTypeContext) {

      return extractArrayType(arrayType, genericTypeContext);
    }

    @Override
    public DAType visitTypeVariable(TypeVariable t, GenericTypeContext genericTypeContext) {
      Name simpleName = t.asElement().getSimpleName();
      TypeArgument daType = genericTypeContext.lookup(simpleName);
      if (daType == SelfTypeArgument.SELF) {
        return DATypeImpl.SelfDAType.SELF;
      }
      if (daType instanceof DeclaredTypeArgument) {
        return extractType(((DeclaredTypeArgument) daType).getDeclaredType(), genericTypeContext);
      }
      return DATypeImpl.typeBuilder(DATypeKind.TYPEVAR, JavaxDANameFactory.from(simpleName)).build();
    }

    @Override
    public DAType visitDeclared(DeclaredType type, GenericTypeContext parentContext) {
      DAType res = daTypeCache.get(type);
      if (res != null) {
        return res;
      }

      DATypeImpl.Builder builder = DATypeImpl
          .typeBuilder(
              TypeKindToDATypeKind.INSTANCE.apply(type.getKind()),
              extractSimpleName(type)
          )
          .withQualifiedName(extractQualifiedName(type))
          .withTypeArgs(extractTypeArgs(type, parentContext.subContext(type)));
      DAType daType = builder.build();
      daTypeCache.put(type, daType);
      return daType;
    }

    @Override
    public DAType visitPrimitive(PrimitiveType t, GenericTypeContext genericTypeContext) {
      DATypeImpl.Builder builder = DATypeImpl
          .typeBuilder(
              TypeKindToDATypeKind.INSTANCE.apply(t.getKind()),
              extractSimpleName(t)
          );
      return builder.build();
    }

    @Override
    protected DAType defaultAction(TypeMirror e, GenericTypeContext typeParameterMap) {
      throw new IllegalArgumentException("TypeMirror " + extractSimpleName(e) + " can not be converted to a DAType");
    }
  }

  private class TypeArgsExtractor extends SimpleTypeVisitor6<List<DAType>, GenericTypeContext> {
    public TypeArgsExtractor() {
      super(Collections.<DAType>emptyList());
    }

    @Override
    public List<DAType> visitDeclared(DeclaredType declaredType, final GenericTypeContext genericTypeContext) {
      final List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
      if (typeArguments == null || typeArguments.isEmpty()) {
        return Collections.emptyList();
      }

      return from(typeArguments)
          .transform(
              new Function<TypeMirror, DAType>() {
                @Nullable
                @Override
                public DAType apply(@Nullable TypeMirror o) {
                  if (o == null) {
                    return null;
                  }

                  return extractType(o, genericTypeContext);
                }
              }
          )
          .filter(notNull())
          .toList();
    }
  }

}
