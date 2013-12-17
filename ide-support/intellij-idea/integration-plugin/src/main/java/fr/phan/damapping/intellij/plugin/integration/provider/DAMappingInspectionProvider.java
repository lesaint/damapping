package fr.phan.damapping.intellij.plugin.integration.provider;

import com.intellij.codeInspection.InspectionToolProvider;
import fr.phan.damapping.intellij.plugin.integration.inspection.DAMappingIntegrationInspection;

/**
 * @author Sébastien Lesaint
 */
public class DAMappingInspectionProvider implements InspectionToolProvider {
    @java.lang.Override
    public Class[] getInspectionClasses() {
        return new Class[] {DAMappingIntegrationInspection.class};
    }
}
