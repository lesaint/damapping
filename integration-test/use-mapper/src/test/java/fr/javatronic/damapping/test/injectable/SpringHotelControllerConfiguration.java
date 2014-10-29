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
package fr.javatronic.damapping.test.injectable;

import fr.javatronic.damapping.test.injectable.mapping.InjectableFloorToFloorDtoMapper;
import fr.javatronic.damapping.test.injectable.mapping.InjectableFloorToFloorDtoMapperImpl;
import fr.javatronic.damapping.test.injectable.mapping.InjectableHotelToHotelDtoMapper;
import fr.javatronic.damapping.test.injectable.mapping.InjectableHotelToHotelDtoMapperImpl;
import fr.javatronic.damapping.test.injectable.mapping.InjectableRoomToRoomDtoMapper;
import fr.javatronic.damapping.test.injectable.mapping.InjectableRoomToRoomDtoMapperImpl;
import fr.javatronic.damapping.test.injectable.service.HotelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringHotelControllerConfiguration - {@link Configuration} class for {@link SpringHotelControllerTest}.
 * <p/>
 * Using only Spring's specific annotations ({@link javax.inject.Inject} could be used in place of {@link Autowired})
 * to avoid interferences with {@link DaggerHotelControllerTest} which fails because it identifies the objec's methods
 * annotated with {@link javax.inject.Inject} as injected method and throws a compilation error.
 *
 * @author Sébastien Lesaint
 */
@Configuration
public class SpringHotelControllerConfiguration {

  @Bean
  public InjectableRoomToRoomDtoMapper getInjectableRoomToRoomDtoMapper() {
    return new InjectableRoomToRoomDtoMapperImpl();
  }

  @Bean
  @Autowired
  public InjectableFloorToFloorDtoMapper getInjectableFloorToFloorDtoMapper(
      InjectableRoomToRoomDtoMapper roomToRoomDtoMapper) {
    return new InjectableFloorToFloorDtoMapperImpl(roomToRoomDtoMapper);
  }

  @Bean
  @Autowired
  public InjectableHotelToHotelDtoMapper getInjectableHotelToHotelDtoMapper(
      InjectableFloorToFloorDtoMapper injectableFloorToFloorDtoMapper) {
    return new InjectableHotelToHotelDtoMapperImpl(injectableFloorToFloorDtoMapper);
  }

  @Bean
  public HotelService getHotelService() {
    return new HotelService();
  }

  @Bean
  @Autowired
  public HotelController getHotelController(InjectableHotelToHotelDtoMapper injectableHotelToHotelDtoMapper,
                                            HotelService hotelService) {
    return new HotelController(injectableHotelToHotelDtoMapper, hotelService);
  }
}
