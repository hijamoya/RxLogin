package com.hijamoya.rxlogin.subscriber;

import android.support.annotation.VisibleForTesting;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.Status;
import com.hijamoya.rxlogin.GoogleCallback;
import com.hijamoya.rxlogin.LoginException;
import com.hijamoya.rxlogin.RxLogin;

import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

public class GoogleSubscriber implements FlowableOnSubscribe<GoogleSignInResult> {

    private final RxLogin mRxLogin;

    @VisibleForTesting GoogleCallback mCallback;

    public GoogleSubscriber(RxLogin rxLogin) {
        this.mRxLogin = rxLogin;
    }

    @Override public void subscribe(FlowableEmitter<GoogleSignInResult> emitter) throws Exception {
        mCallback = new GoogleCallback() {
            @Override public void onSuccess(GoogleSignInResult result) {
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

            @Override public void onError(GoogleSignInResult result) {
                if (!emitter.isCancelled()) {
                    Status status = result.getStatus();
                    emitter.onError(new LoginException(LoginException.GOOGLE_ERROR,
                        status.getStatusCode(), status.getStatusMessage()));
                }
            }
        };
        mRxLogin.registerCallback(mCallback);
    }

}
