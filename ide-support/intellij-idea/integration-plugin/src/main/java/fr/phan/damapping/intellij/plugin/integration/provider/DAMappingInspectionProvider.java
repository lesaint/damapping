package fr.phan.damapping.intellij.plugin.integration.provider;

import fr.phan.damapping.intellij.plugin.integration.inspection.DAMappingIntegrationInspection;

import com.intellij.codeInspection.InspectionToolProvider;

/**
 * @author Sébastien Lesaint
 */
public class DAMappingInspectionProvider implements InspectionToolProvider {
    @java.lang.Override
    public Class[] getInspectionClasses() {
        return new Class[] {DAMappingIntegrationInspection.class};
    }
}
