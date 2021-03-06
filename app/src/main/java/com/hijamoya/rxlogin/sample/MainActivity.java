package com.hijamoya.rxlogin.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Scope;
import com.hijamoya.rxlogin.RxLogin;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.Arrays;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    private RxLogin mRxLogin = new RxLogin();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_login_google).setOnClickListener(new View
            .OnClickListener() {
            @Override public void onClick(View v) {
                mRxLogin.loginGoogle(MainActivity.this, new Scope(Scopes.PLUS_LOGIN))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<GoogleSignInResult>() {
                        @Override
                        public void accept(GoogleSignInResult googleSignInResult) throws Exception {
                            // login success
                        }
                    }, new Consumer<Throwable>() {
                        @Override public void accept(Throwable throwable) throws Exception {
                            // login fail
                        }
                    });
            }
        });
        findViewById(R.id.btn_login_facebook).setOnClickListener(new View
            .OnClickListener() {
            @Override public void onClick(View v) {
                mRxLogin.loginFacebook(MainActivity.this, false, Arrays.asList(
                    "public_profile",
                    "email",
                    "user_friends"))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<LoginResult>() {
                        @Override public void accept(LoginResult loginResult) throws Exception {
                            // login success
                        }
                    }, new Consumer<Throwable>() {
                        @Override public void accept(Throwable throwable) throws Exception {
                            // login fail
                        }
                    });
            }
        });
        findViewById(R.id.btn_login_twitter).setOnClickListener(new View
            .OnClickListener() {
            @Override public void onClick(View v) {
                mRxLogin.loginTwitter(MainActivity.this)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Result<TwitterSession>>() {
                        @Override
                        public void accept(Result<TwitterSession> result) throws Exception {
                            // login success
                        }
                    }, new Consumer<Throwable>() {
                        @Override public void accept(Throwable throwable) throws Exception {
                            // login fail
                        }
                    });
            }
        });
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mRxLogin.onActivityResult(requestCode, resultCode, data);
    }

}
