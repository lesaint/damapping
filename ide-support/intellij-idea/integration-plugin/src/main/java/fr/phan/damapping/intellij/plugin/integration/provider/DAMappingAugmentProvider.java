package fr.phan.damapping.intellij.plugin.integration.provider;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.augment.PsiAugmentProvider;
import fr.phan.damapping.annotation.Mapper;
import fr.phan.damapping.intellij.plugin.integration.psiparsing.PsiAnnotationUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * DAMappingAugmentProvider -
 *
 * @author Sébastien Lesaint
 */
public class DAMappingAugmentProvider extends PsiAugmentProvider {
    private static final Logger LOGGER = Logger.getInstance(DAMappingAugmentProvider.class.getName());

    public DAMappingAugmentProvider() {
        LOGGER.debug("DAMappingAugmentProvider created");
    }
    @NotNull
    @Override
    public <Psi extends PsiElement> List<Psi> getAugments(@NotNull PsiElement element, @NotNull Class<Psi> type) {
        // Expecting that we are only augmenting an PsiClass
        // Don't filter !isPhysical elements or code auto completion will not work
        if (!(element instanceof PsiClass) || !element.isValid()) {
            LOGGER.debug("Not a PsiClass or no valid : " + element);
            return Collections.emptyList();
        }
        // skip processing during index rebuild
        final Project project = element.getProject();
        if (DumbService.getInstance(project).isDumb()) {
            LOGGER.debug("Index rebuilding in progress");
            return Collections.emptyList();
        }
        // TODO skip processing if plugin is disabled
        final PsiClass psiClass = (PsiClass) element;
        if (!PsiAnnotationUtil.isAnnotatedWith(psiClass, Mapper.class)) {
            LOGGER.debug("Class is not annoted with @Mapper");
            return Collections.emptyList();
        }
        PsiJavaFile javaFile = (PsiJavaFile) element.getContainingFile();
        PsiModifierList modifierList = psiClass.getModifierList();
        PsiAnnotation[] annotations = modifierList.getAnnotations();

        String mapperSrc = createMapper(psiClass);
        JavaPsiFacade.getElementFactory(project).createClassFromText(mapperSrc, element /*TODO verify what is this second argument*/);

//        PsiClass res = new LightClass(JavaPsiFacade.getElementFactory(project).createClass(psiClass.getName() + "Mapper"));

        return Collections.emptyList();
    }

    private String createMapper(PsiClass psiClass) {
        // convert PsiClass to DASource class
        // use Writer to create String instead of file from DASource class and return it
        return "";
    }
}
