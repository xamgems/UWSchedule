package com.amgems.uwschedule.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * @author Sherman Pay
 * @version 0.1
 *
 * <p>
 * Timetable that holds TimetableEvents. Each day is a sorted list of TimetableEvents
 * (from earliest to latest).
 * </p>
 * Various Collection views of the Timetable can be obtained via its methods.
 * @see com.amgems.uwschedule.model.TimetableEvent
 *
 * TODO: Add an Interface for Course => EventGroup
 * TODO: Add an Interface for Meeting => EventSchedule
 * EventGroup has a an EventSchedule attached to it
 *
 * TODO: Add an Interface for CourseMeeting => Event
 * A EventSchedule is made up of specific Events
 *
 * TODO: Add a Class for holding a Week of EventGroup
 * A Week holds Events given an EventGroup
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
                            PaddedCourseMeeting.CourseMeeting(course, meeting, day));
                    meetingDay.add(courseMeeting);
                }
            }
        }

        for (Meeting.Day day : table.keySet()) {
            List<PaddedCourseMeeting> meetingList = getDayEvents(day);
            Collections.sort(meetingList);
            for (int i = 1; i < meetingList.size(); i++) {
                PaddedCourseMeeting prevEvent = meetingList.get(i - 1);
                PaddedCourseMeeting meeting = meetingList.get(i);
                int diff = meeting.getStartTime() - prevEvent
                        .getEndTime();
                meeting.setBeforePadding(diff);
                prevEvent.setAfterPadding(diff);
            }
        }

        this.earliest = findEarliest();
        this.latest = findLatest();
        for (Meeting.Day day : table.keySet()) {
            List<PaddedCourseMeeting> today = getDayEvents(day);
            if (today.size() > 0) {
                PaddedCourseMeeting event = today.get(0);
                event.setBeforePadding(event.getStartTime() - this.earliest.getStartTime());
                event.setFirstEvent(true);
                PaddedCourseMeeting last = today.get(today.size() - 1);
                last.setAfterPadding(this.latest.getEndTime() - last.getEndTime());
            }
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
            List<PaddedCourseMeeting> today = getDayEvents(day);
            PaddedCourseMeeting current = today.get(today.size() - 1);
            if (current.getEndTime() > max) {
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

    /**
     * Obtains a left to right view of the table in a list. The list returned will have each
     * event sorted from left to right and from top to bottom for each column.
     * @return a sorted List of TimetableEvents
     */
    public List<PaddedCourseMeeting> toLeftRightList() {
        List<PaddedCourseMeeting> result = new ArrayList<PaddedCourseMeeting>();
        for (Meeting.Day day : table.keySet()) {
            for (PaddedCourseMeeting event : getDayEvents(day)) {
                result.add(event);
            }
        }
        return result;
    }

    /**
     * Obtains a top to bottom view of the table in a list. The list returned will have each
     * event sorted from earliest to latest and from left to right for each row.
     * @return a sorted List fo TimetableEvents
     */
    public List<PaddedCourseMeeting> toTopDownList() {
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

    /**
     * Obtain a Queue view of the Timetable where the initial item of each day is pushed into
     * the queue, subsequent items are pushed in from earliest to latest based on time only,
     * and conflicts are than resolved by time of week.
     *
     * @return A Queue of TimetableEvents
     */
    public Queue<PaddedCourseMeeting> toQueue() {
        Queue<PaddedCourseMeeting> result = new LinkedList<PaddedCourseMeeting>();
        Queue<WeightedDay> queue = new PriorityQueue<WeightedDay>();

        for (Meeting.Day day : table.keySet()) {
            PaddedCourseMeeting meeting = table.get(day).get(0);
            queue.add(new WeightedDay(day, meeting.getEndTime() + meeting.getAfterPadding()));
            result.add(meeting);
        }

        while (!queue.isEmpty()) {
            WeightedDay d = queue.poll();
            List<PaddedCourseMeeting> dayEvents = table.get(d.day);
            if (d.index < dayEvents.size()) {
                PaddedCourseMeeting meeting = dayEvents.get(d.index);
                result.add(meeting);
                d.index++;
                d.weight = meeting.getEndTime() + meeting.getAfterPadding();
                queue.add(d);
            }
        }

        return result;
    }

    @Override
    public String toString() {
        return table.toString();
    }

    /**
     * Used for transforming the Timetable into a queue
     */
    static class WeightedDay implements Comparable<WeightedDay> {
        Meeting.Day day;
        int index;
        int weight;

        public WeightedDay(Meeting.Day day, int weight) {
            this.weight = weight;
            this.day = day;
            index = 1;
        }

        @Override
        public int compareTo(WeightedDay other) {
            if (this.weight == other.weight) {
                return this.day.compareTo(other.day);
            } else {
                return Integer.valueOf(this.weight).compareTo(other.weight);
            }
        }
    }


}
