package com.ekino.lesaint.dozerannihilation.test.fullhardcoded;

import com.ekino.lesaint.dozerannihilation.test.fullhardcoded.dto.HotelDto;
import com.ekino.lesaint.dozerannihilation.test.fullhardcoded.mapping.HotelToHotelDtoMapper;
import com.ekino.lesaint.dozerannihilation.test.fullhardcoded.mapping.HotelToHotelDtoMapperImpl;
import com.ekino.lesaint.dozerannihilation.test.fullhardcoded.service.HotelService;

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
