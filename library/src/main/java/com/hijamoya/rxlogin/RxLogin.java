package com.hijamoya.rxlogin;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.hijamoya.rxlogin.subscriber.FacebookSubscriber;
import com.hijamoya.rxlogin.subscriber.GoogleSubscriber;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.util.async.Async;

public class RxLogin {

    private static final int RC_SIGN_IN = 5712;
    private static final int GOOGLE_API_CONNECTION_TIMEOUT_SECONDS = 10;

    @VisibleForTesting com.facebook.login.LoginManager mFbManager;
    @VisibleForTesting GoogleApiClient mGoogleApiClient;
    @VisibleForTesting com.facebook.CallbackManager mCallbackManager;
    @VisibleForTesting FacebookCallback<LoginResult> mFacebookCallback;
    @VisibleForTesting GoogleCallback mGoogleCallback;

    /**
     * Login facebook to get the {@link LoginResult}.
     *
     * @param activity    the activity which is starting the login process
     * @param publish     need publish permission or not
     * @param permissions the requested permissions
     * @return the {@link Observable} of {@link LoginResult} of this login process
     */
    public Observable<LoginResult> loginFacebook(Activity activity, boolean publish,
        List<String> permissions) {
        if (!FacebookSdk.isInitialized()) {
            FacebookSdk.sdkInitialize(activity.getApplicationContext());
        }
        if (mFbManager == null) {
            mFbManager = com.facebook.login.LoginManager.getInstance();
        }
        return Observable.create(new FacebookSubscriber(this))
            .doOnSubscribe(() -> {
                mFbManager.logOut();
                if (publish) {
                    mFbManager.logInWithPublishPermissions(activity, permissions);
                } else {
                    mFbManager.logInWithReadPermissions(activity, permissions);
                }
            })
            .doOnUnsubscribe(() -> {
                mFbManager = null;
                mFacebookCallback = null;
                mCallbackManager = null;
            });
    }

    /**
     * Login google to get the {@link GoogleSignInResult}.
     *
     * @param activity the activity which is starting the login process
     * @param scope    the requested scope
     * @param scopes   the requested scopes
     * @return the {@link Observable} of {@link GoogleSignInResult} of this login process
     */
    public Observable<GoogleSignInResult> loginGoogle(Activity activity, @NonNull Scope scope,
        Scope... scopes) {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(activity.getApplicationContext())
                .addApi(Auth.GOOGLE_SIGN_IN_API,
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(scope, scopes)
                        .build())
                .build();
        }
        return Async.fromCallable(() -> mGoogleApiClient.blockingConnect(
            GOOGLE_API_CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS), Schedulers.io())
            .flatMap(connectionResult -> {
                if (!connectionResult.isSuccess()) {
                    return Observable.error(new LoginException(LoginException.GOOGLE_ERROR,
                        connectionResult.getErrorCode(), connectionResult.getErrorMessage()));
                }
                return Observable.create(new GoogleSubscriber(this));
            })
            .doOnSubscribe(() -> activity.startActivityForResult(getGoogleSingInIntent(),
                RC_SIGN_IN))
            .doOnUnsubscribe(() -> {
                mGoogleApiClient.disconnect();
                mGoogleApiClient = null;
                mGoogleCallback = null;
            });
    }

    /**
     * Register the callback for facebook login.
     *
     * @param callback the callback for getting the result from facebook
     */
    public void registerCallback(final FacebookCallback<LoginResult> callback) {
        mCallbackManager = CallbackManager.Factory.create();
        mFacebookCallback = callback;
        mFbManager.registerCallback(mCallbackManager, mFacebookCallback);
    }

    /**
     * Register the callback for google login.
     *
     * @param callback the callback for getting the result from google
     */
    public void registerCallback(GoogleCallback callback) {
        mGoogleCallback = callback;
    }

    /**
     * The method that should be called from the Activity's or Fragment's onActivityResult method.
     *
     * @param requestCode the request code that's received by the Activity or Fragment
     * @param resultCode  the result code that's received by the Activity or Fragment
     * @param data        the result data that's received by the Activity or Fragment
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mCallbackManager != null) {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
        if (mGoogleCallback != null) {
            if (requestCode == RC_SIGN_IN) {
                if (resultCode == Activity.RESULT_CANCELED) {
                    mGoogleCallback.onCancel();
                } else {
                    GoogleSignInResult r = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                    if (r != null && r.isSuccess()) {
                        mGoogleCallback.onSuccess(r);
                    } else {
                        mGoogleCallback.onError(r);
                    }
                }
            }
        }
    }

    @VisibleForTesting Intent getGoogleSingInIntent() {
        if (mGoogleApiClient == null) {
            return null;
        }
        return Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
    }

}
