package com.hijamoya.rxlogin.subscriber;

import android.support.annotation.VisibleForTesting;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.Status;
import com.hijamoya.rxlogin.GoogleCallback;
import com.hijamoya.rxlogin.LoginException;
import com.hijamoya.rxlogin.RxLogin;

import rx.Observable;
import rx.Subscriber;

public class GoogleSubscriber implements Observable.OnSubscribe<GoogleSignInResult> {

    private final RxLogin mRxLogin;

    @VisibleForTesting GoogleCallback mCallback;

    public GoogleSubscriber(RxLogin rxLogin) {
        this.mRxLogin = rxLogin;
    }

    @Override public void call(final Subscriber<? super GoogleSignInResult> subscriber) {
        mCallback = new GoogleCallback() {
            @Override public void onSuccess(GoogleSignInResult result) {
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

            @Override public void onError(GoogleSignInResult result) {
                if (!subscriber.isUnsubscribed()) {
                    Status status = result.getStatus();
                    subscriber.onError(new LoginException(LoginException.GOOGLE_ERROR,
                        status.getStatusCode(), status.getStatusMessage()));
                }
            }
        };
        mRxLogin.registerCallback(mCallback);
    }

}
