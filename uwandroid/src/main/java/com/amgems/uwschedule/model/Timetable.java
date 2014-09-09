package com.amgems.uwschedule.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

/**
 * @author Sherman Pay
 * @version 0.1
 */
public class Timetable {
    public EnumMap<Meeting.Day, List<TimetableEvent>> table;

    public Timetable(List<Course> courses) {
        table = new EnumMap<Meeting.Day, List<TimetableEvent>>(Meeting.Day.class);
        for (Course course : courses) {
            List<Meeting> meetings = course.getMeetings();
            for (Meeting meeting : meetings) {
                for (Meeting.Day day : meeting.getMeetingDays()) {
                    List<TimetableEvent> meetingDay;
                    if (table.get(day) == null) {
                        meetingDay = new ArrayList<TimetableEvent>();
                        table.put(day, meetingDay);
                    } else {
                        meetingDay = table.get(day);
                    }
                    TimetableEvent courseMeeting = new PaddedCourseMeeting(new
                            CourseMeeting(course, meeting));
                    meetingDay.add(courseMeeting);
                }
            }
        }


        for (Meeting.Day day : table.keySet()) {
            Collections.sort(table.get(day));
        }
    }

    public TimetableEvent earliest() {
        int min = Meeting.LATEST;
        TimetableEvent result = null;
        for (Meeting.Day day : table.keySet()) {
            TimetableEvent current = table.get(day).get(0);
            if (current.getStartTime() < min) {
                min = current.getStartTime();
                result = current;
            }
        }
        return result;
    }

    public TimetableEvent latest() {
        int max = Meeting.EARLIEST;
        TimetableEvent result = null;
        for (Meeting.Day day : table.keySet()) {
            TimetableEvent current = table.get(day).get(0);
            if (current.getEndTime() < max) {
                max = current.getEndTime();
                result = current;
            }
        }
        return result;
    }

    public List<Meeting.Day> days() {
       return new ArrayList<Meeting.Day>(table.keySet());
    }

    public List<TimetableEvent> getDayEvents(Meeting.Day day) {
        return table.get(day);
    }

    public TimetableEvent get(Meeting.Day day, int i) {
        return table.get(day).get(i);
    }

    public List<TimetableEvent> toList() {
        List<TimetableEvent> result = new ArrayList<TimetableEvent>();
        for (Meeting.Day day : table.keySet()) {
            for (TimetableEvent event : getDayEvents(day)) {
                result.add(event);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return table.toString();
    }
}
