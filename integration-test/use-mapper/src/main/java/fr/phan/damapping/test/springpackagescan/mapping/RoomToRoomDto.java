package fr.phan.damapping.test.springpackagescan.mapping;

import javax.annotation.Nullable;
import com.google.common.base.Function;

import fr.phan.damapping.annotation.Mapper;
import fr.phan.damapping.test.springpackagescan.dto.RoomDto;
import fr.phan.damapping.test.springpackagescan.service.Room;
import fr.phan.damapping.test.springpackagescan.dto.RoomDto;
import fr.phan.damapping.test.springpackagescan.service.Room;
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
