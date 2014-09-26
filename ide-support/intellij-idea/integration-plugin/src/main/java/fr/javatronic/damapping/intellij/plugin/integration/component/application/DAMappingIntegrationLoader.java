/**
 * Copyright (C) 2013 Sébastien Lesaint (http://www.javatronic.fr/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.javatronic.damapping.intellij.plugin.integration.component.application;

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
