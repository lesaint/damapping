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
 * MapperFactory - This annotation on a public constructor or a public static method of a dedicated class (ie.
 * annotated with {@code @Mapper}) indicates that the DAMapping Framework must generate a MapperFactory interface and
 * an implementing class instead of a single Mapper interface.
 * <p>
 * <em>MapperFactory Methods</em>:<br/>
 * The generated MapperFactory interface will expose one method for each method/constructor annotated with {@code
 * @MapperFactory} in the dedicated class. These methods will all return an object that implements the generated
 * Mapper interface.
 * </p>
 * <p>
 * <em>Parameters</em>:<br/>
 * There is no requirement that methods/constructors annotated with {@code MapperFactory} have any parameter. But when
 * they do, they will all be exposed in the corresponding method of the MapperFactory interface.
 * </p>
 * <p>
 * <em>Annotated parameters</em>:<br/>
 * This behavior can be modified by annotating parameters of choice with {@code @MapperDependency}.
 * In such case only parameters which are not annotated with {@code @MapperDependency} will be exposed in the
 * corresponding method of the generated MapperFactory interface. Parameters annotated with {@code @MapperDependency}
 * will be considered as dependencies of the generated MapperFactoryImpl class and will be exposed as parameters of its
 * constructor.<br/>
 * Note that all methods/constructors annotated with {@code @MapperFactory} must have the same set of parameters
 * annotated with {@code @MapperDependency} (order does not matter and "same" is defined as same name and same type)
 * otherwise a compilation will be raised.
 * </p>
 *
 * @author Sébastien Lesaint
 */
@java.lang.annotation.Target({java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.CONSTRUCTOR})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.SOURCE)
@java.lang.annotation.Documented
public @interface MapperFactory {
}