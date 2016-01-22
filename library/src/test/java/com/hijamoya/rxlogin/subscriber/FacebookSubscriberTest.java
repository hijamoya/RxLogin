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
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import rx.observers.TestSubscriber;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@Config(sdk = LOLLIPOP, constants = BuildConfig.class)
@RunWith(RobolectricGradleTestRunner.class)
public class FacebookSubscriberTest {

    @Mock RxLogin mRxLogin;

    TestSubscriber<? super LoginResult> mSubscriber;
    FacebookSubscriber mFacebookSubscriber;

    @Test public void testCallOnSuccess() {
        LoginResult result = mock(LoginResult.class);
        mFacebookSubscriber.call(mSubscriber);
        verify(mRxLogin).registerCallback(eq(mFacebookSubscriber.mCallback));
        mFacebookSubscriber.mCallback.onSuccess(result);
        mSubscriber.assertTerminalEvent();
        mSubscriber.assertNoErrors();
        mSubscriber.assertValueCount(1);
        mSubscriber.assertCompleted();
        assertThat(mSubscriber.getOnNextEvents().get(0)).isEqualTo(result);
    }

    @Test public void testCallOnError() {
        mFacebookSubscriber.call(mSubscriber);
        verify(mRxLogin).registerCallback(eq(mFacebookSubscriber.mCallback));
        mFacebookSubscriber.mCallback.onError(new FacebookException());
        mSubscriber.assertError(LoginException.class);
        assertThat(((LoginException) mSubscriber.getOnErrorEvents().get(0)).getErrorCode())
            .isEqualTo(LoginException.FACEBOOK_ERROR);
    }

    @Test public void testCallWithOnCancel() {
        mFacebookSubscriber.call(mSubscriber);
        verify(mRxLogin).registerCallback(eq(mFacebookSubscriber.mCallback));
        mFacebookSubscriber.mCallback.onCancel();
        mSubscriber.assertError(LoginException.class);
        assertThat(((LoginException) mSubscriber.getOnErrorEvents().get(0)).getErrorCode())
            .isEqualTo(LoginException.LOGIN_CANCELED);
    }

    @Test public void testUnsubscribeWhenSuccess() {
        mFacebookSubscriber.call(mSubscriber);
        mSubscriber.unsubscribe();
        mFacebookSubscriber.mCallback.onSuccess(mock(LoginResult.class));
        mSubscriber.assertNoValues();
        mSubscriber.assertNotCompleted();
        mSubscriber.assertNoTerminalEvent();
    }

    @Test public void testUnsubscribeWhenError() {
        mFacebookSubscriber.call(mSubscriber);
        mSubscriber.unsubscribe();
        mFacebookSubscriber.mCallback.onError(new FacebookException());
        mSubscriber.assertNoErrors();
        mSubscriber.assertNoTerminalEvent();
    }

    @Test public void testUnsubscribeWhenCancel() {
        mFacebookSubscriber.call(mSubscriber);
        mSubscriber.unsubscribe();
        mFacebookSubscriber.mCallback.onCancel();
        mSubscriber.assertNoErrors();
        mSubscriber.assertNoTerminalEvent();
    }

    @Before public void setUp() throws Exception {
        initMocks(this);
        mSubscriber = new TestSubscriber<>();
        mFacebookSubscriber = new FacebookSubscriber(mRxLogin);
    }

    @After public void tearDown() throws Exception {
        mRxLogin = null;
        mFacebookSubscriber = null;
        mSubscriber = null;
    }

}
