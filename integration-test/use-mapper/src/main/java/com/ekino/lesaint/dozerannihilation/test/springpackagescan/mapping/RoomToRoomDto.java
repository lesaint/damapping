package com.ekino.lesaint.dozerannihilation.test.springpackagescan.mapping;

import javax.annotation.Nullable;
import com.google.common.base.Function;

import com.ekino.lesaint.dozerannihilation.annotation.Mapper;
import com.ekino.lesaint.dozerannihilation.test.springpackagescan.dto.RoomDto;
import com.ekino.lesaint.dozerannihilation.test.springpackagescan.service.Room;
import org.springframework.stereotype.Component;

/**
 * RoomToRoomDto -
 *
 * @author Sébastien Lesaint
 */
@Mapper
@Component
public class RoomToRoomDto implements Function<Room, RoomDto> {
    @Nullable
    @Override
    public RoomDto apply(@Nullable Room room) {
        return new RoomDto(room.getRoomNumber());
    }
}
