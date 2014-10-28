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

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
* HotelControllerConfiguration -
*
* @author Sébastien Lesaint
*/
@Configuration
public class HotelControllerConfiguration {

  @Bean
  public InjectableRoomToRoomDtoMapper getInjectableRoomToRoomDtoMapper() {
    return new InjectableRoomToRoomDtoMapperImpl();
  }

  @Bean
  @Inject
  public InjectableFloorToFloorDtoMapper getInjectableFloorToFloorDtoMapper(
      InjectableRoomToRoomDtoMapper roomToRoomDtoMapper) {
    return new InjectableFloorToFloorDtoMapperImpl(roomToRoomDtoMapper);
  }

  @Bean
  @Inject
  public InjectableHotelToHotelDtoMapper getInjectableHotelToHotelDtoMapper(
      InjectableFloorToFloorDtoMapper injectableFloorToFloorDtoMapper) {
    return new InjectableHotelToHotelDtoMapperImpl(injectableFloorToFloorDtoMapper);
  }

  @Bean
  public HotelService getHotelService() {
    return new HotelService();
  }

  @Bean
  @Inject
  public HotelController getHotelController(InjectableHotelToHotelDtoMapper injectableHotelToHotelDtoMapper,
                                            HotelService hotelService) {
    return new HotelController(injectableHotelToHotelDtoMapper, hotelService);
  }
}
