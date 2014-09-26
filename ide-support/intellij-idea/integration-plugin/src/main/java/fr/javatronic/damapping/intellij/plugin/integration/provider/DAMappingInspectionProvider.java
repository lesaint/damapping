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
package fr.javatronic.damapping.intellij.plugin.integration.provider;

import fr.javatronic.damapping.intellij.plugin.integration.inspection.DAMappingIntegrationInspection;

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
