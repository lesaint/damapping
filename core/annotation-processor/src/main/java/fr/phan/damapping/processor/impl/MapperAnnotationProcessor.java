/*
 * Copyright 2013 Sébastien Lesaint
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.phan.damapping.processor.impl;

import fr.phan.damapping.annotation.Mapper;
import fr.phan.damapping.annotation.MapperFactoryMethod;
import fr.phan.damapping.processor.impl.filegenerator.*;
import fr.phan.damapping.processor.model.factory.DANameFactory;
import fr.phan.damapping.processor.model.factory.DATypeFactory;
import fr.phan.damapping.processor.model.DAInterface;
import fr.phan.damapping.processor.model.DAMethod;
import fr.phan.damapping.processor.model.DAName;
import fr.phan.damapping.processor.model.DAParameter;
import fr.phan.damapping.processor.model.DASourceClass;
import fr.phan.damapping.processor.model.DAType;
import fr.phan.damapping.processor.model.InstantiationType;
import fr.phan.damapping.processor.model.predicate.DAMethodPredicates;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static com.google.common.collect.FluentIterable.from;

/**
 * MapperAnnotationProcessor -
 *
 * @author lesaint
 */
public class MapperAnnotationProcessor extends AbstractAnnotationProcessor<Mapper> {

    private static final Set<ElementKind> SUPPORTED_ELEMENTKINDS = ImmutableSet.of(
            ElementKind.CLASS, ElementKind.ENUM
    );
    private static final Set<InstantiationType> MAPPER_FACTORY_CLASS_INTANTIATIONTYPES =
            ImmutableSet.of(InstantiationType.CONSTRUCTOR, InstantiationType.SINGLETON_ENUM);
    private static final Set<InstantiationType> MAPPER_FACTORY_INTERFACE_INTANTIATIONTYPES =
            ImmutableSet.of(InstantiationType.CONSTRUCTOR_FACTORY, InstantiationType.STATIC_FACTORY);

    public MapperAnnotationProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv, Mapper.class);
    }

    @Override
    protected void process(Element element, RoundEnvironment roundEnv) throws IOException {
        if (!SUPPORTED_ELEMENTKINDS.contains(element.getKind())) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                   String.format(
                           "Type %s annoted with @Mapper annotation is not a class nor an enum (kind found = %s)",
                           element, element.getKind()
                   )
            );
            return;
        }

        TypeElement classElement = (TypeElement) element;

//        System.out.println("Processing " + classElement.getQualifiedName() + " in " + getClass().getCanonicalName());

        // retrieve names of the class with @Mapper
        DASourceClass.Builder daSourceClassBuilder = DASourceClass.builder(classElement, extractType((DeclaredType) classElement.asType()));

        // retrieve name of the package of the class with @Mapper
        daSourceClassBuilder.withPackageName(retrievePackageName(classElement));

        daSourceClassBuilder.withModifiers(classElement.getModifiers());

        // retrieve interfaces implemented (directly and if any) by the class with @Mapper (+ their generics)
        // chercher si l'une d'elles est Function (Guava)
        List<DAInterface> interfaces = retrieveInterfaces(classElement);
        daSourceClassBuilder.withInterfaces(interfaces);

        // pour le moment, on ne traite pas les classes abstraites implémentées par la class @Mapper ni les interfaces
        // implémentées indirectement

        List<DAMethod> methods = retrieveMethods(classElement);
        daSourceClassBuilder.withMethods(methods);
        daSourceClassBuilder.withInstantiationType(computeInstantiationType(classElement, methods));
        DASourceClass daSourceClass = daSourceClassBuilder.build();

        try {
            DASourceClassChecker checker = new DASourceClassCheckerImpl();
            checker.check(daSourceClass);
        }
        catch (CheckError e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage(), classElement);
            return;
        }

        DefaultFileGeneratorContext context = new DefaultFileGeneratorContext(daSourceClass);

        // 1 - générer l'interface du Mapper
        generateMapper(context);

        // 2 - générer la factory interface (si @MapperFactoryMethod)
        generateMapperFactoryInterface(context);
        generateMapperFactoryImpl(context);

        // 3 - generer la factory class (si pas de @MapperFactoryMethod)
        generateMapperFactoryClass(context);

        // 3 - générer l'implémentation du Mapper
        generateMapperImpl(context);
    }

    private void generateFile(FileGenerator fileGenerator,
                              FileGeneratorContext context) throws IOException {

        JavaFileObject jfo = processingEnv.getFiler().createSourceFile(
                fileGenerator.fileName(context),
                context.getSourceClass().getClassElement()
        );
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "generating " + jfo.toUri());

        fileGenerator.writeFile(new BufferedWriter(jfo.openWriter()), context);
    }

    private void generateMapper(FileGeneratorContext context) throws IOException {
        generateFile(new MapperFileGenerator(), context);
    }

    private void generateMapperFactoryClass(FileGeneratorContext context) throws IOException {
        if (shouldGenerateMapperFactoryClass(context)) {
            generateFile(new MapperFactoryClassFileGenerator(), context);
        }
    }

    private boolean shouldGenerateMapperFactoryClass(FileGeneratorContext context) {
        return MAPPER_FACTORY_CLASS_INTANTIATIONTYPES.contains(context.getSourceClass().getInstantiationType());
    }

    private void generateMapperFactoryInterface(FileGeneratorContext context) throws IOException {
        if (shouldGenerateMapperFactoryInterface(context)) {
            generateFile(new MapperFactoryInterfaceFileGenerator(), context);
        }
    }

    private void generateMapperFactoryImpl(FileGeneratorContext context) throws IOException {
        if (shouldGenerateMapperFactoryInterface(context)) {
            generateFile(new MapperFactoryImplFileGenerator(), context);
        }
    }

    private void generateMapperImpl(FileGeneratorContext context) throws IOException {
        if (!shouldGenerateMapperFactoryInterface(context)) {
            generateFile(new MapperImplFileGenerator(), context);
        }
    }

    private boolean shouldGenerateMapperFactoryInterface(FileGeneratorContext context) {
        return MAPPER_FACTORY_INTERFACE_INTANTIATIONTYPES.contains(context.getSourceClass().getInstantiationType());
    }

    @Nonnull
    private List<DAMethod> retrieveMethods(final TypeElement classElement) {
        if (classElement.getEnclosedElements() == null) {
            return Collections.emptyList();
        }

        return from(classElement.getEnclosedElements())
                // methods are ExecutableElement
                .filter(Predicates.instanceOf(ExecutableElement.class))
                // transform
                .transform(new Function<Element, DAMethod>() {
                    @Nullable
                    @Override
                    public DAMethod apply(@Nullable Element o) {
                        if (o == null) {
                            return null;
                        }

                        ExecutableElement methodElement = (ExecutableElement) o;
                        DAMethod.Builder res = DAMethod.builder(o.getKind())
                                .withModifiers(extractModifiers(methodElement))
                                .withParameters(extractParameters(methodElement))
                                .withMapperMethod(isMapperMethod(methodElement))
                                .withMapperFactoryMethod(isMapperFactoryMethod(methodElement));
                        if (o.getKind() == ElementKind.CONSTRUCTOR) {
                            res.withName(DANameFactory.from(StringUtils.uncapitalize(classElement.getSimpleName().toString())));
                            res.withReturnType(extractType(classElement.asType()));
                        }
                        else {
                            res.withName(DANameFactory.from(o.getSimpleName()));
                            res.withReturnType(extractReturnType(methodElement));
                        }
                        return res.build();
                    }
                })
                .filter(Predicates.notNull())
                .toList();
    }

    @Nonnull
    private Set<Modifier> extractModifiers(ExecutableElement methodElement) {
        if (methodElement.getModifiers() == null) {
            return Collections.emptySet();
        }
        return methodElement.getModifiers();
    }

    @Nonnull
    private DAType extractReturnType(ExecutableElement methodElement) {
        return extractType(methodElement.getReturnType());
    }

    private boolean isMapperMethod(ExecutableElement methodElement) {
        // TODO implementer isMapperMethod si on ajoute une annotation MapperMethod
        return false;
    }

    private boolean isMapperFactoryMethod(ExecutableElement methodElement) {
        Optional<AnnotationMirror> annotationMirror = getAnnotationMirror(methodElement, MapperFactoryMethod.class);
        return annotationMirror.isPresent();
    }

    private List<DAParameter> extractParameters(ExecutableElement methodElement) {
        if (methodElement.getParameters() == null) {
            return null;
        }

        return from(methodElement.getParameters())
                .transform(new Function<VariableElement, DAParameter>() {
                    @Nullable
                    @Override
                    public DAParameter apply(@Nullable VariableElement o) {
                        return DAParameter.builder(DANameFactory.from(o.getSimpleName()), extractType(o.asType()))
                                .withModifiers(o.getModifiers())
                                .build();
                    }
                })
                .filter(Predicates.notNull())
                .toList();
    }

    private List<DAInterface> retrieveInterfaces(final TypeElement classElement) {
        List<? extends TypeMirror> interfaces = classElement.getInterfaces();
        if (interfaces == null) {
            return Collections.emptyList();
        }

        return from(interfaces).transform(new Function<TypeMirror, DAInterface>() {
            @Nullable
            @Override
            public DAInterface apply(@Nullable TypeMirror o) {
                // TOIMPROVE : le filtrage des interfaces de la classe annotée avec @Mapper sur DeclaredType est-il pertinent ?
                if (!(o instanceof DeclaredType)) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "Interface is not a DeclaredType, not supported", classElement
                    );
                    return null;
                }

                return new DAInterface(extractType(o));
            }
        }).filter(Predicates.notNull()).toList();
    }

    private List<DAType> extractTypeArgs(TypeMirror typeMirror) {
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

    @Nonnull
    private DAType extractType(TypeMirror type) {
        Types typeUtils = processingEnv.getTypeUtils();
        Element element = typeUtils.asElement(type);
        if (type.getKind() == TypeKind.ARRAY) {
            element = typeUtils.asElement(((ArrayType) type).getComponentType());
        }

        return extractType(type, element);
    }

    @Nonnull
    private DAType extractType(TypeMirror type, Element element) {
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

    private DAType extractWildcardType(WildcardType wildcardType) {
        if (wildcardType.getSuperBound() != null) {
            return DATypeFactory.wildcardWithSuperBound(extractType(wildcardType.getSuperBound()));
        }
        if (wildcardType.getExtendsBound() != null) {
            return DATypeFactory.wildcardWithExtendsBound(extractType(wildcardType.getExtendsBound()));
        }
        throw new IllegalArgumentException("Unsupported WildcardType has neither superbound nor extends bound");
    }

    private static DAName extractSimpleName(TypeMirror type, Element element) {
        if (type.getKind().isPrimitive()) {
            return DANameFactory.fromPrimitiveKind(type.getKind());
        }
        if (type.getKind() == TypeKind.WILDCARD) {
            // wildward types do not have a name nor qualified name
            return null;
        }
        return DANameFactory.from(element.getSimpleName());
    }

    private static DAName extractQualifiedName(TypeMirror type, Element element) {
        if (type.getKind().isPrimitive()) {
            // primitive types do not have a qualifiedName by definition
            return null;
        }
        if (element instanceof QualifiedNameable) {
            return DANameFactory.from(((QualifiedNameable) element).getQualifiedName());
        }
        return null;
    }

    private static DAName extractQualifiedName(DeclaredType o) {
        if (o.asElement() instanceof QualifiedNameable) {
            return DANameFactory.from(((QualifiedNameable) o.asElement()).getQualifiedName());
        }
        return null;
    }

    private static DAName retrievePackageName(TypeElement classElement) {
        PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();
        return DANameFactory.from(packageElement.getQualifiedName());
    }

    private static InstantiationType computeInstantiationType(TypeElement classElement, List<DAMethod> methods) {
        Optional<DAMethod> mapperFactoryConstructor = from(methods)
                .filter(DAMethodPredicates.isConstructor())
                .filter(DAMethodPredicates.isMapperFactoryMethod())
                .first();
        if (mapperFactoryConstructor.isPresent()) {
            return InstantiationType.CONSTRUCTOR_FACTORY;
        }

        Optional<DAMethod> mapperFactoryStaticMethods = from(methods)
                .filter(DAMethodPredicates.isStatic())
                .filter(DAMethodPredicates.isMapperFactoryMethod())
                .first();
        if (mapperFactoryStaticMethods.isPresent()) {
            return InstantiationType.STATIC_FACTORY;
        }

        if (classElement.getKind() == ElementKind.ENUM) {
            return InstantiationType.SINGLETON_ENUM;
        }

        Optional<AnnotationMirror> annotationMirror = getAnnotationMirror(classElement, Component.class);
        if (annotationMirror.isPresent()) {
            return InstantiationType.SPRING_COMPONENT;
        }
        return InstantiationType.CONSTRUCTOR;
    }

    private static Optional<AnnotationMirror> getAnnotationMirror(Element classElement,
                                                                  Class<? extends Annotation> annotationClass) {
        for (AnnotationMirror annotationMirror : classElement.getAnnotationMirrors()) {
            if (annotationClass.getCanonicalName().equals(annotationMirror.getAnnotationType().toString())) { // TODO put test to identify AnnotationMirror by Class into a Predicate
                return Optional.of(annotationMirror);
            }
        }
        return Optional.absent();
    }

    private static String getEnumNameElementValue(AnnotationMirror annotationMirror, String elementName) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> elementValue : annotationMirror.getElementValues().entrySet()) {
            if (elementName.equals(elementValue.getKey().getSimpleName().toString())) {
                // VariableElement is the type return by getValue() representing an enum constant (see @AnnotationValue)
                VariableElement variableElement = (VariableElement) elementValue.getValue().getValue();
                return variableElement.getSimpleName().toString();
            }
        }
        return null;
    }

}
