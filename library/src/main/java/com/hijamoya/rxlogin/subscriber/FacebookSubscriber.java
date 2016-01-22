package com.hijamoya.rxlogin.subscriber;

import android.support.annotation.VisibleForTesting;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.hijamoya.rxlogin.LoginException;
import com.hijamoya.rxlogin.RxLogin;

import rx.Observable;
import rx.Subscriber;

public class FacebookSubscriber implements Observable.OnSubscribe<LoginResult> {

    private final RxLogin mRxLogin;

    @VisibleForTesting FacebookCallback<LoginResult> mCallback;

    public FacebookSubscriber(RxLogin rxLogin) {
        this.mRxLogin = rxLogin;
    }

    @Override public void call(final Subscriber<? super LoginResult> subscriber) {
        mCallback = new FacebookCallback<LoginResult>() {
            @Override public void onSuccess(LoginResult result) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(result);
                    subscriber.onCompleted();
                }
            }

            @Override public void onCancel() {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onError(new LoginException(LoginException.LOGIN_CANCELED));
                }
            }

            @Override public void onError(FacebookException error) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onError(new LoginException(LoginException.FACEBOOK_ERROR, error));
                }
            }
        };
        mRxLogin.registerCallback(mCallback);
    }

}
