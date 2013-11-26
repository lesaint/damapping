package fr.phan.damapping.test.springpackagescan.mapping;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import fr.phan.damapping.annotation.Mapper;
import fr.phan.damapping.test.springpackagescan.dto.FloorDto;
import fr.phan.damapping.test.springpackagescan.service.Floor;
import fr.phan.damapping.test.springpackagescan.dto.FloorDto;

import fr.phan.damapping.test.springpackagescan.mapping.RoomToRoomDtoMapper;

import fr.phan.damapping.test.springpackagescan.service.Floor;
import org.springframework.stereotype.Component;

/**
 * FloorToFloorDto -
 *
 * @author Sébastien Lesaint
 */
@Mapper
@Component
public class FloorToFloorDto implements Function<Floor, FloorDto> {
    @Resource
    private RoomToRoomDtoMapper roomToRoomDtoMapper;

    @Nullable
    @Override
    public FloorDto apply(@Nullable Floor floor) {
        return new FloorDto(
                FluentIterable.from(floor.getRooms()).transform(roomToRoomDtoMapper).toList()
        );
    }
}
