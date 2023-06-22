package com.avikor.scheduler.service;

import com.avikor.scheduler.repository.BookingRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingValidatorTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingValidator bookingValidator;

    @Test
    public void shouldAllowSimpleValidBookings() {
        LocalDateTime start = LocalDateTime.of(2050, 1, 3, 9, 0);
        LocalDateTime end = LocalDateTime.of(2050, 1, 3, 11, 30);
        Assertions.assertDoesNotThrow(() -> bookingValidator.validateBooking(start, end));
    }

    @Test
    public void shouldBlockBookingForPastDates() {
        LocalDateTime start = LocalDateTime.of(2018, 1, 3, 9, 0);
        LocalDateTime end = LocalDateTime.of(2018, 1, 3, 11, 30);
        Assertions.assertThrows(IllegalArgumentException.class, () -> bookingValidator.validateBooking(start, end),
                BookingValidator.BOOKING_IN_THE_PAST_MESSAGE);
    }

    @Test
    public void shouldBlockEarlyMorningBookings() {
        LocalDateTime start = LocalDateTime.of(2050, 1, 3, 8, 0);
        LocalDateTime end = LocalDateTime.of(2050, 1, 3, 10, 0);
        Assertions.assertThrows(IllegalArgumentException.class, () -> bookingValidator.validateBooking(start, end),
                BookingValidator.BUSINESS_INTERVAL_MESSAGE);
    }

    @Test
    public void shouldBlockLateEveningBookings() {
        LocalDateTime start = LocalDateTime.of(2050, 1, 3, 16, 0);
        LocalDateTime end = LocalDateTime.of(2050, 1, 3, 17, 30);
        Assertions.assertThrows(IllegalArgumentException.class, () -> bookingValidator.validateBooking(start, end),
                BookingValidator.BUSINESS_INTERVAL_MESSAGE);
    }

    @Test
    public void shouldBlockWeekendBookings() {
        LocalDateTime start = LocalDateTime.of(2050, 1, 1, 12, 30);
        LocalDateTime end = LocalDateTime.of(2050, 1, 1, 13, 0);
        Assertions.assertThrows(IllegalArgumentException.class, () -> bookingValidator.validateBooking(start, end),
                BookingValidator.WEEKEND_MESSAGE);
    }

    @Test
    public void shouldBlockTooLongBookings() {
        LocalDateTime start = LocalDateTime.of(2050, 1, 3, 9, 0);
        LocalDateTime end = LocalDateTime.of(2050, 1, 3, 12, 30);
        Assertions.assertThrows(IllegalArgumentException.class, () -> bookingValidator.validateBooking(start, end),
                BookingValidator.DURATION_MESSAGE);
    }

    @Test
    public void shouldBlockBookingsNotStartingOnTheHourOrHalfHour() {
        LocalDateTime start = LocalDateTime.of(2050, 1, 3, 9, 15);
        LocalDateTime end = LocalDateTime.of(2050, 1, 3, 12, 0);
        Assertions.assertThrows(IllegalArgumentException.class, () -> bookingValidator.validateBooking(start, end),
                BookingValidator.ON_THE_HOUR_MESSAGE);
    }

    @Test
    public void shouldBlockBookingsNotEndingOnTheHourOrHalfHour() {
        LocalDateTime start = LocalDateTime.of(2050, 1, 3, 9, 0);
        LocalDateTime end = LocalDateTime.of(2050, 1, 3, 9, 36);
        Assertions.assertThrows(IllegalArgumentException.class, () -> bookingValidator.validateBooking(start, end),
                BookingValidator.ON_THE_HOUR_MESSAGE);
    }

    @Test
    public void shouldBlockConflictingBookings() {
        LocalDateTime start = LocalDateTime.of(2050, 1, 3, 9, 0);
        LocalDateTime end = LocalDateTime.of(2050, 1, 3, 11, 30);

        when(bookingRepository.existsByStartBeforeAndFinishAfter(end, start)).thenReturn(true);

        Assertions.assertThrows(IllegalArgumentException.class, () -> bookingValidator.validateBooking(start, end),
                BookingValidator.CONFLICT_MESSAGE);
    }



}
