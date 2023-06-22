package com.avikor.scheduler.controller;

import com.avikor.scheduler.domain.Booking;
import com.avikor.scheduler.domain.TimeInterval;
import com.avikor.scheduler.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/booking/save")
    public Booking performBooking(@RequestBody Booking booking) {
        return bookingService.bookDate(booking);
    }

    @GetMapping("/booking/getWeekly")
    public List<Booking> getWeeklySchedule() {
        return bookingService.getCurrentWeeksSchedule();
    }

    @GetMapping("/booking/getFree")
    public List<TimeInterval> getFreePeriods(@RequestParam LocalDate day) {
        return bookingService.getBookablePeriods(day);
    }

    @GetMapping("/booking/getOwner")
    public String getOwner(@RequestParam LocalDateTime time) {
        return bookingService.getBookingOwner(time);
    }
}
