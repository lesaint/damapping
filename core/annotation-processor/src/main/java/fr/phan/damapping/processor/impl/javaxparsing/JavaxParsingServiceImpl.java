package fr.phan.damapping.processor.impl.javaxparsing;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import fr.phan.damapping.annotation.MapperFactoryMethod;
import fr.phan.damapping.processor.model.*;
import fr.phan.damapping.processor.model.factory.DANameFactory;
import fr.phan.damapping.processor.model.factory.DATypeFactory;
import fr.phan.damapping.processor.model.predicate.DAMethodPredicates;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.FluentIterable.from;

/**
 * JavaxParsingService -
 *
 * @author: Sébastien Lesaint
 */
public class JavaxParsingServiceImpl implements JavaxParsingService {
    private final ProcessingEnvironment processingEnv;
    private final JavaxExtractor javaxExtractor;
    private final JavaxUtil javaxUtil;

    public JavaxParsingServiceImpl(ProcessingEnvironment processingEnv,
                                   JavaxExtractor javaxExtractor,
                                   JavaxUtil javaxUtil) {
        this.processingEnv = processingEnv;
        this.javaxExtractor = javaxExtractor;
        this.javaxUtil = javaxUtil;
    }

    public JavaxParsingServiceImpl(ProcessingEnvironment processingEnv) {
        this(processingEnv, new JavaxExtractorImpl(processingEnv.getTypeUtils()), new JavaxUtilImpl());
    }

    @Nonnull
    public DASourceClass parse(TypeElement classElement) {
        // retrieve names of the class with @Mapper
        DASourceClass.Builder daSourceClassBuilder = DASourceClass.builder(classElement,
                javaxExtractor.extractType((DeclaredType) classElement.asType()));

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
        return daSourceClassBuilder.build();
    }

    @Override
    @Nonnull
    public List<DAMethod> retrieveMethods(final TypeElement classElement) {
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
                                .withModifiers(javaxExtractor.extractModifiers(methodElement))
                                .withParameters(javaxExtractor.extractParameters(methodElement))
                                .withMapperMethod(isMapperMethod(methodElement))
                                .withMapperFactoryMethod(isMapperFactoryMethod(methodElement));
                        if (o.getKind() == ElementKind.CONSTRUCTOR) {
                            res.withName(DANameFactory.from(StringUtils.uncapitalize(classElement.getSimpleName().toString())));
                            res.withReturnType(javaxExtractor.extractType(classElement.asType()));
                        } else {
                            res.withName(DANameFactory.from(o.getSimpleName()));
                            res.withReturnType(javaxExtractor.extractReturnType(methodElement));
                        }
                        return res.build();
                    }
                })
                .filter(Predicates.notNull())
                .toList();
    }

    private boolean isMapperMethod(ExecutableElement methodElement) {
        // TODO implementer isMapperMethod si on ajoute une annotation MapperMethod
        return false;
    }

    private boolean isMapperFactoryMethod(ExecutableElement methodElement) {
        Optional<AnnotationMirror> annotationMirror = javaxUtil.getAnnotationMirror(methodElement, MapperFactoryMethod.class);
        return annotationMirror.isPresent();
    }

    @Override
    public List<DAInterface> retrieveInterfaces(final TypeElement classElement) {
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

                return new DAInterface(javaxExtractor.extractType(o));
            }
        }).filter(Predicates.notNull()).toList();
    }

    @Override
    public DAName retrievePackageName(TypeElement classElement) {
        PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();
        return DANameFactory.from(packageElement.getQualifiedName());
    }

    @Override
    public InstantiationType computeInstantiationType(TypeElement classElement, List<DAMethod> methods) {
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

        Optional<AnnotationMirror> annotationMirror = javaxUtil.getAnnotationMirror(classElement, Component.class);
        if (annotationMirror.isPresent()) {
            return InstantiationType.SPRING_COMPONENT;
        }
        return InstantiationType.CONSTRUCTOR;
    }
}
