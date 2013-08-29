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

package com.amgems.uwschedule.api.local;

import android.util.JsonReader;
import com.amgems.uwschedule.model.Course;

import java.util.*;
import java.net.*;
import java.io.*;


/**
 * Describe class <code>ScheduleParser</code> here.
 *
 * @author <a href="mailto:shermpay@cs.washington.edu">Sherman Pay/a>
 * @version 1.0
 */
public class ScheduleParser {
    public static final String BASE_URL = "http://students.washington.edu/shermpay/uw_schedule/web_services/schedule.php?name=";

//    public static List<Course> createSchedule(String name) throws IOException{
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
        InputStream in = null;
        try {
            URI uri = new URI(BASE_URL + name);
            URL myURL = uri.toURL();
            URLConnection myConnection = myURL.openConnection();
            myConnection.connect();
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
     * each item of the List is a specific Course
     *
     * @param in a <code>BufferedReader</code> value
     * @return a <code>List</code> value
     * @exception java.io.IOException if an error occurs
     */
    public List<Course> readJsonStream(InputStream in) throws IOException{
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
    public List<Course> readSchedule(JsonReader reader) throws IOException{
        List<Course> schedule = new ArrayList<Course>();

        reader.beginArray();
        while (reader.hasNext()) {
            schedule.add(readClassInfo(reader));
        }
        reader.endArray();

        return schedule;
    }

    /**
     * Reads an Individual <code>Course</code> parses and returns it
     * Each Course must contain 4 properties
     * 1. <code>String</code> course - represents the course name
     * 2. <code>enum</code> days - represents the days of meetings
     * 3. <code>String</code> times - represents the times of meetings
     * 4. <code>String</code> location - represents the location of meetings
     *
     * @param reader a <code>JsonReader</code> value
     * @return a <code>Course</code> value
     */
    public Course readClassInfo(JsonReader reader) throws IOException {
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

        Course info = null;  //Course.newInstance(sln, code, courseNumber, sectionId, credits, title, type, meetings);
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
}
