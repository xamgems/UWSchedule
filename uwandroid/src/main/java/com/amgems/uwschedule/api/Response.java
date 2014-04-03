package com.amgems.uwschedule.api;

import com.amgems.uwschedule.R;

/**
 * Created by JeremyTeoMBP on 9/2/14.
 */
public enum Response {
    OK(R.string.login_response_ok),
    AUTHENTICATION_ERROR(R.string.login_response_auth_error),
    SERVER_ERROR(R.string.login_response_server_error),
    TIMEOUT_ERROR(R.string.login_response_timeout_error),
    NETWORK_ERROR(R.string.login_response_network_error);

    /**
     * Resource ID for a suitable string corresponding
     * to the given response
     */
    private final int mStringResId;

    Response(int stringResId) {
        mStringResId = stringResId;
    }

    public int getStringResId() {
        return mStringResId;
    }
}
