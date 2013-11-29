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
package fr.phan.damapping.test.springpackagescan;

import fr.phan.damapping.test.springpackagescan.HotelController;
import fr.phan.damapping.test.springpackagescan.dto.HotelDto;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HotelControllerTest -
 *
 * @author Sébastien Lesaint
 */
public class HotelControllerTest {

    private final HotelController hotelController = new HotelController();

    @BeforeClass
    public void setup() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext("fr.phan.damapping.test.springpackagescan");
        ctx.getAutowireCapableBeanFactory().autowireBean(hotelController);
        ctx.start();
    }

    @Test
    public void testGetHotel() throws Exception {
        HotelDto hotel = hotelController.getHotel();
        assertThat(hotel.getFloors()).hasSize(2);
        assertThat(hotel.getFloors().get(0).getRooms()).extracting("number").containsExactly("1", "2");
        assertThat(hotel.getFloors().get(1).getRooms()).extracting("number").containsExactly("11", "12");
    }
}
