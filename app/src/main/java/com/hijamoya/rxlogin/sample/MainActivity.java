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

import java.util.Arrays;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

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
                    .subscribe(new Action1<GoogleSignInResult>() {
                        @Override public void call(GoogleSignInResult result) {
                            // login success
                        }
                    }, new Action1<Throwable>() {
                        @Override public void call(Throwable throwable) {
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
                    .subscribe(new Action1<LoginResult>() {
                        @Override public void call(LoginResult result) {
                            // login success
                        }
                    }, new Action1<Throwable>() {
                        @Override public void call(Throwable throwable) {
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
