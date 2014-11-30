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
package fr.javatronic.damapping.processor.impl;

import fr.javatronic.damapping.processor.DAAnnotationProcessor;
import fr.javatronic.damapping.processor.impl.javaxparsing.ReferenceScanResult;
import fr.javatronic.damapping.processor.impl.javaxparsing.JavaxExtractorImpl;
import fr.javatronic.damapping.processor.impl.javaxparsing.ProcessingContext;
import fr.javatronic.damapping.processor.impl.javaxparsing.ReferencesScanner;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.util.Function;
import fr.javatronic.damapping.util.Optional;
import fr.javatronic.damapping.util.Predicate;
import fr.javatronic.damapping.util.Sets;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.AnnotationValueVisitor;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.SimpleAnnotationValueVisitor6;
import javax.lang.model.util.SimpleElementVisitor7;

import static fr.javatronic.damapping.util.FluentIterable.from;
import static fr.javatronic.damapping.util.Preconditions.checkNotNull;
import static fr.javatronic.damapping.util.Predicates.notNull;
import static javax.tools.Diagnostic.Kind.ERROR;

/**
 * GeneratedAnnotationProcessor -
 *
 * @author Sébastien Lesaint
 */
public class GeneratedAnnotationProcessor extends AbstractAnnotationProcessor<Generated> {

  private static final Set<ElementKind> SUPPORTED_ELEMENTKINDS = Sets.of(
      ElementKind.CLASS, ElementKind.INTERFACE
  );
  private static final AnnotationElementValueName VALUE_ANNOTATION_PARAMETER = new AnnotationElementValueName("value");

  @Nonnull
  private final ProcessingContext processingContext;

  public GeneratedAnnotationProcessor(ProcessingEnvironment processingEnv, @Nonnull ProcessingContext processingContext) {
    super(processingEnv, Generated.class);
    this.processingContext = checkNotNull(processingContext);
  }

  @Override
  protected void processNewElement(Element element, RoundEnvironment roundEnv) throws IOException {
    if (!SUPPORTED_ELEMENTKINDS.contains(element.getKind())) {
      // Elements class and interfaces are the only Elements generated by DAMapping which are annotated with @Generated
      // ignore the others (which most likely come from other tools)
      return;
    }

    if (!isGeneratedByDAMapping(element)) {
      return;
    }

    // cast is safe because we checked the kind right above
    ReferenceScanResult scanResult =  new ReferencesScanner(processingEnv, null).scan((TypeElement) element);

    if (!scanResult.hasUnresolved()) {
      JavaxExtractorImpl javaxExtractor = new JavaxExtractorImpl(processingEnv, scanResult);
      DAType type = javaxExtractor.extractType(element.asType());
      processingContext.addGenerated(type);
    }
    else {
      // very inlikely to occur
      processingEnv.getMessager().printMessage(ERROR,
          String.format(
              "Type %s generated by DAMapping has unresolved references (%s). Compilation will fail",
              element, scanResult.getUnresolved()
          ), element
      );
    }

  }

  private boolean isGeneratedByDAMapping(Element element) {
    // retrieve the AnnotationMirror of @Generated on the current Element
    Optional<? extends AnnotationMirror> generatedAnnotationMirror = from(element.getAnnotationMirrors()).filter(
        new Predicate<AnnotationMirror>() {
          @Override
          public boolean apply(@Nullable AnnotationMirror o) {
            if (o == null) {
              return false;
            }
            Name qualifiedName = ElementQualifiedNameExtractor.INSTANCE.visit(o.getAnnotationType().asElement());
            return qualifiedName != null && qualifiedName.contentEquals(Generated.class.getCanonicalName());
          }
        }
    ).first();
    if (!generatedAnnotationMirror.isPresent()) {
      // can not happen since process only Element annotated with the @Generated
      return false;
    }

    Optional<String> processorName = from(generatedAnnotationMirror.get().getElementValues().entrySet())
        .filter(VALUE_ANNOTATION_PARAMETER)
        .transform(AnnotationElementValueEntryToStringValue.INSTANCE)
        .filter(notNull())
        .first();

    return processorName.isPresent() && DAAnnotationProcessor.class.getCanonicalName().equals(processorName.get());
  }

  @Override
  public void processPostponed(boolean lastRound) {
    // does not apply to generated types
  }

  /**
   * Implementation of {@link AnnotationValueVisitor} that visits a {@link AnnotationValue} to extract
   * its {@link String} value or the String in its one element {@link String} array.
   * <li>
   *   <ul>for {@code @Acme("foo"}, returns {@code "foo"}</ul>
   *   <ul>for {@code @Acme({"foo"}}, returns {@code "foo"}</ul>
   *   <ul>for {@code @Acme({"foo", "bar}}, returns {@code null}</ul>
   * </li>
   */
  private static class StringAnnotationValueVisitor extends SimpleAnnotationValueVisitor6<String, Object> {
    @Override
    public String visitString(String s, Object o) {
      return s;
    }

    @Override
    public String visitArray(List<? extends AnnotationValue> vals, Object o) {
      if (vals.isEmpty() || vals.size() > 1) {
        return null;
      }

      return new StringAnnotationValueVisitor().visit(vals.iterator().next());
    }
  }

  /**
   * Implementation of {@link SimpleElementVisitor7} that visits a {@link Element} to extract its {@code simpleName}.
   */
  private static class ElementSimpleNameExtractor extends SimpleElementVisitor7<Name, Void> {
    public static final ElementVisitor<Name, Void> INSTANCE = new ElementSimpleNameExtractor();

    private ElementSimpleNameExtractor() {
      // prevents instantiation
    }

    @Override
    public Name visitVariable(VariableElement e, Void aVoid) {
      return e.getSimpleName();
    }

    @Override
    public Name visitPackage(PackageElement e, Void aVoid) {
      return e.getSimpleName();
    }

    @Override
    public Name visitType(TypeElement e, Void aVoid) {
      return e.getSimpleName();
    }

    @Override
    public Name visitTypeParameter(TypeParameterElement e, Void aVoid) {
      return e.getSimpleName();
    }

    @Override
    public Name visitExecutable(ExecutableElement e, Void aVoid) {
      return e.getSimpleName();
    }
  }

  /**
   * Implementation of {@link SimpleElementVisitor7} that visits a {@link Element} to extract its {@code qualifiedName}.
   */
  private static class ElementQualifiedNameExtractor extends SimpleElementVisitor7<Name, Void> {
    private static final ElementVisitor<Name, Void> INSTANCE = new ElementQualifiedNameExtractor();

    private ElementQualifiedNameExtractor() {
      // prevents instantiation
    }

    @Override
    public Name visitVariable(VariableElement e, Void o) {
      return null;
    }

    @Override
    public Name visitPackage(PackageElement e, Void o) {
      return e.getQualifiedName();
    }

    @Override
    public Name visitExecutable(ExecutableElement e, Void o) {
      return null;
    }

    @Override
    public Name visitTypeParameter(TypeParameterElement e, Void o) {
      return null;
    }

    @Override
    public Name visitType(TypeElement e, Void o) {
      return e.getQualifiedName();
    }
  }

  private static enum AnnotationElementValueEntryToStringValue
      implements Function<Map.Entry<? extends ExecutableElement, ? extends AnnotationValue>, String> {
    INSTANCE;

    @Nullable
    @Override
    public String apply(@Nullable Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry) {
      if (entry == null) {
        return null;
      }
      return new StringAnnotationValueVisitor().visit(entry.getValue());
    }
  }

  private static class AnnotationElementValueName
      implements Predicate<Map.Entry<? extends ExecutableElement, ? extends AnnotationValue>> {
    private final String elementName;

    public AnnotationElementValueName(String elementName) {
      this.elementName = elementName;
    }

    @Override
    public boolean apply(@Nullable Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry) {
      if (entry == null) {
        return false;
      }
      Name simpleName = ElementSimpleNameExtractor.INSTANCE.visit(entry.getKey());
      return simpleName != null && simpleName.contentEquals(elementName);
    }
  }
}
