package fr.phan.damapping.test.springpackagescan.mapping;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import fr.phan.damapping.annotation.Mapper;
import fr.phan.damapping.test.springpackagescan.dto.HotelDto;
import fr.phan.damapping.test.springpackagescan.service.Hotel;
import fr.phan.damapping.test.springpackagescan.dto.HotelDto;
import fr.phan.damapping.test.springpackagescan.mapping.FloorToFloorDtoMapper;
import fr.phan.damapping.test.springpackagescan.mapping.FloorToFloorDtoMapperImpl;
import fr.phan.damapping.test.springpackagescan.service.Hotel;
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
