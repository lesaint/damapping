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
package fr.phan.damapping.test.fullhardcoded.mapping;

import fr.phan.damapping.annotation.Mapper;
import fr.phan.damapping.test.fullhardcoded.dto.HotelDto;
import fr.phan.damapping.test.fullhardcoded.mapping.FloorToFloorDtoMapper;
import fr.phan.damapping.test.fullhardcoded.mapping.FloorToFloorDtoMapperImpl;
import fr.phan.damapping.test.fullhardcoded.service.Hotel;

import javax.annotation.Nullable;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

/**
 * HotelToHotelDto -
 *
 * @author Sébastien Lesaint
 */
@Mapper
public class HotelToHotelDto implements Function<Hotel, HotelDto> {
    private FloorToFloorDtoMapper floorToFloorDtoMapper = new FloorToFloorDtoMapperImpl();

    @Nullable
    @Override
    public HotelDto apply(@Nullable Hotel hotel) {
        return new HotelDto(
                FluentIterable.from(hotel.getFloors()).transform(floorToFloorDtoMapper).toList()
        );
    }
}