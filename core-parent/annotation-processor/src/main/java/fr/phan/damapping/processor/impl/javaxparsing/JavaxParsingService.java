package fr.phan.damapping.processor.impl.javaxparsing;

import fr.phan.damapping.processor.model.DAInterface;
import fr.phan.damapping.processor.model.DAMethod;
import fr.phan.damapping.processor.model.DAName;
import fr.phan.damapping.processor.model.DASourceClass;

import java.util.List;
import javax.annotation.Nonnull;
import javax.lang.model.element.TypeElement;

/**
 * JavaxParsingService -
 *
 * @author Sébastien Lesaint
 */
public interface JavaxParsingService {
  DASourceClass parse(TypeElement classElement);

  @Nonnull
  List<DAMethod> retrieveMethods(TypeElement classElement);

  List<DAInterface> retrieveInterfaces(TypeElement classElement);

  DAName retrievePackageName(TypeElement classElement);

}
