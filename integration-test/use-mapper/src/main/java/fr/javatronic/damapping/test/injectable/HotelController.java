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

import fr.javatronic.damapping.test.injectable.dto.HotelDto;
import fr.javatronic.damapping.test.injectable.mapping.InjectableHotelToHotelDtoMapper;
import fr.javatronic.damapping.test.injectable.service.HotelService;

import javax.inject.Inject;

/**
 * HotelController -
 *
 * @author Sébastien Lesaint
 */
public class HotelController {
  private final InjectableHotelToHotelDtoMapper injectableHotelToHotelDtoMapper;
  private final HotelService hotelService;

  @Inject
  public HotelController(InjectableHotelToHotelDtoMapper injectableHotelToHotelDtoMapper, HotelService hotelService) {
    this.injectableHotelToHotelDtoMapper = injectableHotelToHotelDtoMapper;
    this.hotelService = hotelService;
  }

  public HotelDto getHotel() {
    return injectableHotelToHotelDtoMapper.apply(hotelService.getHotel());
  }
}
