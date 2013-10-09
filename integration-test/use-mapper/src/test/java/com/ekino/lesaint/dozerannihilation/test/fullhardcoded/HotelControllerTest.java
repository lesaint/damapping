package com.ekino.lesaint.dozerannihilation.test.fullhardcoded;

import com.ekino.lesaint.dozerannihilation.test.fullhardcoded.dto.HotelDto;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HotelControllerTest -
 *
 * @author Sébastien Lesaint
 */
public class HotelControllerTest {

    private final HotelController hotelController = new HotelController();

    @Test
    public void testGetHotel() throws Exception {
        HotelDto hotel = hotelController.getHotel();
        assertThat(hotel.getFloors()).hasSize(2);
        assertThat(hotel.getFloors().get(0).getRooms()).extracting("number").containsExactly("1", "2");
        assertThat(hotel.getFloors().get(1).getRooms()).extracting("number").containsExactly("11", "12");
    }
}
