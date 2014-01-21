package fr.phan.damapping.processor.impl.javaxparsing;

import fr.phan.damapping.processor.model.*;

import javax.annotation.Nonnull;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;

/**
 * JavaxParsingService -
 *
 * @author: Sébastien Lesaint
 */
public interface JavaxParsingService {
    DASourceClass parse(TypeElement classElement);

    @Nonnull
    List<DAMethod> retrieveMethods(TypeElement classElement);

    List<DAInterface> retrieveInterfaces(TypeElement classElement);

    DAName retrievePackageName(TypeElement classElement);

    InstantiationType computeInstantiationType(TypeElement classElement, List<DAMethod> methods);
}
