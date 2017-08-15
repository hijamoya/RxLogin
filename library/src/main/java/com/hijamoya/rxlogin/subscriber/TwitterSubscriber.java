package com.hijamoya.rxlogin.subscriber;

import android.app.Activity;
import android.support.annotation.VisibleForTesting;

import com.hijamoya.rxlogin.LoginException;
import com.hijamoya.rxlogin.RxLogin;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

public class TwitterSubscriber implements FlowableOnSubscribe<Result<TwitterSession>> {

    private final RxLogin mRxLogin;

    @VisibleForTesting Activity mActivity;
    @VisibleForTesting TwitterAuthClient mClient;
    @VisibleForTesting Callback<TwitterSession> mCallback;

    public TwitterSubscriber(RxLogin rxLogin, Activity activity, TwitterAuthClient client) {
        this.mRxLogin = rxLogin;
        this.mActivity = activity;
        this.mClient = client;
    }

    @Override
    public void subscribe(FlowableEmitter<Result<TwitterSession>> emitter) throws Exception {
        mCallback = new Callback<TwitterSession>() {
            @Override public void success(Result<TwitterSession> result) {
                if (!emitter.isCancelled()) {
                    emitter.onNext(result);
                    emitter.onComplete();
                }
            }

            @Override public void failure(TwitterException e) {
                if (!emitter.isCancelled()) {
                    emitter.onError(new LoginException(LoginException.TWITTER_ERROR, e));
                }
            }
        };
        mClient.authorize(mActivity, mCallback);
        mRxLogin.registerCallback(mCallback);
    }

}
