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

import fr.javatronic.damapping.annotation.Mapper;
import fr.javatronic.damapping.processor.impl.javaxparsing.model.JavaxDAMethod;
import fr.javatronic.damapping.processor.impl.javaxparsing.model.JavaxDASourceClass;
import fr.javatronic.damapping.processor.model.DAInterface;
import fr.javatronic.damapping.processor.model.DAMethod;
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.factory.DANameFactory;
import fr.javatronic.damapping.processor.model.function.ToGuavaFunctionOrMapperMethod;
import fr.javatronic.damapping.processor.model.impl.DAInterfaceImpl;
import fr.javatronic.damapping.processor.model.impl.DAMethodImpl;
import fr.javatronic.damapping.processor.model.impl.DASourceClassImpl;
import fr.javatronic.damapping.util.Function;
import fr.javatronic.damapping.util.Lists;
import fr.javatronic.damapping.util.Predicate;
import fr.javatronic.damapping.util.Predicates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleElementVisitor6;
import javax.lang.model.util.SimpleTypeVisitor6;

import static fr.javatronic.damapping.processor.model.impl.DAMethodImpl.makeGuavaFunctionApplyMethod;
import static fr.javatronic.damapping.processor.model.impl.DAMethodImpl.makeMapperMethod;
import static fr.javatronic.damapping.util.FluentIterable.from;
import static fr.javatronic.damapping.util.Preconditions.checkNotNull;
import static fr.javatronic.damapping.util.Predicates.notNull;

/**
 * JavaxParsingService -
 *
 * @author Sébastien Lesaint
 */
public class JavaxParsingServiceImpl implements JavaxParsingService {
  @Nonnull
  private final ProcessingEnvironmentWrapper processingEnv;

  public JavaxParsingServiceImpl(@Nonnull ProcessingEnvironmentWrapper processingEnv) {
    this.processingEnv = checkNotNull(processingEnv);
  }

  @Nonnull
  @Override
  public ParsingResult parse(@Nonnull TypeElement classElement,
                             @Nullable Collection<DAType> generatedTypes) throws IOException {
    ReferenceScanResult scanResult = new ReferencesScanner(processingEnv, generatedTypes).scan(classElement);
    if (scanResult.hasUnresolved()) {
      return ParsingResult.later(classElement, null, scanResult.getUnresolved());
    }

    DAType type = null;
    try {
      JavaxExtractorImpl javaxExtractor = new JavaxExtractorImpl(processingEnv, scanResult);
      type = javaxExtractor.extractType(classElement.asType());
      DASourceClass daSourceClass = parseImpl(classElement, type, javaxExtractor);

      return ParsingResult.ok(classElement, daSourceClass);
    } catch (Exception e) {
      processingEnv.printMessage(Mapper.class, classElement, e);
      return ParsingResult.failed(classElement, type);
    }
  }

  @Nonnull
  private DASourceClass parseImpl(TypeElement classElement, DAType type, JavaxExtractor javaxExtractor) {
    DASourceClassImpl.Builder<?> builder;
    if (classElement.getKind() == ElementKind.ENUM) {
      builder = DASourceClassImpl.enumBuilder(type, javaxExtractor.extractEnumValues(classElement));
    }
    else if (classElement.getKind() == ElementKind.CLASS) {
      builder = DASourceClassImpl.classbuilder(type);
    }
    else {
      throw new IllegalArgumentException("Unsupported Kind of TypeElement, must be either CLASS or ENUM");
    }

    builder.withAnnotations(javaxExtractor.extractDAAnnotations(classElement));

    builder.withModifiers(
        from(classElement.getModifiers()).transform(javaxExtractor.toDAModifier()).toSet()
    );

    // retrieve interfaces implemented (directly and if any) by the class with @Mapper (+ their generics)
    // chercher si l'une d'elles est Function (Guava)
    List<DAInterface> interfaces = retrieveInterfaces(classElement, javaxExtractor);
    builder.withInterfaces(interfaces);

    builder.withMethods(
        from(retrieveMethods(classElement, javaxExtractor))
            .transform(new JavaxToGuavaFunctionOrMapperMethod(interfaces))
            .toList()
    );

    return new JavaxDASourceClass(builder.build(), classElement);
  }

  private List<DAInterface> retrieveInterfaces(final TypeElement classElement,
                                               final JavaxExtractor javaxExtractor) {
    List<TypeMirror> interfaces = new ArrayList<>();
    List<TypeElement> classHierarchyAsList = extractClassHierarchyAsList(classElement);
    for (TypeElement typeElement : classHierarchyAsList) {
      extractInterfaces(typeElement, interfaces);
    }

    return from(interfaces)
        .transform(new TypeMirrorToDAInterface(javaxExtractor))
        .filter(notNull())
        .toList();
  }

  private static void extractInterfaces(TypeElement typeElement, List<TypeMirror> res) {
    List<? extends TypeMirror> interfaces = typeElement.getInterfaces();
    if (interfaces == null) {
      return;
    }
    res.addAll(interfaces);
    for (TypeMirror anInterface : interfaces) {
      TypeElement interfaceTypeElement = asTypeElement(anInterface);

      if (interfaceTypeElement != null) {
        extractInterfaces(interfaceTypeElement, res);
      }
    }
  }

  private static class TypeMirrorToDAInterface implements Function<TypeMirror, DAInterface> {
    private final SimpleTypeVisitor6<DAInterface, Void> daInterfaceExtractor;

    public TypeMirrorToDAInterface(final JavaxExtractor javaxExtractor) {
      this.daInterfaceExtractor = new SimpleTypeVisitor6<DAInterface, Void>() {

        // TOIMPROVE : le filtrage des interfaces de la classe annotée avec @Mapper sur DeclaredType est-il pertinent ?
        @Override
        public DAInterface visitDeclared(DeclaredType declaredType, Void aVoid) {
          return new DAInterfaceImpl(javaxExtractor.extractType(declaredType));
        }
      };
    }

    @Nullable
    @Override
    public DAInterface apply(@Nullable TypeMirror o) {
      return o.accept(daInterfaceExtractor, null);
    }
  }

  @Nonnull
  private List<JavaxDAMethod> retrieveMethods(final TypeElement classElement, final JavaxExtractor javaxExtractor) {
    List<TypeElement> classHierarchy = extractClassHierarchyAsList(classElement);

    List<JavaxDAMethod> res = Lists.of();
    for (TypeElement clazz : classHierarchy) {
      if (clazz.getEnclosedElements() == null) {
        continue;
      }

      for (JavaxDAMethod javaxDAMethod : from(clazz.getEnclosedElements())
          // methods are ExecutableElement
          .filter(ExecutableElement.class)
          // filter out super class constructors
          .filter(
              clazz.equals(classElement) ? Predicates.<ExecutableElement>alwaysTrue() : FilterOutConstructor.INSTANCE
          )
          // transform into object of the DAModel
          .transform(new ExecutableElementToJavaxDAMethod(javaxExtractor, classElement))
          .filter(notNull())) {
        res.add(javaxDAMethod);
      }
    }

    return res;
  }

  private static enum FilterOutConstructor implements Predicate<ExecutableElement> {
    INSTANCE;

    @Override
    public boolean apply(@Nullable ExecutableElement executableElement) {
      return executableElement != null && executableElement.getKind() != ElementKind.CONSTRUCTOR;
    }
  }

  private List<TypeElement> extractClassHierarchyAsList(TypeElement classElement) {
    return classElement.accept(SuperTypeElementsVisitor.getInstance(true), new ArrayList<TypeElement>());
  }

  @Nullable
  private static TypeElement asTypeElement(TypeMirror t) {
    DeclaredType declaredType = asDeclaredType(t);
    if (declaredType == null) {
      return null;
    }
    return declaredType.asElement().accept(AsTypeElementVisitor.INSTANCE, null);
  }

  private static class AsTypeElementVisitor extends SimpleElementVisitor6<TypeElement, Void> {
    public static final AsTypeElementVisitor INSTANCE = new AsTypeElementVisitor();

    private AsTypeElementVisitor() {
      // prevents instantiation
    }

    @Override
    public TypeElement visitType(TypeElement e, Void o) {
      return e;
    }
  }

  @Nullable
  private static DeclaredType asDeclaredType(TypeMirror t) {
    return t.accept(AsDeclaredTypeTypeVisitor.INSTANCE, null);
  }

  private static class AsDeclaredTypeTypeVisitor extends SimpleTypeVisitor6<DeclaredType, Object> {
    public static final AsDeclaredTypeTypeVisitor INSTANCE = new AsDeclaredTypeTypeVisitor();

    private AsDeclaredTypeTypeVisitor() {
      // prevents instantiation
    }

    @Override
    public DeclaredType visitDeclared(DeclaredType t, Object o) {
      return t;
    }
  }

  private static class JavaxToGuavaFunctionOrMapperMethod extends ToGuavaFunctionOrMapperMethod<JavaxDAMethod> {

    public JavaxToGuavaFunctionOrMapperMethod(List<DAInterface> interfaces) {
      super(interfaces);
    }

    @Override
    @Nonnull
    protected DAMethod toMapperMethod(@Nonnull JavaxDAMethod daMethod) {
      return new JavaxDAMethod(makeMapperMethod(daMethod), daMethod.getMethodElement());
    }

    @Override
    @Nonnull
    protected DAMethod toGuavaFunction(@Nonnull JavaxDAMethod daMethod) {
      return new JavaxDAMethod(makeGuavaFunctionApplyMethod(daMethod), daMethod.getMethodElement());
    }
  }

  private static String uncapitalize(String str) {
    if (str == null || str.length() == 0) {
      return str;
    }
    StringBuilder sb = new StringBuilder(str.length());
    sb.append(Character.toLowerCase(str.charAt(0)));
    sb.append(str.substring(1));
    return sb.toString();
  }

  private DAMethodImpl.Builder daMethodBuilder(ExecutableElement element) {
    if (element.getKind() == ElementKind.METHOD) {
      return DAMethodImpl.methodBuilder();
    }
    if (element.getKind() == ElementKind.CONSTRUCTOR) {
      return DAMethodImpl.constructorBuilder();
    }
    throw new IllegalArgumentException(
        String.format(
            "Kind %s of element %s is not supported to build a DAMethod from", element.getKind(),
            element
        )
    );
  }

  /**
   * A ElementVisitor that will builds the list of class in a class hierarchy, as TypeElement objects, of any
   * TypeElement.
   * <p>
   * The list starts with the visited TypeElement and respects the order of the hierarchyTypeElement.
   * </p>
   * <p>
   * Optionally, classes from {@code java.lang} package and its subpackages can be excluded (ie.
   * {@link java.lang.Object}, {@link java.lang.Enum}). In such case, the returned list can be empty when the visited
   * TypeElement belongs to the theses packages.
   * </p>
   */
  private static class SuperTypeElementsVisitor extends SimpleElementVisitor6<List<TypeElement>, List<TypeElement>> {
    private static final SuperTypeElementsVisitor WITH_JAVA_LANG = new SuperTypeElementsVisitor(false);
    private static final SuperTypeElementsVisitor EXCLUDE_JAVA_LANG = new SuperTypeElementsVisitor(true);

    public static ElementVisitor<List<TypeElement>, List<TypeElement>> getInstance(boolean excludeJavaLang) {
      return excludeJavaLang ? EXCLUDE_JAVA_LANG : WITH_JAVA_LANG;
    }

    private final boolean excludeJavaLang;

    private SuperTypeElementsVisitor(boolean excludeJavaLang) {
      this.excludeJavaLang = excludeJavaLang;
    }

    @Override
    public List<TypeElement> visitType(TypeElement e, List<TypeElement> typeElements) {
      if (excludeJavaLang && e.getQualifiedName().toString().startsWith("java.lang.")) {
        return typeElements;
      }
      TypeMirror superTypeMirror = e.getSuperclass();
      typeElements.add(e);
      TypeElement superTypeElement = asTypeElement(superTypeMirror);
      if (superTypeElement != null) { // sanity check, can not happen
        superTypeElement.accept(this, typeElements);
      }
      return typeElements;
    }
  }

  private class ExecutableElementToJavaxDAMethod implements Function<ExecutableElement, JavaxDAMethod> {
    private final JavaxExtractor javaxExtractor;
    private final TypeElement classElement;

    public ExecutableElementToJavaxDAMethod(JavaxExtractor javaxExtractor, TypeElement classElement) {
      this.javaxExtractor = javaxExtractor;
      this.classElement = classElement;
    }

    @Nullable
    @Override
    public JavaxDAMethod apply(@Nullable ExecutableElement methodElement) {
      if (methodElement == null) {
        return null;
      }

      DAMethodImpl.Builder builder = daMethodBuilder(methodElement);
      DAMethodImpl.Builder res = builder
          .withAnnotations(javaxExtractor.extractDAAnnotations(methodElement))
          .withModifiers(javaxExtractor.extractModifiers(methodElement))
          .withParameters(javaxExtractor.extractParameters(methodElement));
      if (methodElement.getKind() == ElementKind.CONSTRUCTOR) {
        res.withName(DANameFactory.from(uncapitalize(classElement.getSimpleName().toString())));
        res.withReturnType(javaxExtractor.extractType(classElement.asType()));
      }
      else {
        res.withName(JavaxDANameFactory.from(methodElement.getSimpleName()));
        res.withReturnType(javaxExtractor.extractReturnType(methodElement));
      }
      return new JavaxDAMethod(res.build(), methodElement);
    }
  }
}
