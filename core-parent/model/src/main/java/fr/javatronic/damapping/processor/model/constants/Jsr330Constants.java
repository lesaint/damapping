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

import javax.annotation.Nullable;

/**
 * Jsr330Constants -
 *
 * @author Sébastien Lesaint
 */
public final class Jsr330Constants {

  public static final String NAMED_QUALIFIEDNAME = "javax.inject.Named";
  public static final DAName NAMED_DANAME = DANameFactory.from(NAMED_QUALIFIEDNAME);
  public static final DAAnnotation NAMED_DAANNOTATION = new DAAnnotation(
      DATypeFactory.declared(NAMED_QUALIFIEDNAME)
  );
  public static final DAImport NAMED_DAIMPORT = DAImport.from(NAMED_DANAME);


  public static final String INJECT_QUALIFIEDNAME = "javax.inject.Inject";
  public static final DAName INJECT_DANAME = DANameFactory.from(INJECT_QUALIFIEDNAME);
  public static final DAAnnotation INJECT_DAANNOTATION = new DAAnnotation(
      DATypeFactory.declared(INJECT_QUALIFIEDNAME)
  );
  public static final DAImport INJECT_DAIMPORT = DAImport.from(INJECT_DANAME);

  @Nullable
  private final static Class<?> jsr330NamedClass = loadJSR330InjectClass(NAMED_QUALIFIEDNAME);
  @Nullable
  private final static Class<?> jsr330InjectClass = loadJSR330InjectClass(INJECT_QUALIFIEDNAME);

  private static Class<?> loadJSR330InjectClass(String qualifiedName) {
    try {
      return Class.forName(qualifiedName);
    } catch (ClassNotFoundException e) {
      return null;
    }
  }

  public static boolean isJSR330Present() {
    return jsr330InjectClass != null && jsr330NamedClass != null;
  }

  private Jsr330Constants() {
    // prevents instantiation
  }
}
