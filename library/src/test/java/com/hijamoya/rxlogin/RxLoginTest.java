package com.hijamoya.rxlogin;

import android.app.Activity;
import android.content.Intent;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.squareup.assertj.android.BuildConfig;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.subscribers.TestSubscriber;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@Config(sdk = LOLLIPOP, constants = BuildConfig.class, manifest=Config.NONE)
@RunWith(ParameterizedRobolectricTestRunner.class)
public class RxLoginTest {

    @Mock Activity mActivity;
    @Mock LoginResult mLoginResult;
    @Mock GoogleSignInResult mGoogleSignInResult;
    @Mock Intent mTestIntent;
    @Mock GoogleApiClient mMockGoogleApiClient;
    @Mock TwitterAuthClient mMockTwitterAuthClient;

    TestSubscriber<? super LoginResult> mFacebookSubscriber;
    TestSubscriber<? super GoogleSignInResult> mGoogleSubscriber;
    TestSubscriber<? super Result<TwitterSession>> mTwitterSubscriber;
    RxLogin mRxLogin;

    private final boolean mPublish;

    @ParameterizedRobolectricTestRunner.Parameters(name = "publish = {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            {false},
            {true}
        });
    }

    public RxLoginTest(boolean publish) {
        this.mPublish = publish;
    }

    @Test public void testRegisterCallbackForFacebook() {
        mRxLogin.mFbManager = mock(com.facebook.login.LoginManager.class);
        FacebookCallback callback = mock(FacebookCallback.class);
        mRxLogin.registerCallback(callback);
        verify(mRxLogin.mFbManager).registerCallback(any(com.facebook.CallbackManager.class),
            eq(callback));
        assertThat(mRxLogin.mFacebookCallback).isEqualTo(callback);
        assertThat(mRxLogin.mCallbackManager).isNotNull();
    }

    @Test public void testRegisterCallbackForGoogle() {
        GoogleCallback callback = mock(GoogleCallback.class);
        mRxLogin.registerCallback(callback);
        assertThat(mRxLogin.mGoogleCallback).isEqualTo(callback);
    }

    @Test public void testOnActivityResultForFacebook() {
        mRxLogin.mCallbackManager = mock(com.facebook.CallbackManager.class);
        mRxLogin.onActivityResult(1234, Activity.RESULT_OK, mTestIntent);
        verify(mRxLogin.mCallbackManager).onActivityResult(eq(1234), eq(-1), eq(mTestIntent));
    }

    @Test public void testOnActivityResultForGoogle() {
        mRxLogin.mGoogleCallback = mock(GoogleCallback.class);
        mRxLogin.onActivityResult(5712, Activity.RESULT_CANCELED, mTestIntent);
        verify(mRxLogin.mGoogleCallback).onCancel();
        mRxLogin.onActivityResult(5712, Activity.RESULT_OK, mTestIntent);
        verify(mRxLogin.mGoogleCallback).onError(eq(null));
    }

    @Test public void testLoginFacebookSuccess() {
        com.facebook.login.LoginManager mockLoginManager =
            mock(com.facebook.login.LoginManager.class);
        InOrder inOrder = inOrder(mockLoginManager);
        mRxLogin.mFbManager = mockLoginManager;
        mRxLogin.loginFacebook(mActivity, mPublish,
            Arrays.asList(
                "public_profile",
                "email",
                "user_friends"))
            .subscribe(mFacebookSubscriber);
        mRxLogin.mFacebookCallback.onSuccess(mLoginResult);
        mFacebookSubscriber.awaitTerminalEvent();
        inOrder.verify(mockLoginManager).logOut();
        if (mPublish) {
            inOrder.verify(mockLoginManager).logInWithPublishPermissions(eq(mActivity),
                eq(Arrays.asList(
                    "public_profile",
                    "email",
                    "user_friends")));
        } else {
            inOrder.verify(mockLoginManager).logInWithReadPermissions(eq(mActivity),
                eq(Arrays.asList(
                    "public_profile",
                    "email",
                    "user_friends")));
        }
        mFacebookSubscriber.assertNoErrors();
        mFacebookSubscriber.assertValueCount(1);
        mFacebookSubscriber.assertTerminated();
        assertThat(mRxLogin.mFbManager).isNull();
        assertThat(mRxLogin.mFacebookCallback).isNull();
        assertThat(mRxLogin.mCallbackManager).isNull();
    }

    @Test public void testLoginFacebookCancel() {
        com.facebook.login.LoginManager mockLoginManager =
            mock(com.facebook.login.LoginManager.class);
        InOrder inOrder = inOrder(mockLoginManager);
        mRxLogin.mFbManager = mockLoginManager;
        mRxLogin.loginFacebook(mActivity, mPublish,
            Arrays.asList(
                "public_profile",
                "email",
                "user_friends"))
            .subscribe(mFacebookSubscriber);
        mRxLogin.mFacebookCallback.onCancel();
        mFacebookSubscriber.awaitTerminalEvent();
        inOrder.verify(mockLoginManager).logOut();
        if (mPublish) {
            inOrder.verify(mockLoginManager).logInWithPublishPermissions(eq(mActivity),
                eq(Arrays.asList(
                    "public_profile",
                    "email",
                    "user_friends")));
        } else {
            inOrder.verify(mockLoginManager).logInWithReadPermissions(eq(mActivity),
                eq(Arrays.asList(
                    "public_profile",
                    "email",
                    "user_friends")));
        }
        mFacebookSubscriber.assertError(LoginException.class);
        mFacebookSubscriber.assertTerminated();
        assertThat(mRxLogin.mFbManager).isNull();
        assertThat(mRxLogin.mFacebookCallback).isNull();
        assertThat(mRxLogin.mCallbackManager).isNull();
    }

    @Test public void testLoginFacebookError() {
        com.facebook.login.LoginManager mockLoginManager =
            mock(com.facebook.login.LoginManager.class);
        InOrder inOrder = inOrder(mockLoginManager);
        mRxLogin.mFbManager = mockLoginManager;
        mRxLogin.loginFacebook(mActivity, mPublish,
            Arrays.asList(
                "public_profile",
                "email",
                "user_friends"))
            .subscribe(mFacebookSubscriber);
        mRxLogin.mFacebookCallback.onError(new FacebookException());
        mFacebookSubscriber.awaitTerminalEvent();
        inOrder.verify(mockLoginManager).logOut();
        if (mPublish) {
            inOrder.verify(mockLoginManager).logInWithPublishPermissions(eq(mActivity),
                eq(Arrays.asList(
                    "public_profile",
                    "email",
                    "user_friends")));
        } else {
            inOrder.verify(mockLoginManager).logInWithReadPermissions(eq(mActivity),
                eq(Arrays.asList(
                    "public_profile",
                    "email",
                    "user_friends")));
        }
        mFacebookSubscriber.assertError(LoginException.class);
        mFacebookSubscriber.assertTerminated();
        assertThat(mRxLogin.mFbManager).isNull();
        assertThat(mRxLogin.mFacebookCallback).isNull();
        assertThat(mRxLogin.mCallbackManager).isNull();
    }

    @Test public void testLoginGoogleSuccess() throws Exception {
        InOrder inOrder = inOrder(mMockGoogleApiClient);
        mRxLogin.mGoogleApiClient = mMockGoogleApiClient;
        mRxLogin.loginGoogle(mActivity, new Scope(Scopes.PLUS_LOGIN))
            .subscribe(mGoogleSubscriber);
        // wait for connection
        Thread.sleep(20);
        mRxLogin.mGoogleCallback.onSuccess(mGoogleSignInResult);
        mGoogleSubscriber.awaitTerminalEvent();
        verify(mActivity).startActivityForResult(eq(mTestIntent), eq(5712));
        inOrder.verify(mMockGoogleApiClient).blockingConnect(eq(10L), eq(TimeUnit.SECONDS));
        inOrder.verify(mMockGoogleApiClient).disconnect();
        mGoogleSubscriber.assertNoErrors();
        mGoogleSubscriber.assertValueCount(1);
        mGoogleSubscriber.assertTerminated();
        assertThat(mRxLogin.mGoogleApiClient).isNull();
        assertThat(mRxLogin.mGoogleCallback).isNull();
    }

    @Test public void testLoginGoogleCancel() throws Exception {
        InOrder inOrder = inOrder(mMockGoogleApiClient);
        mRxLogin.mGoogleApiClient = mMockGoogleApiClient;
        mRxLogin.loginGoogle(mActivity, new Scope(Scopes.PLUS_LOGIN))
            .subscribe(mGoogleSubscriber);
        // wait for connection
        Thread.sleep(20);
        mRxLogin.mGoogleCallback.onCancel();
        mGoogleSubscriber.awaitTerminalEvent();
        verify(mActivity).startActivityForResult(eq(mTestIntent), eq(5712));
        inOrder.verify(mMockGoogleApiClient).blockingConnect(eq(10L), eq(TimeUnit.SECONDS));
        inOrder.verify(mMockGoogleApiClient).disconnect();
        mGoogleSubscriber.assertError(LoginException.class);
        mGoogleSubscriber.assertTerminated();
        assertThat(mRxLogin.mGoogleApiClient).isNull();
        assertThat(mRxLogin.mGoogleCallback).isNull();
    }

    @Test public void testLoginGoogleError() throws Exception {
        InOrder inOrder = inOrder(mMockGoogleApiClient);
        mRxLogin.mGoogleApiClient = mMockGoogleApiClient;
        mRxLogin.loginGoogle(mActivity, new Scope(Scopes.PLUS_LOGIN))
            .subscribe(mGoogleSubscriber);
        // wait for connection
        Thread.sleep(20);
        mRxLogin.mGoogleCallback.onError(mGoogleSignInResult);
        mGoogleSubscriber.awaitTerminalEvent();
        verify(mActivity).startActivityForResult(eq(mTestIntent), eq(5712));
        inOrder.verify(mMockGoogleApiClient).blockingConnect(eq(10L), eq(TimeUnit.SECONDS));
        inOrder.verify(mMockGoogleApiClient).disconnect();
        mGoogleSubscriber.assertError(LoginException.class);
        mGoogleSubscriber.assertTerminated();
        assertThat(mRxLogin.mGoogleApiClient).isNull();
        assertThat(mRxLogin.mGoogleCallback).isNull();
    }

    @Test public void testLoginGoogleConnectionError() throws Exception {
        when(mMockGoogleApiClient.blockingConnect(eq(10L), eq(TimeUnit.SECONDS)))
            .thenReturn(new ConnectionResult(ConnectionResult.API_UNAVAILABLE));
        InOrder inOrder = inOrder(mMockGoogleApiClient);
        mRxLogin.mGoogleApiClient = mMockGoogleApiClient;
        mRxLogin.loginGoogle(mActivity, new Scope(Scopes.PLUS_LOGIN))
            .subscribe(mGoogleSubscriber);
        mGoogleSubscriber.awaitTerminalEvent();
        verify(mActivity).startActivityForResult(eq(mTestIntent), eq(5712));
        inOrder.verify(mMockGoogleApiClient).blockingConnect(eq(10L), eq(TimeUnit.SECONDS));
        inOrder.verify(mMockGoogleApiClient).disconnect();
        mGoogleSubscriber.assertError(LoginException.class);
        mGoogleSubscriber.assertTerminated();
        assertThat(mRxLogin.mGoogleApiClient).isNull();
        assertThat(mRxLogin.mGoogleCallback).isNull();
    }

    @Test public void testLoginTwitterSuccess() {
        Result<TwitterSession> result = mock(Result.class);
        mRxLogin.mTwitterClient = mMockTwitterAuthClient;
        mRxLogin.loginTwitter(mActivity).subscribe(mTwitterSubscriber);
        mRxLogin.mTwitterCallback.success(result);
        mTwitterSubscriber.awaitTerminalEvent();
        mTwitterSubscriber.assertResult(result);
        mTwitterSubscriber.assertComplete();
    }

    @Test public void testLoginTwitterError() {
        mRxLogin.mTwitterClient = mMockTwitterAuthClient;
        mRxLogin.loginTwitter(mActivity).subscribe(mTwitterSubscriber);
        mRxLogin.mTwitterCallback.failure(mock(TwitterException.class));
        mTwitterSubscriber.awaitTerminalEvent();
        mTwitterSubscriber.assertError(LoginException.class);
    }

    @Before public void setUp() throws Exception {
        initMocks(this);
        when(mActivity.getApplicationContext()).thenReturn(RuntimeEnvironment.application);
        when(mLoginResult.getAccessToken()).thenReturn(new AccessToken("accessToken", "1111",
            "22222", Arrays.asList("public_profile", "email", "user_friends"),
            null, null, new Date(1033L), new Date(1044L)));
        GoogleSignInAccount mockAccount = mock(GoogleSignInAccount.class);
        when(mGoogleSignInResult.getSignInAccount()).thenReturn(mockAccount);
        when(mockAccount.getServerAuthCode()).thenReturn("auth_code");
        when(mGoogleSignInResult.getStatus()).thenReturn(new Status(0));
        when(mMockGoogleApiClient.blockingConnect(eq(10L), eq(TimeUnit.SECONDS)))
            .thenReturn(new ConnectionResult(ConnectionResult.SUCCESS));
        mFacebookSubscriber = new TestSubscriber<>();
        mGoogleSubscriber = new TestSubscriber<>();
        mTwitterSubscriber = new TestSubscriber<>();
        mRxLogin = spy(new RxLogin());
        when(mRxLogin.getGoogleSingInIntent()).thenReturn(mTestIntent);
    }

    @After public void tearDown() throws Exception {
        mGoogleSignInResult = null;
        mTestIntent = null;
        mMockGoogleApiClient = null;
        mFacebookSubscriber = null;
        mRxLogin = null;
    }

}
