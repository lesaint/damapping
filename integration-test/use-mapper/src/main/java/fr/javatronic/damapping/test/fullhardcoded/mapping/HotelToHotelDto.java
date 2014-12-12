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
package fr.javatronic.damapping.test.fullhardcoded.mapping;

import fr.javatronic.damapping.annotation.Mapper;
import fr.javatronic.damapping.test.fullhardcoded.dto.HotelDto;
import fr.javatronic.damapping.test.fullhardcoded.service.Hotel;

import javax.annotation.Nullable;
import com.google.common.collect.FluentIterable;

/**
 * HotelToHotelDto - A mapper using another one
 *
 * @author Sébastien Lesaint
 */
@Mapper
public class HotelToHotelDto {
  private final FloorToFloorDtoMapper floorToFloorDtoMapper = new FloorToFloorDtoMapperImpl();

  @Nullable
  public HotelDto apply(@Nullable Hotel hotel) {
    return new HotelDto(
        FluentIterable.from(hotel.getFloors()).transform(floorToFloorDtoMapper).toList()
    );
  }
}
