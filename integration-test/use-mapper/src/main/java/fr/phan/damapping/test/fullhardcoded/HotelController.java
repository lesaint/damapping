package fr.phan.damapping.test.fullhardcoded;

import fr.phan.damapping.test.fullhardcoded.dto.HotelDto;
import fr.phan.damapping.test.fullhardcoded.mapping.HotelToHotelDtoMapper;
import fr.phan.damapping.test.fullhardcoded.mapping.HotelToHotelDtoMapperImpl;
import fr.phan.damapping.test.fullhardcoded.service.HotelService;

/**
 * HotelController -
 *
 * @author Sébastien Lesaint
 */
public class HotelController {
    private HotelToHotelDtoMapper hotelToHotelDtoMapper = new HotelToHotelDtoMapperImpl();
    private HotelService hotelService = new HotelService();

    public HotelDto getHotel() {
        return hotelToHotelDtoMapper.apply(hotelService.getHotel());
    }
}
