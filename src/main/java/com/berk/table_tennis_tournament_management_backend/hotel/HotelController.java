package com.berk.table_tennis_tournament_management_backend.hotel;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/hotel")
@AllArgsConstructor
public class HotelController {

    private final HotelRepository hotelRepository;

    @GetMapping
    public List<Hotel> getHotelOptions() {
        return hotelRepository.findAll();
    }
}
