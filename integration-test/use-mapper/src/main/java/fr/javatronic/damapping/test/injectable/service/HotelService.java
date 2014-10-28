/**
 * Copyright (C) 2013 Sébastien Lesaint (http://www.javatronic.fr/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.javatronic.damapping.test.injectable.service;

import java.util.List;
import javax.inject.Named;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * HotelService -
 *
 * @author Sébastien Lesaint
 */
@Named
public class HotelService {
  private Hotel hotel = buildHotel();

  private Hotel buildHotel() {
    Hotel res = new Hotel();
    res.setName("Hotel California");
    res.setFloors(
        ImmutableList.of(
            new Floor(1, ImmutableList.of(new Room("1"), new Room("2"))),
            new Floor(2, ImmutableList.of(new Room("11"), new Room("12")))
        )
    );
    return res;
  }

  public Hotel getHotel() {
    return hotel;
  }

  public List<Room> getRooms() {
    return ImmutableList.copyOf(
        Iterables.concat(
            hotel.getFloors().get(0).getRooms(),
            hotel.getFloors().get(1).getRooms()
        )
    );
  }

}
