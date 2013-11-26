package fr.phan.damapping.test.fullhardcoded.mapping;

import javax.annotation.Nullable;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import fr.phan.damapping.annotation.Mapper;
import fr.phan.damapping.test.fullhardcoded.service.Floor;
import fr.phan.damapping.test.fullhardcoded.dto.FloorDto;

import fr.phan.damapping.test.fullhardcoded.mapping.RoomToRoomDtoMapper;
import fr.phan.damapping.test.fullhardcoded.mapping.RoomToRoomDtoMapperImpl;

import fr.phan.damapping.test.fullhardcoded.service.Floor;

/**
 * FloorToFloorDto -
 *
 * @author Sébastien Lesaint
 */
@Mapper
public class FloorToFloorDto implements Function<Floor, FloorDto> {
    private final RoomToRoomDtoMapper roomToRoomDto = new RoomToRoomDtoMapperImpl();

    @Nullable
    @Override
    public FloorDto apply(@Nullable Floor floor) {
        return new FloorDto(
                FluentIterable.from(floor.getRooms()).transform(roomToRoomDto).toList()
        );
    }
}
