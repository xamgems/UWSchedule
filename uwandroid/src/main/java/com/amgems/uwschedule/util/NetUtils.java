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

package com.amgems.uwschedule.util;

import android.util.Log;
import com.amgems.uwschedule.services.LoginService;
import org.apache.http.NameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * A series of utility methods used to make and respond to connections
 * over the net.
 */
public class NetUtils {

    /** The base URL for all UW server requests */
    public static final String BASE_REQUEST_URL = "https://weblogin.washington.edu";

    /** The URL for the main UW registration page, detailing current courses */
    public static final String REGISTRATION_URL = "https://sdb.admin.washington.edu/students/uwnetid/register.asp";

    /** The user-agent to be used in a HTTP header */
    public static final String USER_AGENT_STRING = "Mozilla/5.0";

    /** The content-type to be used in a HTTP header */
    public static final String CONTENT_TYPE = "application/x-www-form-urlencoded";

    /** The character set to be requested */
    public static final String CHARSET = "UTF-8";

    // Suppress default constructor to ensure noninstantiability
    private NetUtils() { }

    /**
     * Builds a HTTP compliant query string from a series of NameValuePairs.
     */
    public static String toQueryString (List<? extends NameValuePair> postParameterPairs) {
        StringBuilder builder = new StringBuilder();
        boolean firstParameter = true;

        try {
            for (NameValuePair postParameterPair : postParameterPairs) {
                if (!firstParameter)
                    builder.append("&");
                firstParameter = false;

                builder.append(URLEncoder.encode(postParameterPair.getName(), NetUtils.CHARSET));
                builder.append("=");
                builder.append(URLEncoder.encode(postParameterPair.getValue(), NetUtils.CHARSET));
            }
        } catch (UnsupportedEncodingException e) {
            Log.e(LoginService.class.getSimpleName(), e.getMessage());
        }

        return builder.toString();
    }

}
