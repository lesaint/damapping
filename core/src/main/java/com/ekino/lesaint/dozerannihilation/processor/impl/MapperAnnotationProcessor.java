package com.ekino.lesaint.dozerannihilation.processor.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicates;

import com.ekino.lesaint.dozerannihilation.annotation.InstantiationType;
import com.ekino.lesaint.dozerannihilation.annotation.Mapper;

import static com.google.common.collect.FluentIterable.from;

/**
 * MapperAnnotationProcessor -
 *
 * @author lesaint
 */
public class MapperAnnotationProcessor extends AbstractAnnotationProcessor<Mapper> {
    public MapperAnnotationProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv, Mapper.class);
    }

    @Override
    protected void process(Element element, RoundEnvironment roundEnv) throws IOException {
        if (element.getKind() != ElementKind.CLASS) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Type annoted with @Mapper annotation is not class " + element);
            return;
        }

        TypeElement classElement = (TypeElement) element;

        InstantiationType instantiationType = retrieveInstantiationType(classElement);
        System.out.println("Processing " + classElement.getQualifiedName() + " in " + getClass().getCanonicalName());

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

        // rechercher une ou plusieurs méthodes annontées avec @MapperFunction
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
        // TOIMPROVE : la récupération et les constrôles sur la méthode apply sont faibles


        // retrieve instantiation type from @Mapper annotation
        //  - CONSTRUCTOR : check public/protected default constructor exists sinon erreur de compilation
        //  - SINGLETON_ENUM : check @Mapper class is an enum + check there is only one value sinon erreur de compilation
        //  - SPRING_COMPONENT : TOFINISH quelles vérifications sur la class si le InstantiationType est SPRING_COMPONENT ?
        daMapperClass.instantiationType = retrieveInstantiationType(classElement);
        // TODO contrôles en fonction du InstantiationType

        // construction des listes d'imports
        DefaultImportVisitor visitor = new DefaultImportVisitor();
        daMapperClass.visite(visitor);

        DefaultFileGeneratorContext context = new DefaultFileGeneratorContext(daMapperClass, visitor);

        // 1 - générer l'interface du Mapper
        generateMapperInterface(context);

        // 2 - générer la factory
        generateMapperFactoryInterface(context);

        // 3 - générer l'implémentation du Mapper
        generateMapperImpl(context);
    }

    private void generateFile(FileGenerator fileGenerator,
                              FileGeneratorContext context) throws IOException {

        JavaFileObject jfo = processingEnv.getFiler().createSourceFile(
                fileGenerator.fileName(context),
                context.getMapperClass().classElement
        );
        System.out.println("generating " + jfo.toUri());

        fileGenerator.writeFile(new BufferedWriter(jfo.openWriter()), context);
    }

    private void generateMapperInterface(FileGeneratorContext context) throws IOException {
        generateFile(new MapperFileGenerator(), context);
    }

    private void generateMapperFactoryInterface(FileGeneratorContext context) throws IOException {
        generateFile(new MapperFactoryFileGenerator(), context);
    }

    private void generateMapperImpl(FileGeneratorContext context) throws IOException {
        generateFile(new MapperImplFileGenerator(), context);
    }

    private List<DAMethod> retrieveMethods(final TypeElement classElement) {
        if (classElement.getEnclosedElements() == null) {
            return Collections.emptyList();
        }

        return from(classElement.getEnclosedElements())
                .transform(new Function<Element, DAMethod>() {
                    @Nullable
                    @Override
                    public DAMethod apply(@Nullable Element o) {
                        if (!(o instanceof ExecutableElement)) {
                            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                                    "EnclosedElement is not ExecutableElement. Not supported", classElement
                            );
                            return null;
                        }

                        DAMethod res = new DAMethod();
                        res.kind = o.getKind();
                        res.name = DANameFactory.from(o.getSimpleName());
                        ExecutableElement methodElement = (ExecutableElement) o;
                        res.returnType = extractReturnType(methodElement);
                        res.parameters = extractParameters(methodElement);
                        res.mapperMethod = isMapperMethod(methodElement);
                        return res;
                    }
                })
                .toList();
    }

    private DAType extractReturnType(ExecutableElement methodElement) {
        if (methodElement.getReturnType() instanceof NoType) {
            return null;
        }
        return extractType((DeclaredType) methodElement.getReturnType());
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
                        DeclaredType declaredType = (DeclaredType) o.asType();

                        DAParameter res = new DAParameter();
                        res.name = DANameFactory.from(o.getSimpleName());
                        res.type = extractType(declaredType);
                        res.modifiers = o.getModifiers();
                        return res;
                    }
                })
                .filter(Predicates.notNull())
                .toList();
    }

    private DAType extractType(DeclaredType declaredType) {
        Element element = declaredType.asElement();
        DAType res = new DAType();
        res.simpleName = DANameFactory.from(element.getSimpleName());
        if (element instanceof QualifiedNameable) {
            res.qualifiedName = DANameFactory.from(((QualifiedNameable) element).getQualifiedName());
        }
        return res;
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
                if (!(o instanceof DeclaredType)) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "Interface is not a DeclaredType, not supported", classElement
                    );
                    return null;
                }

                DeclaredType interfaceType = (DeclaredType) o;
                DAInterface daInterface = new DAInterface();
                daInterface.type = extractType(interfaceType);
                daInterface.typeArgs = extractTypeArgs(interfaceType);
                return daInterface;
            }
        }).filter(Predicates.notNull()).toList();
    }

    private List<DAType> extractTypeArgs(final DeclaredType interfaceType) {
        if (interfaceType.getTypeArguments() == null) {
            return Collections.emptyList();
        }

        return from(interfaceType.getTypeArguments())
                .transform(new Function<TypeMirror, DAType>() {
                    @Nullable
                    @Override
                    public DAType apply(@Nullable TypeMirror o) {
                        if (!(o instanceof DeclaredType)) {
                            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                                    "Type argument of interface is not a DeclaredType. Not supported.",
                                    interfaceType.asElement()
                            );
                            return null;
                        }

                        DAType res = new DAType();
                        DeclaredType o1 = (DeclaredType) o;
                        res.simpleName = DANameFactory.from(o1.asElement().getSimpleName());
                        res.qualifiedName = extractQualifiedName(o1);
                        return res;
                    }
                })
                .filter(Predicates.notNull())
                .toList();
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

    private InstantiationType retrieveInstantiationType(TypeElement classElement) {
        Optional<AnnotationMirror> annotationMirror = getAnnotationMirror(classElement, Mapper.class);
        if (!annotationMirror.isPresent()) {
            // ce cas est pratiquement impossible !
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Annotation Mapper non trouvée sur la classe annotée avec @Mapper", classElement);
            return null;
        }

        String enumValue = getEnumNameElementValue(annotationMirror.get(), "value");
        if (enumValue == null) {
            // TOIMPROVE la valeur par défaut est décrite dans l'annotation @Mapper, récupérer la valeur directement depuis le AnnotationMirror
            InstantiationType defaultValue = InstantiationType.SINGLETON_ENUM;
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                    "Annotation Mapper has no explicite value, using defaut one", classElement, annotationMirror.get()
            );
            return defaultValue;
        }

        return InstantiationType.valueOf(enumValue);
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
