package fr.phan.damapping.intellij.plugin.integration.psiparsing;

import fr.phan.damapping.processor.model.DASourceClass;

import com.intellij.psi.PsiClass;

/**
 * PsiParsingService -
 *
 * @author Sébastien Lesaint
 */
public interface PsiParsingService {
    DASourceClass parse(PsiClass psiClass);
}
