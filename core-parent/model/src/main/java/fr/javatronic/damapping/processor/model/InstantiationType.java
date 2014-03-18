/*
 * Copyright 2013 Sébastien Lesaint
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.javatronic.damapping.processor.model;

/**
 * Différentes façon d'instancier la classe annotée @Mapper, pour lui déléguer l'implémentation
 * de l'interface générée *Mapper.
 */
public enum InstantiationType {
  /**
   * Type d'instantiation pour une classe annotée avec @Mapper et @Component
   */
  SPRING_COMPONENT,
  /**
   * Type d'instantiation par défaut pour une classe annotée avec @Mapper
   */
  CONSTRUCTOR,
  /**
   * Type d'instantiation par défaut pour une enum annotée avec @Mapper.
   * L'enum ne doit avoir qu'une seule valeur (singleton enum pattern)
   */
  SINGLETON_ENUM,
  /**
   * Type d'instantiation pour une classe sans constructeur par défaut mais avec un ou plusieurs
   * constructeurs annotés avec @MapperFactoryMethod
   */
  CONSTRUCTOR_FACTORY,
  /**
   * Type d'instantiation pour une classe/enum avec des méthodes statiques annotées avec @MapperFactoryMethod
   */
  STATIC_FACTORY;
}