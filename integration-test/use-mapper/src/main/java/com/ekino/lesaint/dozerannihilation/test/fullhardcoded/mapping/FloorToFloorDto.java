package com.ekino.lesaint.dozerannihilation.test.fullhardcoded.mapping;

import javax.annotation.Nullable;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import com.ekino.lesaint.dozerannihilation.annotation.InstantiationType;
import com.ekino.lesaint.dozerannihilation.annotation.Mapper;
import com.ekino.lesaint.dozerannihilation.test.fullhardcoded.dto.FloorDto;
import com.ekino.lesaint.dozerannihilation.test.fullhardcoded.service.Floor;

/**
 * FloorToFloorDto -
 *
 * @author Sébastien Lesaint
 */
@Mapper(InstantiationType.CONSTRUCTOR)
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
