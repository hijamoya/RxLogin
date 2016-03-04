# RxLogin
RxLogin is an Android library that simplifies the process of login different types of services into a single observable.

Current Supported Services:<br/>
1. Facebook<br/>
2. Google<br/>

Usage
-----

To use facebook login, you should configure the facebook sdk:

```java
<application ...>
    <meta-data android:name="com.facebook.sdk.ApplicationId" android:value="your-fb-app-id-here"/>
</application>
```

For google login, you should follow the page to finish settings:

```java
https://developers.google.com/identity/sign-in/android/start-integrating
```

In your Activity's ```onCreate()```, put RxLogin to your Acitivity's ```onActivityResult```:

```java
 @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    mRxLogin.onActivityResult(requestCode, resultCode, data);
 }
```

And then just subscribe the observables:

```java
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
```
Adding Library
-----

You just add the following dependency to your build.gradle:

```groovy
 dependencies {
    repositories {
         maven { url 'http://dl.bintray.com/hijamoya/maven' }
    }
    compile 'com.hijamoya.rxlogin:library:0.0.2@aar'
  }
```


-----