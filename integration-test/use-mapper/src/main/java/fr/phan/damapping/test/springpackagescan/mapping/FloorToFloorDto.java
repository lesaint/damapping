/*
 * Copyright 2013 Sébastien Lesaint
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
