package fr.phan.damapping.processor.impl.javaxparsing;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import fr.phan.damapping.processor.model.DAModifier;
import fr.phan.damapping.processor.model.DAName;
import fr.phan.damapping.processor.model.DAParameter;
import fr.phan.damapping.processor.model.DAType;
import fr.phan.damapping.processor.model.factory.DANameFactory;
import fr.phan.damapping.processor.model.factory.DATypeFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.Types;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
        DAType.Builder builder = DAType.builder(type.getKind(), extractSimpleName(type, element))
                .withQualifiedName(extractQualifiedName(type, element))
                .withTypeArgs(extractTypeArgs(type));
        return builder.build();
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
                        return DAParameter.builder(DANameFactory.from(o.getSimpleName()), extractType(o.asType()))
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
            return DANameFactory.fromPrimitiveKind(type.getKind());
        }
        if (type.getKind() == TypeKind.WILDCARD) {
            // wildward types do not have a name nor qualified name
            return null;
        }
        return DANameFactory.from(element.getSimpleName());
    }

    @Override
    @Nullable
    public DAName extractQualifiedName(TypeMirror type, Element element) {
        if (type.getKind().isPrimitive()) {
            // primitive types do not have a qualifiedName by definition
            return null;
        }
        if (element instanceof QualifiedNameable) {
            return DANameFactory.from(((QualifiedNameable) element).getQualifiedName());
        }
        return null;
    }

    @Override
    @Nullable
    public DAName extractQualifiedName(DeclaredType o) {
        if (o.asElement() instanceof QualifiedNameable) {
            return DANameFactory.from(((QualifiedNameable) o.asElement()).getQualifiedName());
        }
        return null;
    }
}
