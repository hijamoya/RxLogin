package com.hijamoya.rxlogin.subscriber;


import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.Status;
import com.hijamoya.rxlogin.BuildConfig;
import com.hijamoya.rxlogin.LoginException;
import com.hijamoya.rxlogin.RxLogin;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import io.reactivex.FlowableEmitter;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@Config(sdk = LOLLIPOP, constants = BuildConfig.class, manifest=Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class GoogleSubscriberTest {

    @Mock RxLogin mRxLogin;
    @Mock FlowableEmitter<GoogleSignInResult> mEmitter;

    GoogleSubscriber mGoogleSubscriber;

    @Test public void testCallOnSuccess() throws Exception {
        GoogleSignInResult result = mock(GoogleSignInResult.class);
        mGoogleSubscriber = new GoogleSubscriber(mRxLogin);
        mGoogleSubscriber.subscribe(mEmitter);
        verify(mRxLogin).registerCallback(eq(mGoogleSubscriber.mCallback));
        mGoogleSubscriber.mCallback.onSuccess(result);
        verify(mEmitter).onNext(eq(result));
        verify(mEmitter).onComplete();
    }

    @Test public void testCallOnError() throws Exception {
        GoogleSignInResult result = mock(GoogleSignInResult.class);
        when(result.getStatus()).thenReturn(new Status(1300, "gg"));
        mGoogleSubscriber = new GoogleSubscriber(mRxLogin);
        mGoogleSubscriber.subscribe(mEmitter);
        verify(mRxLogin).registerCallback(eq(mGoogleSubscriber.mCallback));
        mGoogleSubscriber.mCallback.onError(result);
        verify(mEmitter).onError(any(LoginException.class));
    }

    @Test public void testCallWithOnCancel() throws Exception {
        mGoogleSubscriber.subscribe(mEmitter);
        verify(mRxLogin).registerCallback(eq(mGoogleSubscriber.mCallback));
        mGoogleSubscriber.mCallback.onCancel();
        verify(mEmitter).onError(any(LoginException.class));
    }

    @Test public void testCancelWhenSuccess() throws Exception {
        mGoogleSubscriber.subscribe(mEmitter);
        when(mEmitter.isCancelled()).thenReturn(true);
        mGoogleSubscriber.mCallback.onSuccess(mock(GoogleSignInResult.class));
        verify(mEmitter, never()).onError(any(Throwable.class));
        verify(mEmitter, never()).onNext(any(GoogleSignInResult.class));
        verify(mEmitter, never()).onComplete();
    }

    @Test public void testCancelWhenError() throws Exception {
        mGoogleSubscriber.subscribe(mEmitter);
        when(mEmitter.isCancelled()).thenReturn(true);
        mGoogleSubscriber.mCallback.onError(mock(GoogleSignInResult.class));
        verify(mEmitter, never()).onError(any(Throwable.class));
        verify(mEmitter, never()).onNext(any(GoogleSignInResult.class));
        verify(mEmitter, never()).onComplete();
    }

    @Test public void testCancelWhenCancel() throws Exception {
        mGoogleSubscriber.subscribe(mEmitter);
        when(mEmitter.isCancelled()).thenReturn(true);
        mGoogleSubscriber.mCallback.onCancel();
        verify(mEmitter, never()).onError(any(Throwable.class));
        verify(mEmitter, never()).onNext(any(GoogleSignInResult.class));
        verify(mEmitter, never()).onComplete();
    }

    @Before public void setUp() throws Exception {
        initMocks(this);
        mGoogleSubscriber = new GoogleSubscriber(mRxLogin);
    }

    @After public void tearDown() throws Exception {
        mRxLogin = null;
        mGoogleSubscriber = null;
        mEmitter = null;
    }

}
