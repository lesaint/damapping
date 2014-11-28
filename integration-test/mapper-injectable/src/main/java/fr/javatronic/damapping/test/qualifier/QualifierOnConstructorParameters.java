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
package fr.javatronic.damapping.test.qualifier;

import fr.javatronic.damapping.annotation.Mapper;
import fr.javatronic.damapping.test.qualifier.subpackage.EnumA;

import javax.inject.Named;

/**
 * QualifierOnConstructorParameters - Demonstrates how annotations on consructor parameters are included in the
 * constructor of the generated MapperImpl class and that any type of annotation member is supported.
 *
 * @author Sébastien Lesaint
 */
@Mapper
public class QualifierOnConstructorParameters {
  private final String baseUrl;

  public QualifierOnConstructorParameters(@Named("baseUrl") String baseUrl,
                                          @Annot1(
                                              stringMember = "FOO",
                                              stringMemberArr = { "FOO", "BAR "},
                                              booleanMember = true,
                                              booleanMemberArr = { true, false},
                                              intMember = 1,
                                               intMemberArr = {1, 2},
                                              doubleMember = 1.3d,
                                              doubleMemberArr = {1.3d, 4.5d},
                                              enumMember = EnumA.DEFAULT,
                                              enumMemberArr = {EnumA.VAL1, EnumA.DEFAULT},
                                              annotMember = @Annot2
                                          ) String annotatedArg) {
    this.baseUrl = baseUrl;
  }

  public String map(Integer a) {
    return "";
  }
}
