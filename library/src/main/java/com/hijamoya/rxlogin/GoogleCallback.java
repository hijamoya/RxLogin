package com.hijamoya.rxlogin;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;

/**
 * * A callback class which hooks the {@link android.app.Activity#onActivityResult(int, int,
 * android.content.Intent)} of google login process.
 */
public interface GoogleCallback {

    /**
     * Called when google login process completed without error.
     *
     * @param result the login result
     */
    void onSuccess(GoogleSignInResult result);

    /**
     * Called when the google login process is canceled.
     */
    void onCancel();

    /**
     * Called when error occurs during google login process.
     *
     * @param result the login failed result
     */
    void onError(GoogleSignInResult result);

}
