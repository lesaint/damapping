package fr.phan.damapping.processor.impl.javaxparsing;

import fr.phan.damapping.processor.model.DAEnumValue;
import fr.phan.damapping.processor.model.DAModifier;
import fr.phan.damapping.processor.model.DAName;
import fr.phan.damapping.processor.model.DAParameter;
import fr.phan.damapping.processor.model.DAType;
import fr.phan.damapping.processor.model.DATypeKind;
import fr.phan.damapping.processor.model.factory.DANameFactory;
import fr.phan.damapping.processor.model.factory.DATypeFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
import com.google.common.base.Function;
import com.google.common.base.Predicates;

import static com.google.common.collect.FluentIterable.from;

/**
 * JavaxExtractor -
 *
 * @author: Sébastien Lesaint
 */
public class JavaxExtractorImpl implements JavaxExtractor {
    private final Types typeUtils;

    public JavaxExtractorImpl(Types typeUtils) {
        this.typeUtils = typeUtils;
    }

    @Override
    @Nonnull
    public DAType extractType(TypeMirror type) {
        Element element = typeUtils.asElement(type);
        if (type.getKind() == TypeKind.ARRAY) {
            element = typeUtils.asElement(((ArrayType) type).getComponentType());
        }

        return extractType(type, element);
    }

    @Override
    @Nonnull
    public DAType extractType(TypeMirror type, Element element) {
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
                })
                .filter(Predicates.notNull())
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
        return ModifierToDAMoifier.INSTANCE;
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
                                .build();
                    }
                })
                .filter(Predicates.notNull())
                .toList();
    }

    private static enum ModifierToDAMoifier implements Function<Modifier, DAModifier> {
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

    @Override
    @Nullable
    public DAName extractQualifiedName(TypeMirror type, Element element) {
        if (type.getKind().isPrimitive()) {
            // primitive types do not have a qualifiedName by definition
            return null;
        }
        if (element instanceof QualifiedNameable) {
            return JavaxDANameFactory.from(((QualifiedNameable) element).getQualifiedName());
        }
        return null;
    }

    @Override
    @Nullable
    public DAName extractQualifiedName(DeclaredType o) {
        if (o.asElement() instanceof QualifiedNameable) {
            return JavaxDANameFactory.from(((QualifiedNameable) o.asElement()).getQualifiedName());
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
                })
                .toList();
    }
}
