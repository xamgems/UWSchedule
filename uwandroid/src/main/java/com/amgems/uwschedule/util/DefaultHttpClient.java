package com.amgems.uwschedule.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A default implementation of {@link HttpClient} providing basic support for HTTP interaction.
 */
public class DefaultHttpClient implements HttpClient {

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpURLConnection buildReadableConnection(URL targetUrl) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
        connection.setRequestProperty("User-Agent", NetUtils.USER_AGENT_STRING);
        return connection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpURLConnection buildWriteReadableConnection(URL targetUrl) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
        connection.setRequestProperty("User-Agent", NetUtils.USER_AGENT_STRING);
        connection.setRequestProperty("Content-Type", NetUtils.CONTENT_TYPE);
        connection.setDoOutput(true);
        return connection;
    }

}
