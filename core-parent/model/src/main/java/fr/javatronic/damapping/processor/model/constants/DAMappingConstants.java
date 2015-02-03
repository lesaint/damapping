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
package fr.javatronic.damapping.processor.model.constants;

import fr.javatronic.damapping.annotation.MapperDependency;
import fr.javatronic.damapping.processor.model.DAImport;
import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.factory.DANameFactory;
import fr.javatronic.damapping.processor.model.factory.DATypeFactory;
import fr.javatronic.damapping.processor.model.impl.DAImportImpl;

/**
 * DAMappingConstants -
 *
 * @author Sébastien Lesaint
 */
public class DAMappingConstants {

  public static final String MAPPER_DEPENDENCY_QUALIFIEDNAME = MapperDependency.class.getName();
  public static final DAName MAPPER_DEPENDENCY_DANAME = DANameFactory.from(MAPPER_DEPENDENCY_QUALIFIEDNAME);
  public static final DAType MAPPER_DEPENDENCY_DATYPE = DATypeFactory.declared(MAPPER_DEPENDENCY_QUALIFIEDNAME);
  public static final DAImport MAPPER_DEPENDENCY_DAIMPORT = DAImportImpl.from(MAPPER_DEPENDENCY_DANAME);

}
