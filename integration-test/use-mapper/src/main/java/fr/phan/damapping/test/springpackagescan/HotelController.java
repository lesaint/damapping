package fr.phan.damapping.test.springpackagescan;

import fr.phan.damapping.test.springpackagescan.dto.HotelDto;
import fr.phan.damapping.test.springpackagescan.service.HotelService;

import javax.annotation.Resource;

import fr.phan.damapping.test.springpackagescan.mapping.HotelToHotelDtoMapper;

/**
 * HotelController -
 *
 * @author Sébastien Lesaint
 */
public class HotelController {
    @Resource
    private HotelToHotelDtoMapper hotelToHotelDtoMapper;
    @Resource
    private HotelService hotelService;

    public HotelDto getHotel() {
        return hotelToHotelDtoMapper.apply(hotelService.getHotel());
    }
}
