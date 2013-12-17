package fr.phan.damapping.intellij.plugin.integration;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * @author Sébastien Lesaint
 */
public class DAMappingIntegrationLoader implements ApplicationComponent {
    private static final Logger LOG = Logger.getInstance(DAMappingIntegrationLoader.class);

    @NotNull
    public String getComponentName() {
        return "DAMapping integration plugin";
    }

    public void initComponent() {
        LOG.info("DAMapping Integration plugin initialized for IntelliJ");
    }

    public void disposeComponent() {
        LOG.info("DAMapping Integration plugin disposed for IntelliJ");
    }
}
