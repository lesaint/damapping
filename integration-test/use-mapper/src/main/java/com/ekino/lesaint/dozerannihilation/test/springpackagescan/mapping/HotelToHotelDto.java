package com.ekino.lesaint.dozerannihilation.test.springpackagescan.mapping;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import com.ekino.lesaint.dozerannihilation.annotation.Mapper;
import com.ekino.lesaint.dozerannihilation.test.springpackagescan.dto.HotelDto;
import com.ekino.lesaint.dozerannihilation.test.springpackagescan.mapping.FloorToFloorDtoMapper;
import com.ekino.lesaint.dozerannihilation.test.springpackagescan.mapping.FloorToFloorDtoMapperImpl;
import com.ekino.lesaint.dozerannihilation.test.springpackagescan.service.Hotel;
import org.springframework.stereotype.Component;

/**
 * HotelToHotelDto -
 *
 * @author Sébastien Lesaint
 */
@Component
@Mapper
public class HotelToHotelDto implements Function<Hotel, HotelDto> {
    @Resource
    private FloorToFloorDtoMapper floorToFloorDtoMapper;

    @Nullable
    @Override
    public HotelDto apply(@Nullable Hotel hotel) {
        return new HotelDto(
                FluentIterable.from(hotel.getFloors()).transform(floorToFloorDtoMapper).toList()
        );
    }
}
