package com.ekino.lesaint.dozerannihilation.test.fullhardcoded.mapping;

import javax.annotation.Nullable;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import com.ekino.lesaint.dozerannihilation.annotation.Mapper;
import com.ekino.lesaint.dozerannihilation.test.fullhardcoded.dto.HotelDto;
import com.ekino.lesaint.dozerannihilation.test.fullhardcoded.service.Hotel;

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
