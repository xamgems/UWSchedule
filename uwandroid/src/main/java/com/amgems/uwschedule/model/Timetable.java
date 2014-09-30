package com.amgems.uwschedule.model;

import android.support.annotation.NonNull;
import android.util.Log;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * <p>
 * Timetable that holds TimetableEvents. Each day is a sorted list of TimetableEvents
 * (from earliest to latest).
 * </p>
 * Various Collection views of the Timetable can be obtained via its methods.
 * @see com.amgems.uwschedule.model.TimetableEvent
 *
 * @author Sherman Pay
 */
public class Timetable {
    private EnumMap<Meeting.Day, List<TimetableEvent>> table;
    private TimetableEvent earliest;
    private TimetableEvent latest;

    public Timetable(@NonNull List<Course> courses) {
        checkNotNull(courses);
        table = new EnumMap<Meeting.Day, List<TimetableEvent>>(Meeting.Day.class);
        if (!courses.isEmpty()) {
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
                                CourseMeeting(course, meeting, day));
                        meetingDay.add(courseMeeting);
                    }
                }
            }

            for (Meeting.Day day : table.keySet()) {
                List<TimetableEvent> meetingList = table.get(day);
                Collections.sort(meetingList);
                for (int i = 1; i < meetingList.size(); i++) {
                    TimetableEvent prevEvent = meetingList.get(i - 1);
                    TimetableEvent meeting = meetingList.get(i);
                    int diff = meeting.getStartTime() - prevEvent
                            .getEndTime();
                    meeting.setBeforePadding(diff);
                    prevEvent.setAfterPadding(diff);
                }
            }

            this.earliest = findEarliest();
            this.latest = findLatest();
            for (Meeting.Day day : table.keySet()) {
                List<TimetableEvent> today = table.get(day);
                if (today.size() > 0) {
                    TimetableEvent event = today.get(0);
                    event.setBeforePadding(event.getStartTime() - this.earliest.getStartTime());
                    event.setFirstEvent(true);
                    TimetableEvent last = today.get(today.size() - 1);
                    last.setAfterPadding(this.latest.getEndTime() - last.getEndTime());
                }
            }
        } else {
            Log.d(getClass().getSimpleName(), "Empty List of Courses while initializing Timtable");
        }
    }

    public TimetableEvent getEarliest() {
        return earliest;
    }

    public TimetableEvent getLatest() {
        return latest;
    }

    private TimetableEvent findEarliest() {
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

    private TimetableEvent findLatest() {
        int max = Meeting.EARLIEST;
        TimetableEvent result = null;
        for (Meeting.Day day : table.keySet()) {
            List<TimetableEvent> today = table.get(day);
            TimetableEvent current = today.get(today.size() - 1);
            if (current.getEndTime() > max) {
                max = current.getEndTime();
                result = current;
            }
        }
        return result;
    }

    /**
     * Obtain a list days that this timetable is holding.
     * This list is not a view of the internal representation
     * @return  List of Days
     */
    public List<Meeting.Day> days() {
       return new ArrayList<Meeting.Day>(table.keySet());
    }

    /**
     * Obtain a list of timetable events on a particular day.
     * This list is not a view of the internal representation of the timetable.
     * @param day The day of events to return
     * @return A list of timetable events.
     */
    public List<TimetableEvent> getDayEvents(Meeting.Day day) {
        return new ArrayList<TimetableEvent>(table.get(day));
    }

    /**
     * Get the particular event of a day at an index.
     * @param day The day of the event.
     * @param index The index of the event on a day.
     * @return A TimetableEvent at a specific day and index.
     */
    public TimetableEvent get(Meeting.Day day, int index) {
        return table.get(day).get(index);
    }

    /**
     * Obtains a left to right view of the table in a list. The list returned will have each
     * event sorted from left to right and from top to bottom for each column.
     * @return a sorted List of TimetableEvents
     */
    public List<TimetableEvent> toLeftRightList() {
        List<TimetableEvent> result = new ArrayList<TimetableEvent>();
        for (Meeting.Day day : table.keySet()) {
            for (TimetableEvent event : table.get(day)) {
                result.add(event);
            }
        }
        return result;
    }

    /**
     * Obtains a top to bottom view of the table in a list. The list returned will have each
     * event sorted from earliest to latest and from left to right for each row.
     * @return a sorted List of TimetableEvents
     */
    public List<TimetableEvent> toTopDownList() {
        List<TimetableEvent> result = new ArrayList<TimetableEvent>();
        boolean cont = true;
        for (int i = 0; cont; i++) {
            int counter = 0;
            for (Meeting.Day day : table.keySet()) {
                List<TimetableEvent> events = table.get(day);
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
     * and conflicts are than resolved by day of week.
     *
     * @return A Queue of TimetableEvents
     */
    public Queue<TimetableEvent> toQueue() {
        Queue<TimetableEvent> result = new LinkedList<TimetableEvent>();
        Queue<WeightedDay> queue = new PriorityQueue<WeightedDay>();

        for (Meeting.Day day : table.keySet()) {
            TimetableEvent meeting = table.get(day).get(0);
            queue.add(new WeightedDay(day, meeting.getEndTime() + meeting.getAfterPadding()));
            result.add(meeting);
        }

        while (!queue.isEmpty()) {
            WeightedDay d = queue.poll();
            List<TimetableEvent> dayEvents = table.get(d.day);
            if (d.index < dayEvents.size()) {
                TimetableEvent meeting = dayEvents.get(d.index);
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
     * Used for transforming the Timetable into a queue. Weighted based on the end time of a
     * TimetableEvent, else compared by day of week.
     */
    private static class WeightedDay implements Comparable<WeightedDay> {
        Meeting.Day day;
        int index;
        int weight;

        public WeightedDay(Meeting.Day day, int weight) {
            this.weight = weight;
            this.day = day;
            index = 1;
        }

        @Override
        public int compareTo(@NonNull WeightedDay other) {
            if (this.weight == other.weight) {
                return this.day.compareTo(other.day);
            } else {
                return Integer.valueOf(this.weight).compareTo(other.weight);
            }
        }
    }


}
