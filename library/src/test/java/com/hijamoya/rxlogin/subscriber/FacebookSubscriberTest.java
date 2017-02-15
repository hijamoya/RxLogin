package com.hijamoya.rxlogin.subscriber;


import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
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
public class FacebookSubscriberTest {

    @Mock RxLogin mRxLogin;
    @Mock FlowableEmitter<LoginResult> mEmitter;

    FacebookSubscriber mFacebookSubscriber;

    @Test public void testCallOnSuccess() throws Exception {
        LoginResult result = mock(LoginResult.class);
        mFacebookSubscriber.subscribe(mEmitter);
        verify(mRxLogin).registerCallback(eq(mFacebookSubscriber.mCallback));
        mFacebookSubscriber.mCallback.onSuccess(result);
        verify(mEmitter).onNext(eq(result));
        verify(mEmitter).onComplete();
    }

    @Test public void testCallOnError() throws Exception {
        mFacebookSubscriber.subscribe(mEmitter);
        verify(mRxLogin).registerCallback(eq(mFacebookSubscriber.mCallback));
        mFacebookSubscriber.mCallback.onError(new FacebookException());
        verify(mEmitter).onError(any(LoginException.class));
    }

    @Test public void testCallWithOnCancel() throws Exception {
        mFacebookSubscriber.subscribe(mEmitter);
        verify(mRxLogin).registerCallback(eq(mFacebookSubscriber.mCallback));
        mFacebookSubscriber.mCallback.onCancel();
        verify(mEmitter).onError(any(LoginException.class));
    }

    @Test public void testCancelWhenSuccess() throws Exception {
        mFacebookSubscriber.subscribe(mEmitter);
        when(mEmitter.isCancelled()).thenReturn(true);
        mFacebookSubscriber.mCallback.onSuccess(mock(LoginResult.class));
        verify(mEmitter, never()).onError(any(Throwable.class));
        verify(mEmitter, never()).onNext(any(LoginResult.class));
        verify(mEmitter, never()).onComplete();
    }

    @Test public void testCancelWhenError() throws Exception {
        mFacebookSubscriber.subscribe(mEmitter);
        when(mEmitter.isCancelled()).thenReturn(true);
        mFacebookSubscriber.mCallback.onError(new FacebookException());
        verify(mEmitter, never()).onError(any(Throwable.class));
        verify(mEmitter, never()).onNext(any(LoginResult.class));
        verify(mEmitter, never()).onComplete();
    }

    @Test public void testCancelWhenCancel() throws Exception {
        mFacebookSubscriber.subscribe(mEmitter);
        when(mEmitter.isCancelled()).thenReturn(true);
        mFacebookSubscriber.mCallback.onCancel();
        mFacebookSubscriber.mCallback.onError(new FacebookException());
        verify(mEmitter, never()).onError(any(Throwable.class));
        verify(mEmitter, never()).onNext(any(LoginResult.class));
        verify(mEmitter, never()).onComplete();
    }

    @Before public void setUp() throws Exception {
        initMocks(this);
        mFacebookSubscriber = new FacebookSubscriber(mRxLogin);
    }

    @After public void tearDown() throws Exception {
        mRxLogin = null;
        mFacebookSubscriber = null;
        mEmitter = null;
    }

}
