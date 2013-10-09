package com.ekino.lesaint.dozerannihilation.test.springpackagescan;

import javax.annotation.Resource;

import com.ekino.lesaint.dozerannihilation.test.springpackagescan.dto.HotelDto;
import com.ekino.lesaint.dozerannihilation.test.springpackagescan.mapping.HotelToHotelDtoMapper;
import com.ekino.lesaint.dozerannihilation.test.springpackagescan.mapping.HotelToHotelDtoMapperImpl;
import com.ekino.lesaint.dozerannihilation.test.springpackagescan.service.HotelService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

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
