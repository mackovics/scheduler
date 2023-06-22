package com.avikor.scheduler.service;

import com.avikor.scheduler.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class BookingValidator {
    static final String WEEKEND_MESSAGE = "Cannot book for the weekend!";
    static final String BUSINESS_INTERVAL_MESSAGE = "Room only available between 9am - 17pm";
    static final String BOOKING_IN_THE_PAST_MESSAGE = "Bookings are only valid for future dates!";
    static final String DURATION_MESSAGE = "Bookings have to be at least 30 minutes, and at most 3 hours long";
    static final String ON_THE_HOUR_MESSAGE = "Bookings only available starting on the hour, or on the half hour " +
            "(e.g. 10:00, 10:30)";
    static final String CONFLICT_MESSAGE = "This booking is not possible because of a conflicting meeting!";

    @Autowired
    private BookingRepository bookingRepository;

    public void validateBooking(LocalDateTime start, LocalDateTime end) {
        validateStart(start);
        validateInterval(start,end);
        validateDay(start);
        checkAvailability(start, end);
    }

    private void validateStart(LocalDateTime start) {
        if (start.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(BOOKING_IN_THE_PAST_MESSAGE);
        }

        if (start.getHour() < 9) {
            throw new IllegalArgumentException(BUSINESS_INTERVAL_MESSAGE);
        }
        if (start.getMinute() != 0 && start.getMinute() != 30) {
            throw new IllegalArgumentException(ON_THE_HOUR_MESSAGE);
        }
    }

    private void validateInterval(LocalDateTime start, LocalDateTime end) {
        if (end.getHour() >= 17 && end.getMinute() > 0) {
            throw new IllegalArgumentException(BUSINESS_INTERVAL_MESSAGE);
        }

        if (end.getMinute() != 0 && end.getMinute() != 30) {
            throw new IllegalArgumentException(ON_THE_HOUR_MESSAGE);
        }

        Duration duration = Duration.between(start, end);
        if (duration.toMinutes() < 30 || duration.toMinutes() > 180) {
            throw new IllegalArgumentException(DURATION_MESSAGE);
        }
    }

    private void validateDay(LocalDateTime start) {
        if (start.getDayOfWeek().equals(DayOfWeek.SUNDAY) || start.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
            throw new IllegalArgumentException(WEEKEND_MESSAGE);
        }
    }

    private void checkAvailability(LocalDateTime start, LocalDateTime end) {
        if (bookingRepository.existsByStartBeforeAndFinishAfter(end, start)) {
            throw new IllegalArgumentException(CONFLICT_MESSAGE);
        }
    }
}
