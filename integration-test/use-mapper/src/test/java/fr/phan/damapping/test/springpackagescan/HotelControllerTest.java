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
