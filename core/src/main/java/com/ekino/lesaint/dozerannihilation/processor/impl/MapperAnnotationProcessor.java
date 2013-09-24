package com.ekino.lesaint.dozerannihilation.processor.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Iterator;
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
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

import com.ekino.lesaint.dozerannihilation.annotation.InstantiationType;
import com.ekino.lesaint.dozerannihilation.annotation.Mapper;

/**
 * MapperAnnotationProcessor -
 *
 * @author lesaint
 */
public class MapperAnnotationProcessor extends AbstractAnnotationProcessor<Mapper> {
    public MapperAnnotationProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv, Mapper.class);
    }

    private final Predicate<DAMethod> isGuavaMethod = new Predicate<DAMethod>() {
        @Override
        public boolean apply(@Nullable DAMethod daMethod) {
            return daMethod != null && daMethod.isGuavaFunction();
        }
    };
    private final Predicate<DAInterface> isGuavaInterface = new Predicate<DAInterface>() {
        @Override
        public boolean apply(@Nullable DAInterface daInterface) {
            return daInterface != null && daInterface.isGuavaFunction();
        }
    };

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

        // retrieve instantiation type from @Mapper annotation
        //  - CONSTRUCTOR : check public/protected default constructor exists sinon erreur de compilation
        //  - SINGLETON_ENUM : check @Mapper class is an enum + check there is only one value sinon erreur de compilation
        //  - SPRING_COMPONENT : TOFINISH quelles vérifications sur la class si le InstantiationType est SPRING_COMPONENT ?
        daMapperClass.instantiationType = retrieveInstantiationType(classElement);

        // retrieve interfaces implemented (directly and if any) by the class with @Mapper (+ their generics)
        // chercher si l'une d'elles est Function (Guava)
        daMapperClass.interfaces = retrieveInterfaces(classElement);

        // pour le moment, on ne traite pas les classes abstraites implémentées par la class @Mapper ni les interfaces
        // implémentées indirectement

        // rechercher si la classe Mapper implémente Function
        List<DAInterface> guavaFunctionInterfaces = FluentIterable.from(daMapperClass.interfaces).filter(isGuavaInterface).toList();

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
        ImmutableList<DAMethod> guavaFunctionMethods = FluentIterable.from(daMapperClass.methods).filter(isGuavaMethod).toList();
        if (guavaFunctionMethods.size() > 1) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Mapper having more than one apply method is not supported", classElement);
            return;
        }
        if (guavaFunctionMethods.isEmpty()) { // TOIMPROVE cette vérification ne sera plus obligatoire si on introduit @MapperMethod
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Mapper not having a apply method is not supported", classElement);
            return;
        }
        // TOIMPROVE : la récupération et les constrôles sur la méthode apply sont faibles

        // contrôles en fonction du InstantiationType

        // construction des listes d'imports
        DefaultImportVisitor visitor = new DefaultImportVisitor();
        daMapperClass.visite(visitor);

        // 1 - générer l'interface du Mapper
        //     -> nom de package
        //     -> nom de la classe (infère nom du Mapper)
        //     -> visibilite de la classe (protected ou public ?)
        //     -> liste des interfaces implémentées
        //     -> compute liste des imports à réaliser
        generateMapperInterface(daMapperClass, visitor);

        // 2 - générer la factory
        //     -> nom du package
        //     -> nom de la classe (infère nom de la factory et nom du Mapper)
        //     -> type d'instantiation (si enum, le nom de la valeur d'enum à utiliser)
        generateMapperFactoryInterface(daMapperClass, visitor);

        // 3 - générer l'implémentation du Mapper
        //     -> nom de package
        //     -> nom de la classe (infère nom du Mapper, nom de la factory, nom de l'implémentation)
        //     -> liste des méthodes mapper
        //     -> compute liste des imports à réaliser
        generateMapperImpl(daMapperClass, visitor);

        // TODO switch on the various InstantiationType values
    }

    private void generateMapperInterface(DAMapperClass daMapperClass, DefaultImportVisitor visitor) throws IOException {

        JavaFileObject jfo = processingEnv.getFiler().createSourceFile(daMapperClass.type.qualifiedName + "Mapper", daMapperClass.classElement);
        System.out.println("generating " + jfo.toUri());

        BufferedWriter bw = new BufferedWriter(jfo.openWriter());
        List<Name> mapperImports = visitor.getMapperImports();
        appendHeader(bw, daMapperClass, mapperImports);
        for (Modifier modifier : daMapperClass.modifiers) {
            bw.append(modifier.toString()).append(" ");
        }
        bw.append("interface ");
        bw.append(daMapperClass.type.simpleName).append("Mapper");
        if (!daMapperClass.interfaces.isEmpty()) {
            bw.append(" extends ");
        }
        for (DAInterface anInterface : daMapperClass.interfaces) {
            bw.append(anInterface.type.simpleName);
            Iterator<DAType> iterator = anInterface.typeArgs.iterator();
            if (iterator.hasNext()) {
                bw.append("<");
                while (iterator.hasNext()) {
                    DAType arg = iterator.next();
                    bw.append(arg.simpleName);
                    if (iterator.hasNext()) {
                        bw.append(", ");
                    }
                }
                bw.append(">");
            }
        }
        bw.append(" {");
        bw.newLine();
        bw.newLine();

        appendFooter(bw);

        bw.flush();
        bw.close();

    }
    private static final String INDENT = "    ";

    private void generateMapperFactoryInterface(DAMapperClass daMapperClass, DefaultImportVisitor visitor) throws IOException {

        JavaFileObject jfo = processingEnv.getFiler().createSourceFile(daMapperClass.type.qualifiedName + "MapperFactory", daMapperClass.classElement);
        System.out.println("generating " + jfo.toUri());

        BufferedWriter bw = new BufferedWriter(jfo.openWriter());

        appendHeader(bw, daMapperClass, visitor.getMapperFactoryImports());

        bw.append("class ").append(daMapperClass.type.simpleName).append("MapperFactory").append(" {");
        bw.newLine();
        bw.newLine();
        bw.append(INDENT).append("public static ").append(daMapperClass.type.simpleName).append(" instance() {");
        bw.newLine();
        switch (daMapperClass.instantiationType) {
            case SINGLETON_ENUM:
                // TOIMPROVE générer le code de la factory dans le cas enum avec un nom d'enum dynamique
                bw.append(INDENT).append(INDENT).append("return ").append(daMapperClass.type.simpleName).append(".INSTANCE;");
                break;
            case CONSTRUCTOR:
                bw.append(INDENT).append(INDENT).append("return new ").append(daMapperClass.type.simpleName).append("();");
                break;
            case SPRING_COMPONENT:
                // cas qui ne doit pas survenir
                break;
        }
        bw.newLine();
        bw.append(INDENT).append("}");
        bw.newLine();

        appendFooter(bw);

        bw.flush();
        bw.close();
    }

    private void appendFooter(BufferedWriter bw) throws IOException {
        bw.append("}");
        bw.newLine();
    }

    private void generateMapperImpl(DAMapperClass daMapperClass, DefaultImportVisitor visitor) throws IOException {

        JavaFileObject jfo = processingEnv.getFiler().createSourceFile(daMapperClass.type.qualifiedName + "MapperImpl", daMapperClass.classElement);
        System.out.println("generating " + jfo.toUri());

        BufferedWriter bw = new BufferedWriter(jfo.openWriter());

        appendHeader(bw, daMapperClass, visitor.getMapperImplImports());

        bw.append("class ").append(daMapperClass.type.simpleName).append("MapperImpl").append(" implements ").append(daMapperClass.type.simpleName).append("Mapper").append(" {");
        bw.newLine();
        bw.newLine();

        DAInterface guavaInterface = FluentIterable.from(daMapperClass.interfaces).firstMatch(isGuavaInterface).get();
        DAMethod guavaMethod = FluentIterable.from(daMapperClass.methods).firstMatch(isGuavaMethod).get();

        bw.append(INDENT).append("@Override");
        bw.newLine();
        bw.append(INDENT).append("public ").append(guavaMethod.returnType.simpleName).append(" ").append(guavaMethod.name).append("(");
        Iterator<DAType> typeArgsIterator = guavaInterface.typeArgs.iterator();
        Iterator<DAParameter> parametersIterator = guavaMethod.parameters.iterator();
        while (hasNext(typeArgsIterator, parametersIterator)) {
            bw.append(typeArgsIterator.next().simpleName).append(" ").append(parametersIterator.next().name);
            if (hasNext(typeArgsIterator, parametersIterator)) {
                bw.append(", ");
            }
        }
        bw.append(")").append(" {");
        bw.newLine();
        bw.append(INDENT).append(INDENT).append("return ").append(daMapperClass.type.simpleName).append("MapperFactory").append(".instance()").append(".").append(guavaMethod.name).append("(");
        parametersIterator = guavaMethod.parameters.iterator();
        while (parametersIterator.hasNext()) {
            bw.append(parametersIterator.next().name);
            if (parametersIterator.hasNext()) {
                bw.append(", ");
            }
        }
        bw.append(");");
        bw.newLine();
        bw.append(INDENT).append("}");
        bw.newLine();

        appendFooter(bw);

        bw.flush();
        bw.close();
    }

    private boolean hasNext(Iterator<DAType> typeArgsIterator, Iterator<DAParameter> parametersIterator) {
        return typeArgsIterator.hasNext() && parametersIterator.hasNext();
    }

    private void appendHeader(BufferedWriter bw, DAMapperClass daMapperClass, List<Name> mapperImports) throws IOException {
        List<Name> imports = filterImports(mapperImports, daMapperClass);

        bw.append("package ").append(daMapperClass.packageName).append(";");
        bw.newLine();
        bw.newLine();
        if (!imports.isEmpty()) {
            for (Name name : imports) {
                bw.append("import ").append(name).append(";");
            }
            bw.newLine();
            bw.newLine();
        }
        bw.append("// GENERATED CODE, DO NOT MODIFY, THIS WILL BE OVERRIDE");
        bw.newLine();
    }

    private List<Name> filterImports(List<Name> mapperImports, final DAMapperClass daMapperClass) {
        return FluentIterable.from(mapperImports)
                .filter(
                        Predicates.not(
                                Predicates.or(
                                        // imports in the same package as the generated class (ie. the package of the Mapper class)
                                        new Predicate<Name>() {
                                            @Override
                                            public boolean apply(@Nullable Name name) {
                                                return name != null && name.toString().startsWith(daMapperClass.packageName.toString());
                                            }
                                        },
                                        // imports from java itself
                                        new Predicate<Name>() {
                                            @Override
                                            public boolean apply(@Nullable Name name) {
                                                return name != null && name.toString().startsWith("java.lang.");
                                            }
                                        }
                                )
                        )
                ).toList();
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

        return FluentIterable.from(methodElement.getParameters())
                .transform(new Function<VariableElement, DAParameter>() {
                    @Nullable
                    @Override
                    public DAParameter apply(@Nullable VariableElement o) {
                        DeclaredType declaredType = (DeclaredType) o.asType();

                        DAParameter res = new DAParameter();
                        res.name = o.getSimpleName();
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

    private static Name retrievePackageName(TypeElement classElement) {
        PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();
        return packageElement.getQualifiedName();
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
