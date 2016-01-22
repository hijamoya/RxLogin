package com.hijamoya.rxlogin;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Represents an error condition specific to the Login process.
 */
public class LoginException extends RuntimeException {

    /**
     * Initial status code.
     */
    public static final int UNKNOWN_STATUS = -1;

    @IntDef({UNKNOWN_ERROR, LOGIN_CANCELED, FACEBOOK_ERROR, GOOGLE_ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LoginErrorCode {}

    public static final int UNKNOWN_ERROR = 0;

    public static final int LOGIN_CANCELED = 1;

    public static final int FACEBOOK_ERROR = 2;

    public static final int GOOGLE_ERROR = 3;

    private static final long serialVersionUID = 7769013204120744732L;

    private int mErrorCode = UNKNOWN_ERROR;
    private int mStatusCode = UNKNOWN_STATUS;

    public LoginException(@LoginErrorCode int errorCode) {
        this.mErrorCode = errorCode;
    }

    public LoginException(@LoginErrorCode int errorCode, Throwable throwable) {
        super(throwable);
        this.mErrorCode = errorCode;
    }

    public LoginException(@LoginErrorCode int errorCode, int statusCode, String detailMessage) {
        super(detailMessage);
        this.mErrorCode = errorCode;
        this.mStatusCode = statusCode;
    }

    /**
     * Get the error code of this {@link LoginException}.
     *
     * @return error code
     */
    @LoginErrorCode public int getErrorCode() {
        return mErrorCode;
    }

    /**
     * Get status code returned by third party service.
     *
     * @return status code, -1 if there is no specific status
     */
    public int getStatusCode() {
        return mStatusCode;
    }

}
