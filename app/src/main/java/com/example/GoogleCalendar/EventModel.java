package com.example.GoogleCalendar;

import org.joda.time.LocalDate;

import androidx.annotation.NonNull;

public class EventModel implements Comparable<EventModel> {
    private String eventname;
    private LocalDate localDate;
    private final int type;
    private int color;
    private String appointmentType;

    public void setColor(int color) {
        this.color = color;
    }

    public EventModel(String eventname, LocalDate localDate, int type) {
        this.eventname = eventname;
        this.localDate = localDate;
        this.type = type;
    }

    public String getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }

    public int getColor() {
        return color;
    }

    public int getType() {
        return type;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public String getEventname() {
        return eventname;
    }

    public void setEventname(String eventname) {
        this.eventname = eventname;
    }

    @Override
    public int compareTo(@NonNull EventModel eventModel) {
        return localDate.compareTo(eventModel.localDate);
    }
}
