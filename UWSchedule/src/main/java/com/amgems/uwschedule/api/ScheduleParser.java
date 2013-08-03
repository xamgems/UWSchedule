package com.amgems.uwschedule.api;

import android.util.JsonReader;
import android.util.Log;

import java.util.*;
import java.net.*;
import java.io.*;


/**
 * Describe class <code>ScheduleParser</code> here.
 *
 * @author <a href="mailto:shermpay@cs.washington.edu">ShermPay</a>
 * @version 1.0
 */
public class ScheduleParser {
    public static final String BASE_URL = "http://students.washington.edu/shermpay/uw_schedule/web_services/schedule.php?name=";

//    public static List<ClassInfo> createSchedule(String name) throws IOException{
//        ScheduleParser parser = new ScheduleParser();
//        return parser.readJsonStream(parser.getJsonStream(name));
//    }

    /**
     * Returns the Json in a BufferedReader of a the Schedule specified by the
     * name passed in.
     *
     * @param name a <code>String</code> value
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     * @return a <code>BufferedReader</code> value
     */
    public InputStream getJsonStream(String name) {
        Log.d("POOP", "GENIUS");
        InputStream in = null;
        try {
            Log.d("POOP", "TRY");
            URI uri = new URI(BASE_URL + name);
            URL myURL = uri.toURL();
            URLConnection myConnection = myURL.openConnection();
            Log.d("POOP", "CONNECT");
            myConnection.connect();
            Log.d("POOP", "SUCCESS");
            in = myConnection.getInputStream();
            //            String s = "[{\"course\":\"ECON 200 B\",\"days\":[\"T\",\"Th\"],\"times\":[\"1030-1150\"],\"location\":[\"BAG 131\"]}," +
            //                    "{\"course\":\"ECON 200 BD\",\"days\":[\"F\"],\"times\":[\"1100-1220\"],\"location\":[\"THO 125\"]}," +
            //                    "{\"course\":\"MATH 126 D\",\"days\":[\"M\",\"W\",\"F\"],\"times\":[\"130-220\"],\"location\":[\"AND 223\"]}," +
            //                    "{\"course\":\"MATH 126 DB\",\"days\":[\"T\",\"Th\"],\"times\":[\"930-1020\"],\"location\":[\"CDH 105\"]},{" +
            //                    "\"course\":\"MATH 307 L\",\"days\":[\"M\",\"W\",\"F\"],\"times\":[\"230-320\"],\"location\":[\"\"]}," +
            //                    "{\"course\":\"MUSIC 116 A\",\"days\":[\"M\",\"W\"],\"times\":[\"1130-1220\"],\"location\":[\"MUS 126\"]}]";
            //            in = new ByteArrayInputStream(s.getBytes("UTF-8"));
        } catch (IOException e) {
            System.out.println("Exception caught: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            return in;
        }
    }


    /**
     * Reads the BufferedReader Containing the Json of the Schedule and
     * Returns a List that represents that Schedule
     * each item of the List is a specific ClassInfo
     *
     * @param in a <code>BufferedReader</code> value
     * @return a <code>List</code> value
     * @exception java.io.IOException if an error occurs
     */
    public List<ClassInfo> readJsonStream(InputStream in) throws IOException{
        if (in != null) {
            JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
            reader.setLenient(true);
            try {
                return readSchedule(reader);
            } finally {
                reader.close();
            }
        }
        return null;
    }

    /**
     * Reads the JsonReader containing an array of <code>ClassInfos</code>
     * Parses that Array and returns a <code>List</code>
     *
     * @param reader a <code>JsonReader</code> value
     * @return a <code>List</code> value
     * @exception java.io.IOException if an error occurs
     */
    public List<ClassInfo> readSchedule(JsonReader reader) throws IOException{
        List<ClassInfo> schedule = new ArrayList<ClassInfo>();

        reader.beginArray();
        while (reader.hasNext()) {
            schedule.add(readClassInfo(reader));
        }
        reader.endArray();

        return schedule;
    }

    /**
     * Reads an Individual <code>ClassInfo</code> parses and returns it
     * Each ClassInfo must contain 4 properties
     * 1. <code>String</code> course - represents the course name
     * 2. <code>enum</code> days - represents the days of meetings
     * 3. <code>String</code> times - represents the times of meetings
     * 4. <code>String</code> location - represents the location of meetings
     *
     * @param reader a <code>JsonReader</code> value
     * @return a <code>ClassInfo</code> value
     */
    public ClassInfo readClassInfo(JsonReader reader) throws IOException {
        reader.beginObject();
        String course = "";
        List<String> times = new ArrayList<String>();
        List<String> days = new ArrayList<String>();
        List<String> locations = new ArrayList<String>();

        while (reader.hasNext()) {
            String propertyName = reader.nextName();
            if (propertyName.equals("course")) {
                course = reader.nextString();
            } else if (propertyName.equals("times")) {
                arrayReader(reader, times);
            } else if (propertyName.equals("days")) {
                arrayReader(reader, days);
            } else if (propertyName.equals("location")) {
                arrayReader(reader, locations);
            }
        }

        ClassInfo info = new ClassInfo(course);
        info.addAllMeetings(days, times, locations);
        reader.endObject();
        System.out.println(info);
        return info;
    }

    public void arrayReader(JsonReader reader, List<String> property) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            property.add(reader.nextString());
        }
        reader.endArray();
    }


    /**
     * Contains details of each individual class.
     * Parses it into easily usable formats and stores them
     *
     *
     */
    public static class ClassInfo {
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
}