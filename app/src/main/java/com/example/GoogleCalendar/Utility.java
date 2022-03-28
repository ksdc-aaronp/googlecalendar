package com.example.GoogleCalendar;

import android.graphics.Color;
import android.util.Log;

import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.TimeZone;


public class Utility {

    private static int eventId = 1;

    public static HashMap<LocalDate, EventInfo> localDateHashMap = new HashMap<>();

    public static HashMap<LocalDate, EventInfo> readCalendarEvent() {
        createBlueFileAppointments();
        return localDateHashMap;
    }

    private static void createBlueFileAppointments() {
        createFullDayEvent("Sales Conference", LocalDate.now().minusDays(4));
        createCallAppointment("Checkmart Crescent O/P  CHCR", -3, 9, 0,
                11, 0, false);
        createAppointmentInterval(-3, 11, 0, 12, 0);
        createCallAppointment("Choppies Durban Dist Centre", -3, 11, 0,
                12, 0, false);
        createNonCallAppointment("Lunch break", -3, 12, 0, 13, 0);
        createCallAppointment("Checkmart Crescent O/P  CHCR", -3, 14, 0,
                16, 0, true);
        createFullDayEvent("Annual Leave", LocalDate.now().minusDays(5));
        createCallAppointment("Makro Amanzimtoti      M25", -1, 8, 0,
                10, 0, true);
        createCallAppointment("Cambridge Food DC MDD", -1, 10, 30,
                11, 30, true);
        createNonCallAppointment("Lunch break", -1, 12, 0, 13, 0);
        createCallAppointment("Game KZN RDC Riverhorse  140", -1, 13, 0,
                16, 0, false);
        createCallAppointment("Oxford Tara Rd", 0, 7, 30,
                9, 30, true);
        createCallAppointment("Mtubatuba Cash & Carry", 0, 11, 0,
                12, 0, false);
        createNonCallAppointment("Lunch break", 0, 12, 0, 13, 0);
        createCallAppointment("Take n Pay Foodtown Chatsworth", 0, 14, 0,
                16, 0, false);
        createNonCallAppointment("An introduction to Lean-Thinking", 1, 9, 0, 16, 0);
        createNonCallAppointment("Meeting @ SGX", 2, 8, 15, 10, 15);
        createCallAppointment("Boxer Logra Park DC C/Rg 251", 2, 11, 0,
                13, 0, false);
        createCallAppointment("Take n Pay Foodtown Chatsworth", 2, 10, 15,
                10, 30, false);
        createNonCallAppointment("Meeting with insurance broker", 2, 13, 0, 13, 30);
        createCallAppointment("Panjivan Powertrade C&C", 2, 14, 0,
                17, 0, false);
        createFullDayEvent("Annual Leave", LocalDate.now().plusDays(3));
        createCallAppointment("Makro Amanzimtoti      M25", 4, 9, 0,
                11, 0, false);
        createCallAppointment("Makro Springfield        M07", 4, 12, 0,
                14, 0, false);
        createNonCallAppointment("AdHoc-Budget meeting", 4, 15, 0, 16, 30);
    }

    public static void createFullDayEvent(String title, LocalDate localDate) {
        EventInfo eventInfo = new EventInfo();
        eventInfo.accountname = "AaronP";
        eventInfo.timezone = TimeZone.getTimeZone("Africa/Johannesburg").getID();
        eventInfo.eventtitles = new String[]{title};
        eventInfo.isallday = true;
        eventInfo.title = title;
        eventInfo.id = eventId++;
        eventInfo.appointmentType = "Full-Day";
        eventInfo.eventcolor = getEventColor(eventInfo);
        localDateHashMap.put(localDate, eventInfo);
    }

    public static void createNewEvent(String title, LocalDateTime startTime, LocalDateTime endTime,
                                      String appointmentType, boolean complete) {
        EventInfo eventInfo = new EventInfo();
        eventInfo.starttime = Instant.parse(getDateTime(startTime)).getMillis();
        eventInfo.endtime = Instant.parse(getDateTime(endTime)).getMillis();
        eventInfo.accountname = "AaronP";
        eventInfo.timezone = TimeZone.getTimeZone("Africa/Johannesburg").getID();
        eventInfo.eventtitles = new String[]{title};
        eventInfo.isallday = false;
        eventInfo.title = title;
        eventInfo.id = eventId++;
        eventInfo.complete = complete;
        eventInfo.appointmentType = appointmentType;
        eventInfo.eventcolor = getEventColor(eventInfo);

        localDateHashMap.put(startTime.toLocalDate(), eventInfo);
    }

    public static void createSameDayEvent(EventInfo eventInfo, String title, LocalDateTime startTime,
                                          LocalDateTime endTime, String appointmentType, boolean complete) {
        EventInfo prevEventInfo = eventInfo;
        while (prevEventInfo.nextnode != null) {
            prevEventInfo = prevEventInfo.nextnode;
        }
        String[] s = eventInfo.eventtitles;
        String ss[] = Arrays.copyOf(s, s.length + 1);
        ss[ss.length - 1] = title;
        eventInfo.eventtitles = ss;
        EventInfo nextNode = new EventInfo();
        nextNode.id = eventId++;
        nextNode.starttime = Instant.parse(getDateTime(startTime)).getMillis();
        nextNode.endtime = Instant.parse(getDateTime(endTime)).getMillis();
        nextNode.isallday = false;
        nextNode.timezone = TimeZone.getTimeZone("Africa/Johannesburg").getID();
        nextNode.title = title;
        nextNode.accountname = "AaronP";
        nextNode.noofdayevent = 0;
        nextNode.appointmentType = appointmentType;
        nextNode.complete = complete;
        nextNode.eventcolor = getEventColor(nextNode);
        prevEventInfo.nextnode = nextNode;
        localDateHashMap.put(startTime.toLocalDate(), eventInfo);
    }

    public static int getEventColor(EventInfo eventInfo) {
        if (eventInfo.appointmentType.equals("Call")) {
            if (eventInfo.complete) {
                return Color.parseColor("#009688");
            } else {
                LocalDateTime aptTime = Instant.ofEpochMilli(eventInfo.endtime).toDateTime().toLocalDateTime();
                if (LocalDateTime.now().isAfter(aptTime)) {
                    return Color.parseColor("#f97f7f");
                } else return Color.parseColor("#98C4E5");
            }
        } else if (eventInfo.appointmentType.equals("Non-Call")) {
            return Color.parseColor("#ffc391");
        } else if (eventInfo.appointmentType.equals("Full-Day")) {
            return Color.parseColor("#ff89f4");
        } else if (eventInfo.appointmentType.equals("Interval")) {
            return Color.parseColor("#ffffff");
        } else
            return Color.parseColor("#98C4E5");
    }

    public static String getDateTime(LocalDateTime localDateTime) {
        return formatDateTime(localDateTime, "yyyy-MM-dd'T'HH:mm:ss.SSZ");
    }

    public static String formatDateTime(LocalDateTime localDateTime, String dateTimePattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(dateTimePattern);
        return dateTimeFormatter.print(localDateTime);
    }

    public static void createAppointment(String location, String appointmentType, int dayOffset, int startHour,
                                         int startMin, int endHour, int endMin, boolean complete) {
        LocalDateTime startDateTime = LocalDateTime.now()
                .plusDays(dayOffset).withTime(startHour, startMin, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.now()
                .plusDays(dayOffset).withTime(endHour, endMin, 0, 0);
        LocalDate appointmentDate = startDateTime.toLocalDate();

        if (!localDateHashMap.containsKey(appointmentDate)) {
            createNewEvent(location, startDateTime, endDateTime, appointmentType, complete);
        } else {
            EventInfo eventInfo = localDateHashMap.get(appointmentDate);
            createSameDayEvent(eventInfo, location, startDateTime, endDateTime, appointmentType, complete);
        }
    }

    public static void createCallAppointment(String location, int dayOffset, int startHour,
                                             int startMin, int endHour, int endMin, boolean complete) {
        createAppointment(location, "Call", dayOffset, startHour, startMin, endHour,
                endMin, complete);
    }

    public static void createNonCallAppointment(String location, int dayOffset, int startHour,
                                                int startMin, int endHour, int endMin) {
        createAppointment(location, "Non-Call", dayOffset, startHour, startMin, endHour,
                endMin, false);
    }

    public static void createAppointmentInterval(int dayOffset, int startHour, int startMin,
                                                 int endHour, int endMin) {
        createAppointment("Interval", "Interval", dayOffset, startHour, startMin, endHour,
                endMin, false);
    }

    public static LocalDate getDate(long milliSeconds) {
        Instant instantFromEpochMilli
                = Instant.ofEpochMilli(milliSeconds);
        return instantFromEpochMilli.toDateTime(DateTimeZone.getDefault()).toLocalDate();
    }


}