package com.hijamoya.rxlogin.subscriber;

import android.support.annotation.VisibleForTesting;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.hijamoya.rxlogin.LoginException;
import com.hijamoya.rxlogin.RxLogin;

import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

public class FacebookSubscriber implements FlowableOnSubscribe<LoginResult> {

    private final RxLogin mRxLogin;

    @VisibleForTesting FacebookCallback<LoginResult> mCallback;

    public FacebookSubscriber(RxLogin rxLogin) {
        this.mRxLogin = rxLogin;
    }

    @Override public void subscribe(FlowableEmitter<LoginResult> emitter) throws Exception {
        mCallback = new FacebookCallback<LoginResult>() {
            @Override public void onSuccess(LoginResult result) {
                if (!emitter.isCancelled()) {
                    emitter.onNext(result);
                    emitter.onComplete();
                }
            }

            @Override public void onCancel() {
                if (!emitter.isCancelled()) {
                    emitter.onError(new LoginException(LoginException.LOGIN_CANCELED));
                }
            }

            @Override public void onError(FacebookException error) {
                if (!emitter.isCancelled()) {
                    emitter.onError(new LoginException(LoginException.FACEBOOK_ERROR, error));
                }
            }
        };
        mRxLogin.registerCallback(mCallback);
    }

}
