package com.avikor.scheduler.repository;

import com.avikor.scheduler.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    boolean existsByStartBeforeAndFinishAfter(LocalDateTime newEntityEnd, LocalDateTime newEntityStart);

    List<Booking> findByStartBetweenOrderByStartAsc(LocalDateTime start, LocalDateTime finish);

    @Query("SELECT b FROM Booking b WHERE ?1 >= b.start AND ?1 < b.finish")
    Optional<Booking> findByDate(LocalDateTime dateTime);
}
