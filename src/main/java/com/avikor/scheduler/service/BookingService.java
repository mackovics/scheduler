package com.avikor.scheduler.service;

import com.avikor.scheduler.domain.Booking;
import com.avikor.scheduler.domain.TimeInterval;
import com.avikor.scheduler.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingValidator bookingValidator;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private Clock clock;

    public Booking bookDate(Booking booking) {
        bookingValidator.validateBooking(booking.getStart(), booking.getFinish());
        return bookingRepository.save(booking);
    }

    public List<Booking> getCurrentWeeksSchedule() {
        LocalDateTime monday = LocalDate.now(clock).with(DayOfWeek.MONDAY).atTime(9, 0);
        LocalDateTime friday = monday.toLocalDate().with(DayOfWeek.FRIDAY).atTime(17, 0);

        return bookingRepository.findByStartBetweenOrderByStartAsc(monday, friday);
    }

    public List<TimeInterval> getBookablePeriods(LocalDate day) {
        LocalDateTime start = day.atTime(9, 0);
        LocalDateTime end = day.atTime(17, 0);

        LocalTime earliestPossible = LocalTime.of(9, 0);
        List<TimeInterval> freePeriods = new ArrayList<>();

        List<Booking> dailyBookings = bookingRepository.findByStartBetweenOrderByStartAsc(start, end);
        for (Booking booking : dailyBookings) {
            if (earliestPossible.isBefore(booking.getStart().toLocalTime())) {
                freePeriods.add(new TimeInterval(day.atTime(earliestPossible.getHour(),
                        earliestPossible.getMinute()), booking.getStart()));
            }
            earliestPossible = booking.getFinish().toLocalTime();
        }

        if (earliestPossible.isBefore(end.toLocalTime())) {
            freePeriods.add(new TimeInterval(day.atTime(earliestPossible), end));
        }

        return freePeriods;
    }

    public String getBookingOwner(LocalDateTime localDateTime) {
        Optional<Booking> booking = bookingRepository.findByDate(localDateTime);
        if (booking.isPresent()) {
            return booking.get().getOwner();
        }
        return "The requested date isn't booked!";
    }
}
