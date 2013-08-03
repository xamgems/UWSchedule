/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *   UWSchedule student class and registration sharing interface
 *   Copyright (C) 2013 Sherman Pay, Jeremy Teo, Zachary Iqbal
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.amgems.uwschedule.metadata;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Contains details of each individual class.
 * Parses it into easily usable formats and stores them
 */
public class ClassInfo {
    public String course;
    public SortedMap<Day, Meeting> meetings;
    public enum Day {
        M (0, "Monday"), T (1, "Tuesday"), W (2, "Wednesday"), TH (3, "Thursday"), F (4, "Friday");

        public final int val;
        public final String name;
        Day (int val, String name) {
            this.val = val;
            this.name = name;
        }

        public int getVal() {
            return val;
        }
        public String toString() {
            return name;
        }

    }


    /**
     * Creates a new <code>ClassInfo</code> instance.
     *
     */
    public ClassInfo() {
        meetings = new TreeMap<Day, Meeting>();
    }


    /**
     * Creates a new <code>ClassInfo</code> instance.
     *
     * @param course a <code>String</code> value
     */
    public ClassInfo(String course) {
        meetings = new TreeMap<Day, Meeting>();
        this.course = course;
    }


    /**
     * Returns the meetings in a <code>SortedMap</code>
     *
     * @return <code>SortedMap</code> of meetings
     */
    public SortedMap<Day, Meeting> getMeetings() {
        return meetings;
    }

    /**
     * Adds the Day to the a the meetings <code>SortedMap</code>
     *
     * @param dayString a <code>String</code> value
     */
    public void addDay(String dayString) {
        Day day = null;
        if (dayString.equals("M")) {
            day = Day.M;
        } else if (dayString.equals("T")) {
            day = Day.T;
        } else if (dayString.equals("W")) {
            day = Day.W;
        } else if (dayString.equals("Th")) {
            day = Day.TH;
        } else if (dayString.equals("F")) {
            day = Day.F;
        }

        meetings.put(day, null);
    }

    /**
     * Adds all Days into the meetings <code>SortedMap</code>
     *
     * @param days a <code>List<String></code> value
     */
    public void addAllDays(List<String> days) {
        for (String day : days) {
            addDay(day);
        }
    }

    /**
     * Adds all Meetings into the meetings <code>SortedMap</code>
     * Must specify the times as a <code>List</code> and locations as a <code>List</code>
     *
     * @param times a <code>List<String></code>
     * @param locations a <code>List<String</code>
     */
    public void addMeeting(List<String> times, List<String> locations) {
        int timeI = 0;
        int locI = 0;
        for (Day day : meetings.keySet()) {
            String time = times.get(timeI);
            String location = locations.get(locI);
            Meeting meeting = new Meeting(time, location);
            if (timeI < times.size() - 1) {
                timeI++;
            }
            if (locI < locations.size() - 1) {
                locI++;
            }
            meetings.put(day, meeting);
        }
    }

    public void addAllMeetings(List<String> days, List<String> times, List<String> locations) {
        addAllDays(days);
        addMeeting(times, locations);
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String toString() {
        return course + ": \n" + "\t" + meetings;
    }

    public static class Meeting {
        public int startTime;
        public int endTime;
        public String location;

        public Meeting() {}
        public Meeting(String time, String location) {
            startTime(time);
            endTime(time);
            this.location = location;
        }

        public void startTime(String time) {
            int start = Integer.parseInt(time.substring(0, time.indexOf('-')));
            if (time.endsWith("P"))
                startTime = 1200 + start;
            else {
                if (start <= 500)
                    startTime = 1200 + start;
                else
                    startTime = start;
            }
        }

        public void endTime(String time) {
            int end = 0;
            if (time.endsWith("P")) {
                end = Integer.parseInt(time.substring(time.indexOf('-') + 1, time.indexOf('P')));
                endTime = 1200 + end;
            } else {
                end = Integer.parseInt(time.substring(time.indexOf('-') + 1));
                if (end <= 500)
                    endTime = 1200 + end;
                else
                    endTime = end;
            }
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public int getStartTime() {
            return startTime;
        }

        public int getEndTime() {
            return endTime;
        }

        public String getLocation() {
            return location;
        }

        public String toString() {
            return  startTime + " - " + endTime + " | location: " + location;
        }
    }
}
