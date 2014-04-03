/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *   UWSchedule student class and registration sharing interface
 *   Copyright (C) 2013 Sherman Pay, Jeremy Teo, Zachary Iqbal
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by`
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

package com.amgems.uwschedule.api.uw;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.amgems.uwschedule.api.Response;
import com.amgems.uwschedule.util.NetUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Executes a login request to the UW servers, yielding a cookie on a successful
 * login.
 *
 * Preforms the appropriate post request to the UW login service with a given
 * username and password. All response cookies for any given request are then
 * stored.
 *
 * Note that LoginAuthenticator instances are not thread-safe, and thus should only
 * be manipulated on a single thread or when happens-before relationships on any
 * given instance can be guaranteed.
 *
 * @author Zachary Iqbal
 */
public final class LoginAuthenticator {

    /** Regex pattern to find name,value groups of hidden form fields */
    private static final Pattern HIDDEN_PARAMS = Pattern.compile("<input type=\"?hidden\"? name=\"(.+)\".*value=\"(.*)\">");

    /** A single cookie string captured by this login request */
    private String mCookiesValue;

    /** Defines the error or success response from executing a request*/
    private Response mResponse;

    /** The username string for the login user */
    private final String mUsername;
    /** The password string for the login user */
    private final String mPassword;

    /** A WebView used as a Javascript engine to load redirects required for
     *  cookie loading */
    private final WebView mJsWebview;
    /** A handler running on the UI thread to house the WebView */
    private final Handler mHandler;

    /** The HTML content received from the WebView */
    private String mHtml;
    /** Counts the number of times a loaded page callback is recieved from the
     *  WebView */
    private int mPageLoadCount;



    private final Lock lock = new ReentrantLock();
    /** Condition variable to wait on loading HTML content into mHtml */
    private final Condition htmlCallbackCondition = lock.newCondition();
    private volatile boolean mLoadingFinished;

    private LoginAuthenticator(Context context, Handler handler, String username, String password) {
        mUsername = username;
        mPassword = password;
        mCookiesValue = "";

        mLoadingFinished = false;
        mJsWebview = new WebView(context);
        mHandler = handler;
        CookieManager.getInstance().removeAllCookie();
        mJsWebview.getSettings().setJavaScriptEnabled(true);
        mJsWebview.addJavascriptInterface(new CaptureHtmBody(), CaptureHtmBody.INTERFACE_NAME);
        mJsWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // If the page has been loaded before, then the WebView must have
                // returned from a JavaScript redirect required for
                // appropriate login cookies. This is when the html content
                // should be captured and processed
                if (mPageLoadCount >= 1)
                    view.loadUrl(CaptureHtmBody.CAPTURE_HTML_SCRIPT);
                mPageLoadCount++;
            }
        });
    }

    /**
     * Returns a new, executable instance of LoginAuthenticator
     *
     * @param username Username string to log in with
     * @param password Password string to log in with
     */
    public static LoginAuthenticator newInstance(Context context, Handler handler, String username, String password) {
        return new LoginAuthenticator(context, handler, username, password);
    }

    /**
     * Executes a login authentication request.
     *
     * Stores the response from the execution and the cookie string if authentication
     * was valid. The execute method should only be called once. Behavior is unspecified
     * for multiple calls.
     *
     * Note that this method is blocking and should <b>not</b> be called on the UI thread.
     */
    public void execute() {

        // List of cookies received from server
        List<String> cookieList = null;

        try {
            lock.lock();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mJsWebview.loadUrl(NetUtils.REGISTRATION_URL);
                }
            });

            while (!mLoadingFinished) {
                htmlCallbackCondition.await();
            }
        } catch (InterruptedException e) {
            Log.d(getClass().getSimpleName(), "Html callback thread interrupted before it could be signaled");
        } finally {
            lock.unlock();
        }

        try {
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            InputStream htmlInputStream = new ByteArrayInputStream(mHtml.getBytes());
            BufferedReader reader = new BufferedReader(new InputStreamReader(htmlInputStream));

            // Captures and injects required post parameters for login
            postParameters.add(new BasicNameValuePair("user", mUsername));
            postParameters.add(new BasicNameValuePair("pass", mPassword));
            captureHiddenParameters(reader, postParameters);

            HttpURLConnection connection = NetUtils.getOutputConnection(new URL(NetUtils.LOGIN_REQUEST_URL));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));

            // Reads stream from response and stores any received cookies
            try {
                cookieList = getAuthCookies(connection, bufferedWriter, postParameters);
                // Valid cookie was returned - authentication was a success
                if (cookieList != null) {
                    mCookiesValue = cookieList.get(0);
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String postLoginHtml = "";
                    String line;
                    while ((line = reader.readLine()) != null) {
                        postLoginHtml += line;
                    }
                    postParameters.clear();
                    captureHiddenParameters(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(postLoginHtml.getBytes()))), postParameters);
                    mCookiesValue = postLoginHtml.substring(postLoginHtml.indexOf("pubcookie_g"), postLoginHtml.indexOf("==\">") + 2);
                    mCookiesValue = "pubcookie_g=" + mCookiesValue.substring("pubcookie_g\" value=\"".length()) + ";";
                }
            } finally {
                connection.disconnect();
            }

        } catch (MalformedURLException e) {
            mResponse = Response.SERVER_ERROR;
        } catch (IOException e) {
            mResponse = Response.NETWORK_ERROR;
        }

        if (mResponse == null) {
            if (cookieList != null) {
                mResponse = Response.OK;
            } else { // No cookies returned, username/password incorrect
                mResponse = Response.AUTHENTICATION_ERROR;
            }
        }
    }

    /**
     * @return The cookie string value of this instance execution or an empty
     *         String if the instance was not executed or failed in execution.
     */
    public String getCookie() {
        return mCookiesValue;
    }

    /**
     * @return The server {@link Response} of this instance execution or null if this
     *         instance has yet to be executed.
     */
    public Response getResponse() {
        return mResponse;
    }

    /**
     * Captures all form hidden key value pairs from an input source.
     */
    private void captureHiddenParameters (BufferedReader reader,
                                                 List<? super NameValuePair> destHiddenParams) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            Matcher parameterMatcher = HIDDEN_PARAMS.matcher(line);
            while (parameterMatcher.find()) {
                destHiddenParams.add(new BasicNameValuePair(parameterMatcher.group(1), parameterMatcher.group(2)));
            }
        }
    }

    /**
     * Collects all cookies from a given response source into a list of NameValuePairs.
     */
    private List<String> getAuthCookies (HttpURLConnection connection, BufferedWriter writer,
                                                List<? extends NameValuePair> postParams) throws IOException {
        writer.write(NetUtils.toQueryString(postParams));
        writer.flush();
        return connection.getHeaderFields().get("Set-Cookie");
    }

    /** Inner class used as a callback from a WebView */
    private final class CaptureHtmBody {

        /** A tag for this JavaScript interface */
        private static final String INTERFACE_NAME = "GetHtmlBody";
        /** Script used to inject HTML content into this JavaScript interface */
        private static final String CAPTURE_HTML_SCRIPT = "javascript:window." + INTERFACE_NAME +
                                                          ".processHTML(document.getElementsByTagName('html')[0].innerHTML);";

        @JavascriptInterface
        /**
         * Called from a JavaScript interface with the html body of the WebView
         */
        public void processHTML(String html) {
            lock.lock();

            mHtml = html;
            mLoadingFinished = true;

            htmlCallbackCondition.signal();
            lock.unlock();
        }
    }

}
