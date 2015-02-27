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

import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.DAImport;
import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.factory.DANameFactory;
import fr.javatronic.damapping.processor.model.factory.DATypeFactory;
import fr.javatronic.damapping.processor.model.impl.DAAnnotationImpl;
import fr.javatronic.damapping.processor.model.impl.DAImportImpl;

/**
 * Jsr330Constants -
 *
 * @author Sébastien Lesaint
 */
public interface Jsr330Constants {

  public static final String NAMED_QUALIFIEDNAME = "javax.inject.Named";
  public static final DAName NAMED_DANAME = DANameFactory.from(NAMED_QUALIFIEDNAME);
  public static final DAAnnotation NAMED_DAANNOTATION = new DAAnnotationImpl(
      DATypeFactory.declared(NAMED_QUALIFIEDNAME)
  );
  public static final DAImport NAMED_DAIMPORT = DAImportImpl.from(NAMED_DANAME);


  public static final String INJECT_QUALIFIEDNAME = "javax.inject.Inject";
  public static final DAName INJECT_DANAME = DANameFactory.from(INJECT_QUALIFIEDNAME);
  public static final DAAnnotation INJECT_DAANNOTATION = new DAAnnotationImpl(
      DATypeFactory.declared(INJECT_QUALIFIEDNAME)
  );
  public static final DAImport INJECT_DAIMPORT = DAImportImpl.from(INJECT_DANAME);

  public static final String QUALIFIER_QUALIFIED_NAME = "javax.inject.Qualifier";
  public static final DAName QUALIFIER_DANAME = DANameFactory.from(QUALIFIER_QUALIFIED_NAME);
  public static final DAAnnotation QUALIFIER_DAANNOTATION = new DAAnnotationImpl(
      DATypeFactory.declared(QUALIFIER_QUALIFIED_NAME)
  );

  public static final String SCOPE_QUALIFIED_NAME = "javax.inject.Scope";
  public static final DAName SCOPE_DANAME = DANameFactory.from(SCOPE_QUALIFIED_NAME);
  public static final DAAnnotation SCOPE_DAANNOTATION = new DAAnnotationImpl(
      DATypeFactory.declared(SCOPE_QUALIFIED_NAME)
  );

}
