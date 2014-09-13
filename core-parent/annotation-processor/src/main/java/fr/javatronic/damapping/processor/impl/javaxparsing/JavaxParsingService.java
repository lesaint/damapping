package fr.javatronic.damapping.processor.impl.javaxparsing;

import javax.annotation.Nonnull;
import javax.lang.model.element.TypeElement;

/**
 * JavaxParsingService -
 *
 * @author Sébastien Lesaint
 */
public interface JavaxParsingService {
  @Nonnull
  ParsingResult parse(@Nonnull TypeElement classElement);

}
