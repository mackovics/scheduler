package com.avikor.scheduler.integration;

import com.avikor.scheduler.domain.Booking;
import com.avikor.scheduler.repository.BookingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTests {

    private static final String OWNER = "avikor";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookingRepository bookingRepository;

    @MockBean
    private Clock clock;

    private Clock fixedClock = Clock.fixed(LocalDate.of(2050, 3, 1).atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() {
        bookingRepository.deleteAll();
    }

    @Test
    public void testRepeatedBookings() throws Exception {
        List<Booking> testBookings = createTestBookings();

        mockMvc.perform(
                        post("/booking/save")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testBookings.get(0))))
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/booking/save")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testBookings.get(1))))
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/booking/save")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testBookings.get(2))))
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/booking/save")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testBookings.get(3))))
                .andExpect(status().isOk());

        Assertions.assertEquals(4, bookingRepository.findAll().size());
    }

    @Test
    public void testTooLongBooking() throws Exception {
        Booking first = new Booking(
                LocalDateTime.of(2050, 3, 2, 9, 0),
                LocalDateTime.of(2050, 3, 2, 13, 0),
                OWNER
        );

        mockMvc.perform(
                        post("/booking/save")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(first)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testOutOfAllowedIntervalBooking() throws Exception {
        Booking first = new Booking(
                LocalDateTime.of(2050, 3, 2, 8, 30),
                LocalDateTime.of(2050, 3, 2, 10, 0),
                OWNER
        );

        mockMvc.perform(
                        post("/booking/save")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(first)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testOverLappingBookings() throws Exception {
        List<Booking> bookings = createTestBookings();
        bookingRepository.save(bookings.get(0));

        Booking second = new Booking(
                LocalDateTime.of(2050, 3, 2, 10, 30),
                LocalDateTime.of(2050, 3, 2, 11, 0),
                OWNER
        );

        mockMvc.perform(
                        post("/booking/save")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(second)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testNotOnTheHourBookings() throws Exception {
        Booking first = new Booking(
                LocalDateTime.of(2050, 3, 2, 10, 15),
                LocalDateTime.of(2050, 3, 2, 11, 0),
                OWNER
        );

        mockMvc.perform(
                        post("/booking/save")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(first)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGettingWeeklySchedule() throws Exception {
        bookingRepository.saveAll(createTestBookings());

        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        mockMvc.perform(get("/booking/getWeekly"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));

    }

    @Test
    public void testGettingFreePeriods() throws Exception {
        bookingRepository.saveAll(createTestBookings());

        mockMvc.perform(get("/booking/getFree").param("day", "2050.03.04"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void testGettingOwner() throws Exception {
        bookingRepository.saveAll(createTestBookings());

        mockMvc.perform(get("/booking/getOwner").param("time", "2050.03.04 15:30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("avikor")));
    }

    private List<Booking> createTestBookings() {
        Booking first = new Booking(
                LocalDateTime.of(2050, 3, 2, 9, 0),
                LocalDateTime.of(2050, 3, 2, 11, 0),
                OWNER
        );

        Booking second = new Booking(
                LocalDateTime.of(2050, 3, 2, 11, 0),
                LocalDateTime.of(2050, 3, 2, 13, 0),
                OWNER
        );

        Booking third = new Booking(
                LocalDateTime.of(2050, 3, 2, 13, 0),
                LocalDateTime.of(2050, 3, 2, 15, 0),
                OWNER
        );
        Booking fourth = new Booking(
                LocalDateTime.of(2050, 3, 2, 15, 0),
                LocalDateTime.of(2050, 3, 2, 17, 0),
                OWNER
        );
        Booking fifth = new Booking(
                LocalDateTime.of(2050, 3, 4, 15, 0),
                LocalDateTime.of(2050, 3, 4, 17, 0),
                OWNER
        );

        return List.of(first, second, third, fourth, fifth);
    }
}
