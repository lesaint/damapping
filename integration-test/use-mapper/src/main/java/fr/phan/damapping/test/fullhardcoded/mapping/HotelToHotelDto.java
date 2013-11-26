package fr.phan.damapping.test.fullhardcoded.mapping;

import javax.annotation.Nullable;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import fr.phan.damapping.annotation.Mapper;
import fr.phan.damapping.test.fullhardcoded.dto.HotelDto;

import fr.phan.damapping.test.fullhardcoded.mapping.FloorToFloorDtoMapper;
import fr.phan.damapping.test.fullhardcoded.mapping.FloorToFloorDtoMapperImpl;

import fr.phan.damapping.test.fullhardcoded.service.Hotel;

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
