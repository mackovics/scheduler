package com.avikor.scheduler.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class TimeInterval {

    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    private LocalDateTime start;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    private LocalDateTime end;

    public TimeInterval(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return start.toString() + " - " + end.toString();
    }
}
