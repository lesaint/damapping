package com.ekino.lesaint.dozerannihilation.processor;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;

import com.ekino.lesaint.dozerannihilation.annotation.InstantiationType;
import com.ekino.lesaint.dozerannihilation.annotation.Mapper;

/**
 * MapperAnnotationProcessor -
 *
 * @author lesaint
 */
public class MapperAnnotationProcessor extends AbstractAnnotationProcessor<Mapper> {
    protected MapperAnnotationProcessor(ProcessingEnvironment processingEnv) {
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

        DAClass daClass = new DAClass();
        // retrieve name of the package of the class with @Mapper
        daClass.packageName = retrievePackageName(classElement);

        // retrieve names of the class with @Mapper
        daClass.type =  extractType((DeclaredType) classElement.asType());

        // retrieve qualifiers of the class with @Mapper + make check : must be public or protected sinon erreur de compilation
        daClass.modifiers = classElement.getModifiers();
        if (daClass.modifiers.contains(Modifier.PRIVATE)) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Class annoted with @Mapper can not be private", classElement);
            return;
        }

        // retrieve instantiation type from @Mapper annotation
        //  - CONSTRUCTOR : check public/protected default constructor exists sinon erreur de compilation
        //  - SINGLETON_ENUM : check @Mapper class is an enum + check there is only one value sinon erreur de compilation
        //  - SPRING_COMPONENT : TOFINISH quelles vérifications sur la class si le InstantiationType est SPRING_COMPONENT ?
        daClass.instantiationType = retrieveInstantiationType(classElement);

        // retrieve interfaces implemented (directly and if any) by the class with @Mapper (+ their generics)
        // chercher si l'une d'elles est Function (Guava)
        daClass.interfaces = retrieveInterfaces(classElement);

        // pour le moment, on ne traite pas les classes abstraites implémentées par la class @Mapper ni les interfaces
        // implémentées indirectement

        // rechercher si la classe Mapper implémente Function
        List<DAInterface> guavaFunctionInterfaces = FluentIterable.from(daClass.interfaces).filter(new Predicate<DAInterface>() {
            @Override
            public boolean apply(@Nullable DAInterface daInterface) {
                return daInterface != null && daInterface.isGuavaFunction();
            }
        }).toList();

        if (guavaFunctionInterfaces.size() > 1) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Mapper implementing more than one Function is not supported", classElement);
            return;
        }

        // rechercher une ou plusieurs méthodes annontées avec @MapperFunction
        // si classe @Mapper implémente Function, la rechercher en commençant par les méthodes annotées avec @MapperFunction
        // si aucune méthode trouvée => erreur  de compilation
        daClass.methods = retrieveMethods(classElement);

        // 1 - générer l'interface du Mapper
        //     -> nom de package
        //     -> nom de la classe (infère nom du Mapper)
        //     -> visibilite de la classe (protected ou public ?)
        //     -> liste des interfaces implémentées
        //     -> compute liste des imports à réaliser

        // 2 - générer la factory
        //     -> nom du package
        //     -> nom de la classe (infère nom de la factory et nom du Mapper)
        //     -> type d'instantiation (si enum, le nom de la valeur d'enum à utiliser)

        // 3 - générer l'implémentation du Mapper
        //     -> nom de package
        //     -> nom de la classe (infère nom du Mapper, nom de la factory, nom de l'implémentation)
        //     -> liste des méthodes mapper
        //     -> compute liste des imports à réaliser

        // TODO switch on the various InstantiationType values

//        JavaFileObject jfo = processingEnv.getFiler().createSourceFile(classElement.getQualifiedName() + "Mapper", element);
//        System.out.println("generating " + jfo.toUri());
//        BufferedWriter bw = new BufferedWriter(jfo.openWriter());
//        bw.append("package ");
//        bw.append(packageElement.getQualifiedName());
//        bw.append(";");
//        bw.newLine();
//        bw.append("import com.google.common.base.Function;");
//        bw.newLine();
//        bw.append("public interface ");
//        bw.append(classElement.getSimpleName() + "Mapper");
//        bw.append(" implements Function<");
//        bw.flush();
//        bw.close();
    }

    private List<DAMethod> retrieveMethods(final TypeElement classElement) {
        if (classElement.getEnclosedElements() == null) {
            return Collections.emptyList();
        }

        return FluentIterable.from(classElement.getEnclosedElements())
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
                        res.name = o.getSimpleName();
                        ExecutableElement methodElement = (ExecutableElement) o;
                        res.parameters = extractParameters(methodElement);
                        res.mapperMethod = isMapperMethod(methodElement);
                        return res;
                    }
                })
                .toList();
    }

    private boolean isMapperMethod(ExecutableElement methodElement) {
        // TODO implementer isMapperMethod si on ajoute une annotation MapperMethod
        return false;
    }

    private List<DAParameter> extractParameters(ExecutableElement methodElement) {
        if (methodElement.getParameters() == null) {
            return null;
        }

        return FluentIterable.from(methodElement.getParameters())
                .transform(new Function<VariableElement, DAParameter>() {
                    @Nullable
                    @Override
                    public DAParameter apply(@Nullable VariableElement o) {
                        DeclaredType declaredType = (DeclaredType) o.asType();

                        DAParameter res = new DAParameter();
                        res.simpleName = o.getSimpleName();
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
        res.simpleName = element.getSimpleName();
        if (element instanceof QualifiedNameable) {
            res.qualifiedName = ((QualifiedNameable) element).getQualifiedName();
        }
        return res;
    }

    private List<DAInterface> retrieveInterfaces(final TypeElement classElement) {
        List<? extends TypeMirror> interfaces = classElement.getInterfaces();
        if (interfaces == null) {
            return Collections.emptyList();
        }

        return FluentIterable.from(interfaces).transform(new Function<TypeMirror, DAInterface>() {
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

        return FluentIterable.from(interfaceType.getTypeArguments())
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
                        res.simpleName = o1.asElement().getSimpleName();
                        res.qualifiedName = extractQualifiedName(o1);
                        return res;
                    }
                })
                .filter(Predicates.notNull())
                .toList();
    }

    private static Name extractQualifiedName(DeclaredType o) {
        if (o.asElement() instanceof QualifiedNameable) {
            return ((QualifiedNameable) o.asElement()).getQualifiedName();
        }
        return null;
    }

    private class DAClass {
        Name packageName;
        DAType type;
        Set<Modifier> modifiers;
        List<DAInterface> interfaces;
        List<DAMethod> methods;
        // specific to the class annoted with @Mapper
        InstantiationType instantiationType;
    }

    private class DAInterface {
        DAType type;
        List<DAType> typeArgs;

        public boolean isGuavaFunction() {
            return type.qualifiedName != null && Function.class.getCanonicalName().equals(type.qualifiedName.toString());
        }
    }

    private class DAType {
        Name qualifiedName;
        Name simpleName;
    }

    private class DAMethod {
        ElementKind kind;
        Name name;
        List<DAParameter> parameters;
        boolean mapperMethod;

        public boolean isDefaultConstructor() {
            return kind == ElementKind.CONSTRUCTOR;
        }
    }

    private class DAParameter {
        Name simpleName;
        DAType type;
        Set<Modifier> modifiers;

    }

    private static Name retrievePackageName(TypeElement classElement) {
        PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();
        return packageElement.getQualifiedName();
    }

    private static InstantiationType retrieveInstantiationType(TypeElement classElement) {
        Optional<AnnotationMirror> annotationMirror = getAnnotationMirror(classElement, Mapper.class);
        if (!annotationMirror.isPresent()) {
//            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, ); TODO add an error message if expected Annotation is not found on element
            return null;
        }

        String enumValue = getEnumNameElementValue(annotationMirror.get(), "value");

        // TODO quel est le comportement s'il n'y a pas de valeur explicite ? on récupère null et on doit aller récupérer la valeur par défaut dans l'AnnotationMiror ou la valeur par défaut est automatiquement inférée ?

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
