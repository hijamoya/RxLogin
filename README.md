# RxLogin
RxLogin is an Android library that simplifies the process of login different types of services into a single Flowable (Observable).

Current Supported Services:<br/>
1. Facebook<br/>
2. Google<br/>
3. Twitter<br/>

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

For twitter login, you should follow the pages to finish settings:

```java
https://dev.twitter.com/twitterkit/android/installation
https://dev.twitter.com/twitterkit/android/log-in-with-twitter
```

Put RxLogin to your Activity's ```onActivityResult```:

```java
 @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    mRxLogin.onActivityResult(requestCode, resultCode, data);
 }
```

And then just subscribe the Flowable (Observable):

```java
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
```
Adding Library
-----

You just add the following dependency to your build.gradle:
```groovy
Rxjava:
 dependencies {
    repositories {
         maven { url 'http://dl.bintray.com/hijamoya/maven' }
    }
    compile 'com.hijamoya.rxlogin:library:0.0.2@aar'
  }
```
```groovy
Rxjava2:
 dependencies {
    repositories {
         maven { url 'http://dl.bintray.com/hijamoya/maven' }
    }
    compile 'com.hijamoya.rxlogin:library:0.2.1@aar'
  }
```

License
-----
    Copyright 2016 Jam Hsu

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.