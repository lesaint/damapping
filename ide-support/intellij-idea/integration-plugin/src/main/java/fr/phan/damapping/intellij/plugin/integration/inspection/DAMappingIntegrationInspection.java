package fr.phan.damapping.intellij.plugin.integration.inspection;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import org.jetbrains.annotations.NotNull;

/**
 * DAMappingIntegrationInspection -
 *
 * @author Sébastien Lesaint
 */
public class DAMappingIntegrationInspection extends BaseJavaLocalInspectionTool {

    @NotNull
    @Override
    public String getDisplayName() {
        return "DAMapping annotations inspection";
    }

    @NotNull
    @Override
    public String getGroupDisplayName() {
        return GroupNames.BUGS_GROUP_NAME;
    }

    @NotNull
    @Override
    public String getShortName() {
        return "DAMapping";
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}
