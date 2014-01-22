package fr.phan.damapping.intellij.plugin.integration.psiparsing;

import com.intellij.psi.PsiClass;
import fr.phan.damapping.processor.model.DASourceClass;

/**
 * PsiParsingService -
 *
 * @author: Sébastien Lesaint
 */
public interface PsiParsingService {
    DASourceClass parse(PsiClass psiClass);
}
