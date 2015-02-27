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
package fr.javatronic.damapping.annotation;

/**
 * Injectable - Indicates to DAMapping that the concrete class it generates from the current Type must be an "injectable"
 * bean that any {@code JSR-330} compliant Dependency Injection framework will support.
 * <p>
 * This annotation is only valid when applied to a type (enum or class) annotated with {@link Mapper}, otherwise
 * DAMapping annotation processor will raise a compilation error.
 * </p>
 * <p>
 * In practice, if the dedicated class declares a non-default constructor, the generated MapperImpl class will declare a
 * constructor with the same parameters (as any Mapper class) but the constructor will additionally be annotated with
 * {@code javax.inject.Inject} (note that it implies that these arguments are beans which will be injected by the
 * Dependency Injection framework).
 * </p>
 * <p>
 * In addition, if the dedicated class is annotated with any annotation
 * </p>
 *
 * @author Sébastien Lesaint
 */
@java.lang.annotation.Target({java.lang.annotation.ElementType.TYPE})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.SOURCE)
@java.lang.annotation.Documented
public @interface Injectable {

}