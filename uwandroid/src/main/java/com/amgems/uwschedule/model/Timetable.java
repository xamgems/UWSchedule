package com.amgems.uwschedule.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;

/**
 * @author Sherman Pay
 * @version 0.1
 */
public class Timetable {
    private EnumMap<Meeting.Day, List<PaddedCourseMeeting>> table;
    private PaddedCourseMeeting earliest;
    private PaddedCourseMeeting latest;

    public Timetable(List<Course> courses) {
        table = new EnumMap<Meeting.Day, List<PaddedCourseMeeting>>(Meeting.Day.class);
        for (Course course : courses) {
            List<Meeting> meetings = course.getMeetings();
            for (Meeting meeting : meetings) {
                for (Meeting.Day day : meeting.getMeetingDays()) {
                    List<PaddedCourseMeeting> meetingDay;
                    if (table.get(day) == null) {
                        meetingDay = new ArrayList<PaddedCourseMeeting>();
                        table.put(day, meetingDay);
                    } else {
                        meetingDay = table.get(day);
                    }
                    PaddedCourseMeeting courseMeeting = new PaddedCourseMeeting(new
                            CourseMeeting(course, meeting));
                    meetingDay.add(courseMeeting);
                    int currentPosition = meetingDay.size() - 1;
                    if (currentPosition > 0) {
                        PaddedCourseMeeting prevEvent = meetingDay.get(currentPosition - 1);
                        courseMeeting.setBeforePadding(courseMeeting.getStartTime() - prevEvent
                                .getEndTime());
                    }
                }
            }
            this.earliest = findEarliest();
            this.latest = findLatest();
            for (Meeting.Day day : table.keySet()) {
                PaddedCourseMeeting event = table.get(day).get(0);
                event.setBeforePadding(event.getStartTime() - this.earliest.getStartTime());
            }
        }


        for (Meeting.Day day : table.keySet()) {
            Collections.sort(table.get(day));
        }
    }

    public PaddedCourseMeeting getEarliest() {
        return earliest;
    }

    public PaddedCourseMeeting getLatest() {
        return latest;
    }

    private PaddedCourseMeeting findEarliest() {
        int min = Meeting.LATEST;
        PaddedCourseMeeting result = null;
        for (Meeting.Day day : table.keySet()) {
            PaddedCourseMeeting current = table.get(day).get(0);
            if (current.getStartTime() < min) {
                min = current.getStartTime();
                result = current;
            }
        }
        return result;
    }

    private PaddedCourseMeeting findLatest() {
        int max = Meeting.EARLIEST;
        PaddedCourseMeeting result = null;
        for (Meeting.Day day : table.keySet()) {
            PaddedCourseMeeting current = table.get(day).get(0);
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

    public List<PaddedCourseMeeting> getDayEvents(Meeting.Day day) {
        return table.get(day);
    }

    public PaddedCourseMeeting get(Meeting.Day day, int i) {
        return table.get(day).get(i);
    }

    public List<PaddedCourseMeeting> toVerticalList() {
        List<PaddedCourseMeeting> result = new ArrayList<PaddedCourseMeeting>();
        for (Meeting.Day day : table.keySet()) {
            for (PaddedCourseMeeting event : getDayEvents(day)) {
                result.add(event);
            }
        }
        return result;
    }

    public List<PaddedCourseMeeting> toHorizontalList() {
        List<PaddedCourseMeeting> result = new ArrayList<PaddedCourseMeeting>();
        boolean cont = true;
        for (int i = 0; cont; i++) {
            int counter = 0;
            for (Meeting.Day day : table.keySet()) {
                List<PaddedCourseMeeting> events = getDayEvents(day);
                if (i < events.size()) {
                    result.add(events.get(i));
                } else {
                    counter++;
                }

                if (counter == table.keySet().size()) {
                    cont = false;
                }
            }
        }

        return result;
    }


    @Override
    public String toString() {
        return table.toString();
    }
}
