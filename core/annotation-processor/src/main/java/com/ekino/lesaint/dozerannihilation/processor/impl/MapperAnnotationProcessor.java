package com.ekino.lesaint.dozerannihilation.processor.impl;

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
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

import com.ekino.lesaint.dozerannihilation.annotation.Mapper;
import org.springframework.stereotype.Component;

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

        DAMapperClass daMapperClass = new DAMapperClass(classElement);
        // retrieve name of the package of the class with @Mapper
        daMapperClass.packageName = retrievePackageName(classElement);

        // retrieve names of the class with @Mapper
        daMapperClass.type =  extractType((DeclaredType) classElement.asType());

        // retrieve qualifiers of the class with @Mapper + make check : must be public or protected sinon erreur de compilation
        daMapperClass.modifiers = classElement.getModifiers();
        if (daMapperClass.modifiers.contains(Modifier.PRIVATE)) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Class annoted with @Mapper can not be private", classElement);
            return;
        }

        // retrieve interfaces implemented (directly and if any) by the class with @Mapper (+ their generics)
        // chercher si l'une d'elles est Function (Guava)
        daMapperClass.interfaces = retrieveInterfaces(classElement);

        // pour le moment, on ne traite pas les classes abstraites implémentées par la class @Mapper ni les interfaces
        // implémentées indirectement

        // rechercher si la classe Mapper implémente Function
        List<DAInterface> guavaFunctionInterfaces = from(daMapperClass.interfaces)
                .filter(DAInterfacePredicates.isGuavaFunction())
                .toList();

        if (guavaFunctionInterfaces.size() > 1) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Mapper implementing more than one Function interface is not supported", classElement);
            return;
        }
        if (guavaFunctionInterfaces.isEmpty()) { // TOIMPROVE cette vérification ne sera plus obligatoire si on introduit @MapperMethod
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Mapper not implementing Function interface is not supported", classElement);
            return;
        }

        // rechercher une ou plusieurs méthodes annotées avec @MapperFunction
        // si classe @Mapper implémente Function, la rechercher en commençant par les méthodes annotées avec @MapperFunction
        // si aucune méthode trouvée => erreur  de compilation
        daMapperClass.methods = retrieveMethods(classElement);
        if (daMapperClass.methods.isEmpty()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Class annoted with @Mapper must have at least one methode", classElement);
            return;
        }
        List<DAMethod> guavaFunctionMethods = from(daMapperClass.methods).filter(DAMethodPredicates.isGuavaFunction()).toList();
        if (guavaFunctionMethods.size() > 1) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Mapper having more than one apply method is not supported", classElement);
            return;
        }
        if (guavaFunctionMethods.isEmpty()) { // TOIMPROVE cette vérification ne sera plus obligatoire si on introduit @MapperMethod
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Mapper not having a apply method is not supported", classElement);
            return;
        }
        // TOIMPROVE : la récupération et les contrôles sur la méthode apply sont faibles


        // retrieve instantiation type from @Mapper annotation
        //  - CONSTRUCTOR : check public/protected default constructor exists sinon erreur de compilation
        //  - SINGLETON_ENUM : check @Mapper class is an enum + check there is only one value sinon erreur de compilation
        //  - SPRING_COMPONENT : TOFINISH quelles vérifications sur la class si le InstantiationType est SPRING_COMPONENT ?
        daMapperClass.instantiationType = computeInstantiationType(classElement);
        if (!checkInstantiationTypeRequirements(daMapperClass)) {
            return;
        }

        // construction des listes d'imports
        DefaultImportVisitor visitor = new DefaultImportVisitor();
        daMapperClass.visite(visitor);

        DefaultFileGeneratorContext context = new DefaultFileGeneratorContext(daMapperClass, visitor);

        // 1 - générer l'interface du Mapper
        generateMapper(context);

        // 2 - générer la factory
        generateMapperFactory(context);

        // 3 - générer l'implémentation du Mapper
        generateMapperImpl(context);
    }

    private boolean checkInstantiationTypeRequirements(DAMapperClass daMapperClass) {
        switch (daMapperClass.instantiationType) {
            case SPRING_COMPONENT:
                return true; // requirements are enforced by Spring
            case CONSTRUCTOR:
                return hasAccessibleConstructor(daMapperClass.classElement, daMapperClass.methods);
            case SINGLETON_ENUM:
                return hasOnlyOneEnumValue(daMapperClass.classElement);
            default:
                throw new IllegalArgumentException("Unsupported instantiationType " + daMapperClass.instantiationType);
        }
    }

    private boolean hasAccessibleConstructor(TypeElement classElement, List<DAMethod> methods) {
        Optional<DAMethod> accessibleConstructor = FluentIterable.from(methods).filter(new Predicate<DAMethod>() {
            @Override
            public boolean apply(@Nullable DAMethod daMethod) {
                return daMethod.kind == ElementKind.CONSTRUCTOR;
            }
        }).filter(new Predicate<DAMethod>() {
            @Override
            public boolean apply(@Nullable DAMethod daMethod) {
                return !FluentIterable.from(daMethod.modifiers).firstMatch(Predicates.equalTo(Modifier.PRIVATE)).isPresent();
            }
        }).first();

        if (!accessibleConstructor.isPresent()) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "Classe does not exposed an accessible default constructor",
                    classElement
            );
        }

        return accessibleConstructor.isPresent();
    }

    private boolean hasOnlyOneEnumValue(TypeElement classElement) {
        if (classElement.getEnclosedElements() == null) {
            // this case can not occurs because it is enforced by the java compiler
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "Enum annoted wih @Mapper must have one value",
                    classElement
            );
            return false;
        }

        int res = from(classElement.getEnclosedElements())
                // enum values are VariableElement
                .filter(Predicates.instanceOf(VariableElement.class))
                .size();
        if (res != 1) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "Enum annoted with @Mapper must have just one value",
                    classElement
            );
        }
        return res == 1;
    }

    private void generateFile(FileGenerator fileGenerator,
                              FileGeneratorContext context) throws IOException {

        JavaFileObject jfo = processingEnv.getFiler().createSourceFile(
                fileGenerator.fileName(context),
                context.getMapperClass().classElement
        );
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "generating " + jfo.toUri());

        fileGenerator.writeFile(new BufferedWriter(jfo.openWriter()), context);
    }

    private void generateMapper(FileGeneratorContext context) throws IOException {
        generateFile(new MapperFileGenerator(), context);
    }

    private void generateMapperFactory(FileGeneratorContext context) throws IOException {
        if (shouldGenerateMapperFactory(context)) {
            generateFile(new MapperFactoryFileGenerator(), context);
        }
    }

    private boolean shouldGenerateMapperFactory(FileGeneratorContext context) {
        return context.getMapperClass().instantiationType != InstantiationType.SPRING_COMPONENT;
    }

    private void generateMapperImpl(FileGeneratorContext context) throws IOException {
        generateFile(new MapperImplFileGenerator(), context);
    }

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

                        DAMethod res = new DAMethod();
                        res.kind = o.getKind();
                        res.name = DANameFactory.from(o.getSimpleName());
                        ExecutableElement methodElement = (ExecutableElement) o;
                        res.modifiers = extractModifiers(methodElement);
                        res.returnType = extractReturnType(methodElement);
                        res.parameters = extractParameters(methodElement);
                        res.mapperMethod = isMapperMethod(methodElement);
                        return res;
                    }
                })
                .filter(Predicates.notNull())
                .toList();
    }

    private @Nonnull Set<Modifier> extractModifiers(ExecutableElement methodElement) {
        if (methodElement.getModifiers() == null) {
            return Collections.emptySet();
        }
        return methodElement.getModifiers();
    }

    private DAType extractReturnType(ExecutableElement methodElement) {
        return extractType(methodElement.getReturnType());
    }

    private boolean isMapperMethod(ExecutableElement methodElement) {
        // TODO implementer isMapperMethod si on ajoute une annotation MapperMethod
        return false;
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
                        DAParameter res = new DAParameter();
                        res.name = DANameFactory.from(o.getSimpleName());
                        res.type = extractType(o.asType());
                        res.modifiers = o.getModifiers();
                        return res;
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

                DeclaredType interfaceType = (DeclaredType) o;
                DAInterface daInterface = new DAInterface();
                daInterface.type = extractType(interfaceType);
                return daInterface;
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

    private DAType extractType(TypeMirror type) {
        Types typeUtils = processingEnv.getTypeUtils();
        Element element = typeUtils.asElement(type);
        if (type.getKind() == TypeKind.ARRAY) {
            element = typeUtils.asElement(((ArrayType) type).getComponentType());
        }

        return extractType(type, element);
    }

    private DAType extractType(TypeMirror type, Element element) {
        if (type.getKind() == TypeKind.VOID) {
            return null;
        }
        DAType res = new DAType();
        res.kind = type.getKind();
        res.simpleName = DANameFactory.from(element.getSimpleName());
        res.qualifiedName = extractQualifiedName(element);
        res.typeArgs = extractTypeArgs(type);
        return res;
    }

    private static DAName extractQualifiedName(Element element) {
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

    private static InstantiationType computeInstantiationType(TypeElement classElement) {
        if (classElement.getKind() == ElementKind.ENUM) {
            return InstantiationType.SINGLETON_ENUM;
        }
        Optional<AnnotationMirror> annotationMirror = getAnnotationMirror(classElement, Component.class);
        if (annotationMirror.isPresent()) {
            return InstantiationType.SPRING_COMPONENT;
        }
        return InstantiationType.CONSTRUCTOR;
    }

    private static Optional<AnnotationMirror> getAnnotationMirror(TypeElement classElement,
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
