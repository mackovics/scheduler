package com.avikor.scheduler.service;

import com.avikor.scheduler.domain.Booking;
import com.avikor.scheduler.domain.TimeInterval;
import com.avikor.scheduler.repository.BookingRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    private final LocalDateTime dayStart = LocalDateTime.of(2023, 6, 22, 9, 0);
    private final LocalDateTime dayEnd = LocalDateTime.of(2023, 6, 22, 17, 0);
    private final LocalDate day = LocalDate.of(2023, 6, 22);

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    public void shouldReturnFullPeriodWhenNoBookings() {
        when(bookingRepository.findByStartBetweenOrderByStartAsc(dayStart, dayEnd))
                .thenReturn(Collections.emptyList());

        List<TimeInterval> freePeriods = bookingService.getBookablePeriods(day);
        Assertions.assertEquals(1, freePeriods.size());
        Assertions.assertEquals(dayStart, freePeriods.get(0).getStart());
        Assertions.assertEquals(dayEnd, freePeriods.get(0).getEnd());
    }

    @Test
    public void shouldReturnFreePeriodsBetweenBookings() {
        Booking first = new Booking(
                LocalDateTime.of(2023, 6, 22, 9, 30),
                LocalDateTime.of(2023, 6, 22, 11,0),
                "avikor"
        );
        Booking second = new Booking(
                LocalDateTime.of(2023, 6, 22, 16, 30),
                LocalDateTime.of(2023, 6, 22, 17, 0 ),
                "avikor"
        );
        when(bookingRepository.findByStartBetweenOrderByStartAsc(dayStart, dayEnd))
                .thenReturn(List.of(first, second));

        List<TimeInterval> freePeriods = bookingService.getBookablePeriods(day);
        Assertions.assertEquals(2, freePeriods.size());

        Assertions.assertEquals(dayStart, freePeriods.get(0).getStart());
        Assertions.assertEquals(dayStart.withMinute(30), freePeriods.get(0).getEnd());

        Assertions.assertEquals(dayStart.withHour(11), freePeriods.get(1).getStart());
        Assertions.assertEquals(dayStart.withHour(16).withMinute(30), freePeriods.get(1).getEnd());
    }
}
