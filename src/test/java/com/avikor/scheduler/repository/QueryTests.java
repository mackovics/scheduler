package com.avikor.scheduler.repository;

import com.avikor.scheduler.domain.Booking;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@DataJpaTest
public class QueryTests {

    private static final String OWNER = "avikor";

    @Autowired
    private BookingRepository bookingRepository;

    @MockBean
    private Clock clock;

    @BeforeEach
    public void setUp() {
        Booking first = new Booking(
                LocalDateTime.of(2050, 3, 2, 9, 0),
                LocalDateTime.of(2050, 3, 2, 11,0),
                OWNER
        );
        Booking second = new Booking(
                LocalDateTime.of(2050, 3, 2, 12, 30),
                LocalDateTime.of(2050, 3, 2, 13, 30 ),
                OWNER
        );
        bookingRepository.saveAll(List.of(first, second));
    }

    @Test
    public void shouldReturnFalseForInBetweenInterval() {
        LocalDateTime start = LocalDateTime.of(2050, 3, 2, 11,0);
        LocalDateTime end = LocalDateTime.of(2050, 3, 2, 12,30);

        Assertions.assertFalse(bookingRepository.existsByStartBeforeAndFinishAfter(end, start));
    }

    @Test
    public void shouldReturnFalseForFreeInterval() {
        LocalDateTime start = LocalDateTime.of(2050, 3, 2, 15,0);
        LocalDateTime end = LocalDateTime.of(2050, 3, 2, 16,30);

        Assertions.assertFalse(bookingRepository.existsByStartBeforeAndFinishAfter(end, start));
    }

    @Test
    public void shouldReturnTrueForOverlappingStart() {
        LocalDateTime start = LocalDateTime.of(2050, 3, 2, 10,0);
        LocalDateTime end = LocalDateTime.of(2050, 3, 2, 12,30);

        Assertions.assertTrue(bookingRepository.existsByStartBeforeAndFinishAfter(end, start));
    }

    @Test
    public void shouldReturnTrueForOverlappingEnd() {
        LocalDateTime start = LocalDateTime.of(2050, 3, 2, 12,0);
        LocalDateTime end = LocalDateTime.of(2050, 3, 2, 13,0);

        Assertions.assertTrue(bookingRepository.existsByStartBeforeAndFinishAfter(end, start));
    }

    @Test
    public void shouldReturnTrueForFullOverlap() {
        LocalDateTime start = LocalDateTime.of(2050, 3, 2, 9,0);
        LocalDateTime end = LocalDateTime.of(2050, 3, 2, 11,0);

        Assertions.assertTrue(bookingRepository.existsByStartBeforeAndFinishAfter(end, start));
    }

    @Test
    public void shouldReturnWeeklyBookings() {
        Booking third = new Booking(
                LocalDateTime.of(2050, 3, 9, 9, 0),
                LocalDateTime.of(2050, 3, 9, 11,0),
                OWNER
        );
        bookingRepository.save(third);

        LocalDateTime start = LocalDateTime.of(2050, 2, 28, 9,0);
        LocalDateTime end = LocalDateTime.of(2050, 3, 4, 17,0);

        Assertions.assertEquals(2, bookingRepository.findByStartBetweenOrderByStartAsc(start, end).size());
    }

    @Test
    public void shouldReturnBookingForExistingDate() {
        Optional<Booking> booking = bookingRepository.findByDate(LocalDateTime.of(2050, 3, 2, 9, 0));
        Assertions.assertTrue(booking.isPresent());
        Assertions.assertEquals(OWNER, booking.get().getOwner());
    }

    @Test
    public void shouldReturnEmptyOptionalForFreePeriod() {
        Optional<Booking> booking = bookingRepository.findByDate(LocalDateTime.of(2050, 3, 2, 12, 0));
        Assertions.assertTrue(booking.isEmpty());
    }
}
