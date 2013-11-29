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

import fr.phan.damapping.annotation.Mapper;
import fr.phan.damapping.test.springpackagescan.dto.RoomDto;
import fr.phan.damapping.test.springpackagescan.service.Room;

import javax.annotation.Nullable;
import com.google.common.base.Function;

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
