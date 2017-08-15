package com.hijamoya.rxlogin.subscriber;


import android.app.Activity;

import com.hijamoya.rxlogin.BuildConfig;
import com.hijamoya.rxlogin.LoginException;
import com.hijamoya.rxlogin.RxLogin;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

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

@Config(sdk = LOLLIPOP, constants = BuildConfig.class, manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class TwitterSubscriberTest {

    @Mock RxLogin mRxLogin;
    @Mock FlowableEmitter<Result<TwitterSession>> mEmitter;

    TwitterSubscriber mTwitterSubscriber;

    @Test public void testCallOnSuccess() throws Exception {
        Result<TwitterSession> result = mock(Result.class);
        mTwitterSubscriber.subscribe(mEmitter);
        verify(mRxLogin).registerCallback(eq(mTwitterSubscriber.mCallback));
        mTwitterSubscriber.mCallback.success(result);
        verify(mEmitter).onNext(eq(result));
        verify(mEmitter).onComplete();
    }

    @Test public void testCallOnError() throws Exception {
        mTwitterSubscriber.subscribe(mEmitter);
        verify(mRxLogin).registerCallback(eq(mTwitterSubscriber.mCallback));
        mTwitterSubscriber.mCallback.failure(mock(TwitterException.class));
        verify(mEmitter).onError(any(LoginException.class));
    }

    @Test public void testCancelWhenSuccess() throws Exception {
        mTwitterSubscriber.subscribe(mEmitter);
        when(mEmitter.isCancelled()).thenReturn(true);
        mTwitterSubscriber.mCallback.success(mock(Result.class));
        verify(mEmitter, never()).onError(any(Throwable.class));
        verify(mEmitter, never()).onNext(any(Result.class));
        verify(mEmitter, never()).onComplete();
    }

    @Test public void testCancelWhenError() throws Exception {
        mTwitterSubscriber.subscribe(mEmitter);
        when(mEmitter.isCancelled()).thenReturn(true);
        mTwitterSubscriber.mCallback.failure(mock(TwitterException.class));
        verify(mEmitter, never()).onError(any(Throwable.class));
        verify(mEmitter, never()).onNext(any(Result.class));
        verify(mEmitter, never()).onComplete();
    }

    @Before public void setUp() throws Exception {
        initMocks(this);
        mTwitterSubscriber = new TwitterSubscriber(mRxLogin, mock(Activity.class), mock(
            TwitterAuthClient.class));
    }

    @After public void tearDown() throws Exception {
        mRxLogin = null;
        mTwitterSubscriber = null;
        mEmitter = null;
    }

}
