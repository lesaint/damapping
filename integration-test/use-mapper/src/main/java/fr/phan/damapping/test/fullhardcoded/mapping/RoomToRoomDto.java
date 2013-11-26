package fr.phan.damapping.test.fullhardcoded.mapping;

import javax.annotation.Nullable;
import com.google.common.base.Function;

import fr.phan.damapping.annotation.Mapper;
import fr.phan.damapping.test.fullhardcoded.dto.RoomDto;
import fr.phan.damapping.test.fullhardcoded.service.Room;

/**
 * RoomToRoomDto -
 *
 * @author Sébastien Lesaint
 */
@Mapper
public class RoomToRoomDto implements Function<Room, RoomDto> {
    @Nullable
    @Override
    public RoomDto apply(@Nullable Room room) {
        return new RoomDto(room.getRoomNumber());
    }
}
