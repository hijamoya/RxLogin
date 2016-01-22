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
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import rx.observers.TestSubscriber;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@Config(sdk = LOLLIPOP, constants = BuildConfig.class)
@RunWith(RobolectricGradleTestRunner.class)
public class GoogleSubscriberTest {

    @Mock RxLogin mRxLogin;

    TestSubscriber<? super GoogleSignInResult> mSubscriber;
    GoogleSubscriber mGoogleSubscriber;

    @Test public void testCallOnSuccess() {
        GoogleSignInResult result = mock(GoogleSignInResult.class);
        mGoogleSubscriber = new GoogleSubscriber(mRxLogin);
        mGoogleSubscriber.call(mSubscriber);
        verify(mRxLogin).registerCallback(eq(mGoogleSubscriber.mCallback));
        mGoogleSubscriber.mCallback.onSuccess(result);
        mSubscriber.assertTerminalEvent();
        mSubscriber.assertNoErrors();
        mSubscriber.assertValueCount(1);
        mSubscriber.assertCompleted();
        assertThat(mSubscriber.getOnNextEvents().get(0)).isEqualTo(result);
    }

    @Test public void testCallOnError() {
        GoogleSignInResult result = mock(GoogleSignInResult.class);
        when(result.getStatus()).thenReturn(new Status(1300, "gg"));
        mGoogleSubscriber = new GoogleSubscriber(mRxLogin);
        mGoogleSubscriber.call(mSubscriber);
        verify(mRxLogin).registerCallback(eq(mGoogleSubscriber.mCallback));
        mGoogleSubscriber.mCallback.onError(result);
        mSubscriber.assertError(LoginException.class);
        assertThat(((LoginException) mSubscriber.getOnErrorEvents().get(0)).getErrorCode())
            .isEqualTo(LoginException.GOOGLE_ERROR);
        assertThat(((LoginException) mSubscriber.getOnErrorEvents().get(0)).getStatusCode())
            .isEqualTo(1300);
        assertThat((mSubscriber.getOnErrorEvents().get(0)).getMessage()).isEqualTo("gg");
    }

    @Test public void testCallWithOnCancel() {
        mGoogleSubscriber.call(mSubscriber);
        verify(mRxLogin).registerCallback(eq(mGoogleSubscriber.mCallback));
        mGoogleSubscriber.mCallback.onCancel();
        mSubscriber.assertError(LoginException.class);
        assertThat(((LoginException) mSubscriber.getOnErrorEvents().get(0)).getErrorCode())
            .isEqualTo(LoginException.LOGIN_CANCELED);
    }

    @Test public void testUnsubscribeWhenSuccess() {
        mGoogleSubscriber.call(mSubscriber);
        mSubscriber.unsubscribe();
        mGoogleSubscriber.mCallback.onSuccess(mock(GoogleSignInResult.class));
        mSubscriber.assertNoValues();
        mSubscriber.assertNotCompleted();
        mSubscriber.assertNoTerminalEvent();
    }

    @Test public void testUnsubscribeWhenError() {
        mGoogleSubscriber.call(mSubscriber);
        mSubscriber.unsubscribe();
        mGoogleSubscriber.mCallback.onError(mock(GoogleSignInResult.class));
        mSubscriber.assertNoErrors();
        mSubscriber.assertNoTerminalEvent();
    }

    @Test public void testUnsubscribeWhenCancel() {
        mGoogleSubscriber.call(mSubscriber);
        mSubscriber.unsubscribe();
        mGoogleSubscriber.mCallback.onCancel();
        mSubscriber.assertNoErrors();
        mSubscriber.assertNoTerminalEvent();
    }

    @Before public void setUp() throws Exception {
        initMocks(this);
        mSubscriber = new TestSubscriber<>();
        mGoogleSubscriber = new GoogleSubscriber(mRxLogin);
    }

    @After public void tearDown() throws Exception {
        mRxLogin = null;
        mGoogleSubscriber = null;
        mSubscriber = null;
    }

}
