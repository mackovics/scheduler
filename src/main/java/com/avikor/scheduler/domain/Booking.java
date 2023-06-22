package com.avikor.scheduler.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Booking {
    @Id
    @GeneratedValue
    private Integer id;

    @JsonFormat(pattern="yyyy.MM.dd HH:mm")
    private LocalDateTime start;

    @JsonFormat(pattern="yyyy.MM.dd HH:mm")
    private LocalDateTime finish;

    private String owner;

    public Booking() {
    }

    public Booking(LocalDateTime start, LocalDateTime finish, String owner) {
        this.start = start;
        this.finish = finish;
        this.owner = owner;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getFinish() {
        return finish;
    }

    public void setFinish(LocalDateTime finish) {
        this.finish = finish;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
