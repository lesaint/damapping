package com.ekino.lesaint.dozerannihilation.test.fullhardcoded.mapping;

import javax.annotation.Nullable;
import com.google.common.base.Function;

import com.ekino.lesaint.dozerannihilation.annotation.InstantiationType;
import com.ekino.lesaint.dozerannihilation.annotation.Mapper;
import com.ekino.lesaint.dozerannihilation.test.fullhardcoded.dto.RoomDto;
import com.ekino.lesaint.dozerannihilation.test.fullhardcoded.service.Room;

/**
 * RoomToRoomDto -
 *
 * @author Sébastien Lesaint
 */
@Mapper(InstantiationType.CONSTRUCTOR)
public class RoomToRoomDto implements Function<Room, RoomDto> {
    @Nullable
    @Override
    public RoomDto apply(@Nullable Room room) {
        return new RoomDto(room.getRoomNumber());
    }
}
