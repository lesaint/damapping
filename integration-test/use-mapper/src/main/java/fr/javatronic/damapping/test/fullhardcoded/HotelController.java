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
package fr.javatronic.damapping.test.fullhardcoded;

import fr.javatronic.damapping.test.fullhardcoded.dto.HotelDto;
import fr.javatronic.damapping.test.fullhardcoded.mapping.HotelToHotelDtoMapper;
import fr.javatronic.damapping.test.fullhardcoded.mapping.HotelToHotelDtoMapperImpl;
import fr.javatronic.damapping.test.fullhardcoded.service.HotelService;

/**
 * HotelController -
 *
 * @author Sébastien Lesaint
 */
public class HotelController {
  private HotelToHotelDtoMapper hotelToHotelDtoMapper = new HotelToHotelDtoMapperImpl();
  private HotelService hotelService = new HotelService();

  public HotelDto getHotel() {
    return hotelToHotelDtoMapper.apply(hotelService.getHotel());
  }
}
