package com.amgems.uwschedule.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This interface represents a contract for a lightweight client for executing writable or readable HTTP requests using
 * the {@link java.net.URLConnection} framework.
 */
public interface HttpClient {

    /**
     * Establishes a connection to a given target URL, capable of reading
     * input from the connection's stream.
     *
     * @param targetUrl A non-null url to get a connection from
     * @throws IOException If the connection could not be established
     */
    public HttpURLConnection buildReadableConnection(URL targetUrl) throws IOException;

    /**
     * Establishes a connection to a given target URL, capable of writing output
     * and reading input from the connection's stream.
     *
     * @param targetUrl A non-null url to get a connection from
     * @throws IOException If the connection could not be established
     */
    public HttpURLConnection buildWriteReadableConnection(URL targetUrl) throws IOException;

}
