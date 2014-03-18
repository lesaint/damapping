package fr.javatronic.damapping.intellij.plugin.integration.psiparsing;

import fr.javatronic.damapping.processor.model.DASourceClass;

import com.intellij.psi.PsiClass;

/**
 * PsiParsingService -
 *
 * @author Sébastien Lesaint
 */
public interface PsiParsingService {
    DASourceClass parse(PsiClass psiClass);
}
