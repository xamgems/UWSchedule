package com.amgems.uwschedule.api;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by shermpay on 6/29/13.
 */
public class ParserActivity extends Activity {

/*    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        AsyncTask<String, Integer, List<ScheduleParser.Course>> getScheduleTask = new GetScheduleTask(){
            @Override
            protected void onPostExecute(List<ScheduleParser.Course> classInfos) {

            }
        }.execute("shermpay");
    }

    private abstract class GetScheduleTask extends AsyncTask<String, Integer, List<ScheduleParser.Course>> {
        @Override
        protected List<ScheduleParser.Course> doInBackground(String... name) {
            List<ScheduleParser.Course> schedule = null;
            try {
                ScheduleParser parser = new ScheduleParser();
                InputStream inputStream = parser.getJsonStream(name[0]);
                schedule = parser.readJsonStream(inputStream);
                Log.d("POOP", inputStream.toString());
                Log.d("POOP", schedule.toString());
            } catch (Exception e){};

            return schedule;
        }
    }

*/
}
