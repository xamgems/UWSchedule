package com.amgems.uwschedule.api.uw;

import com.amgems.uwschedule.api.Response;
import com.amgems.uwschedule.util.HttpClient;
import com.amgems.uwschedule.util.NetUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Collects SLNs for all courses a student is registered for from the UW registration
 * website.
 *
 * @author Jeremy Teo
 */
public class GetStudentSlns {

    private static final Pattern pat_sln = Pattern.compile("<TT>\\d{5}");
    /** Cookie value to request registration page with */
    private String mCookie;
    private List<String> mSlns;
    /** Defines the error or success response from executing a request*/
    private Response mResponse;
    // TODO: Utilize the html string as a local variable instead and remove the getHtml() method
    //       for debugging purposes only
    private String mHtml;

    private final HttpClient mHttpClient;

    /**
     * Constructs a GetStudentSlns with the given well formed cookie string.
     * @param cookie
     */
    private GetStudentSlns(String cookie, HttpClient httpClient) {
        mHttpClient = httpClient;
        mCookie = cookie;
        mHtml = "";
    }

    /**
     * @param cookie The cookie string to request the registration data with.
     *               This cookie string must be non-null, well formed and contains
     *               the required pubcookie_g cookie.
     * @return A new, executable instance of GetStudentSlns.
     */
    public static GetStudentSlns newInstance(String cookie, HttpClient httpClient) {
        return new GetStudentSlns(cookie, httpClient);
    }

    // Returns a list of SLNs as Strings, given a well formed html content String.
    private static List<String> parseSlnList(String html) {
        Scanner scanner = new Scanner(html);
        List<String> slns = new ArrayList<String>();
        String sln;
        while ((sln = scanner.findInLine(pat_sln)) != null) {
            slns.add(sln.substring(4, sln.length()));
        }
        return slns;
    }

    /**
     * Executes a request for a list of the student's SLNs.
     * <p/>
     * Stores the response from the execution and the list of SLNs if the request was successful.
     * The execute method should only be called once. Behavior is unspecified for multiple calls.
     * <p/>
     * Note that this method is blocking and should <b>not</b> be called on the UI thread.
     */
    public void execute() {

        try {
            HttpURLConnection connection = mHttpClient.buildReadableConnection(new URL(NetUtils.REGISTRATION_URL));
            // Puts the given cookie in the request header
            connection.setRequestProperty("Cookie", mCookie);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            try {
                // Reads lines to accumulate the registration page HTML content
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                mHtml = sb.toString();
            } finally {
                connection.disconnect();
                reader.close();
            }

            // TODO: Parse the html for slns
            mSlns = parseSlnList(mHtml);

        } catch (MalformedURLException e) {
            mResponse = Response.SERVER_ERROR;
        } catch (IOException e) {
            mResponse = Response.NETWORK_ERROR;
        }

    }

    /**
     * @return A list of Strings corresponding to the SLNs for which the requested
     *         student is registered for or {@code null} if this instance has yet
     *         to be executed.
     */
    public List<String> getSlns() {
        return mSlns;
    }

    /**
     * @return The server {@link Response} of this instance execution or {@code null} if this
     *         instance has yet to be executed.
     */
    public Response getResponse() {
        return mResponse;
    }

    // TODO: Remove this debugging method when no longer necessary
    public String getHtml() { return mHtml; }
}
