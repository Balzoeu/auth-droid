# Basic Setup

### Gradle

in your project's root `build.gradle` append these repositories to your list:

```groovy
allprojects {
    repositories {
        mavenCentral()
        jcenter()
        maven { url "https://dl.bintray.com/giacomoparisi/auth-droid" 
    }
}
```
<br />
<br />
<br />

# Facebook

## Setup

Add the dependencies into the project's `build.gradle`:

```groovy

dependencies {

    implementation "com.giacomoparisi.authdroid:core:$authdroid_version"
    implementation "com.giacomoparisi.authdroid:facebook-core:$authdroid_version"
    implementation "com.giacomoparisi.authdroid:rx-facebook:$authdroid_version"
    
}
```

1) Create a facebook application and configure it for android
2) Add the metadata with your facebook appId in the manifest
```xml
<uses-permission android:name="android.permission.INTERNET"/>

<application android:label="@string/app_name" ...>
    ...
    <meta-data 
        android:name="com.facebook.sdk.ApplicationId" 
        android:value="@string/facebook_app_id"/>
    ...
</application>
```

For more info on step 1 or 2 see the [Facebook Android Login Docs](https://developers.facebook.com/docs/facebook-login/android/)

## Usage

### Auth

Subscribe to ***authWithFacebook*** single to start the login flow

```kotlin

authWithFacebook(activity) // pass you current activity
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ socialUser -> Log.d("User", socialUser.toString())}) { /* error */ }
                    
```
###### Result :

The [Social User](#social-user) entity


###### Errors :

***FacebookException*** see [Facebook Exception Docs](https://developers.facebook.com/docs/reference/androidsdk/5.9.0/facebook/com/facebook/facebookexception.html/)

***FacebookAuthError*** for any unkown error during the facebook auth flow

***Cancelled*** error if the task is cancelled

### Sign Out

Call ***facebookSignOut*** if you need to logout the user from facebook

```kotlin

facebookSignOut()                 
```

<br />
<br />
<br />

# Firebase

## Setup

### Basic Setup

Add the dependencies into the project's `build.gradle`:

```groovy

dependencies {

    implementation "com.giacomoparisi.authdroid:core:$authdroid_version"
    implementation "com.giacomoparisi.authdroid:firebase:$authdroid_version"
    
    // if you need firebase facebook login
    implementation "com.giacomoparisi.authdroid:facebook-core:$authdroid_version"
    implementation "com.giacomoparisi.authdroid:rx-firebase-facebook:$authdroid_version"
    
    // if you need firebase google login
    implementation "com.giacomoparisi.authdroid:rx-firebase-google:$authdroid_version"
}
```

1) Create a firebase application and configure it for android
2) In the "Authentication" section, enable the login methods you need

For more info on step 1 see the [Firebase Setup Doc](https://firebase.google.com/docs/android/setup)

<br />

### Firebase Facebook

1) Create a facebook application and configure it for android
2) Add the metadata with your facebook appId in the manifest
```xml
<uses-permission android:name="android.permission.INTERNET"/>

<application android:label="@string/app_name" ...>
    ...
    <meta-data 
        android:name="com.facebook.sdk.ApplicationId" 
        android:value="@string/facebook_app_id"/>
    ...
</application>
```
3) Enable Facebook Sign-In in the Firebase console, in the ***Authentication*** section

For more info on step 1 or 2 see the [Facebook Android Login Docs](https://developers.facebook.com/docs/facebook-login/android/)

<br />

### Firebase Google

1) Enable Google Sign-In in the Firebase console, in the ***Authentication*** section
2) Go to [Google Api Console Credential Page](https://console.developers.google.com/apis/credentials), select your project (auto created by firebase app) and get your Web Client OAuth 2.0 ID

For more info see [Google Android Sign In Doc](https://developers.google.com/identity/sign-in/android/start-integrating)

# AUTH

## Email / Password

<br />

### Sign Up

Subscribe to ***signUpWithFirebaseEmailPassword*** single to start the signUp flow

```kotlin

signUpWithFirebaseEmailPassword("email@email.com", "password")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ socialUser -> Log.d("User", socialUser.toString())}) { /* error */ }
                    
```
###### Result :

The [Social User](#social-user) entity


###### Errors :

***Cancelled*** error if the task is cancelled

***UnknownFirebaseError*** for any unkown error during the firebase auth flow

***FirebaseException*** see [Firebase Exception Docs](https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuthException)

<br />

### Sign In

Subscribe to ***signInWithFirebaseEmailPassword*** single to start the signIn flow

```kotlin

signInWithFirebaseEmailPassword("email@email.com", "password")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ socialUser -> Log.d("User", socialUser.toString())}) { /* error */ }
                    
```
###### Result :

The [Social User](#social-user) entity


###### Errors :

***Cancelled*** error if the task is cancelled

***UnknownFirebaseError*** for any unkown error during the firebase auth flow

***FirebaseException*** see [Firebase Exception Docs](https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuthException)



## Firebase Facebook

### Auth

Subscribe to ***authWithFirebaseFacebook*** single to start the firebase facebook signIn flow

```kotlin

authWithFirebaseFacebook(activity) // pass your current activity 
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ socialUser -> Log.d("User", socialUser.toString())}) { /* error */ }
                    
```
###### Result :

The [Social User](#social-user) entity


###### Errors :

***FacebookException*** see [Facebook Exception Docs](https://developers.facebook.com/docs/reference/androidsdk/5.9.0/facebook/com/facebook/facebookexception.html/)

***FacebookAuthError*** for any unkown error during the facebook auth flow

***Cancelled*** error if the task is cancelled

***UnknownFirebaseError*** for any unkown error during the firebase auth flow

***FirebaseException*** see [Firebase Exception Docs](https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuthException)

<br />

### Sign Out

Call ***facebookSignOut*** if you need to logout the user from facebook social

```kotlin

facebookSignOut()        
```

<br />
<br />

## Firebase Google

### Auth

Subscribe to ***authWithFirebaseGoogle*** single to start the firebase google signIn flow

You need your web google client id ( more info in [Setup](#firebase) ) 

```kotlin

authWithFirebaseGoogle(activity, googleClientIdWeb)) // pass your current activity and your web google client id 
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ socialUser -> Log.d("User", socialUser.toString())}) { /* error */ }
                    
```

###### Result :

The [Social User](#social-user) entity


###### Errors :

***ApiException*** see [Api Exception Docs](https://developers.google.com/android/reference/com/google/android/gms/common/api/ApiException)

***GoogleAuthError*** for any unkown error during the google auth flow

***Cancelled*** error if the task is cancelled

***UnknownFirebaseError*** for any unkown error during the firebase auth flow

***FirebaseException*** see [Firebase Exception Docs](https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuthException)

<br />

### Sign Out

Subscribe to ***googleSignOut*** single if you need to logout the user form google social

```kotlin

googleSignOut(this, this.getString(R.string.google_client_id_web))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ /* success */ }) { /* error */ }
                    
```

###### Result :

Unit object

<br />
<br />

## User Managment

### Token

Subscribe to ***getFirebaseToken*** to get the firebase token of the logged in user

```kotlin

getFirebaseToken()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ id -> Log.d("ID", id)}) { /* error */ }
                    
```

###### Result :

The firebase token of the user as a String


###### Errors :

***FirebaseUserNotLogged*** error if the user is not logged in

***Cancelled*** error if the task is cancelled

***UnknownFirebaseError*** for any other error during the flow

***FirebaseException*** see [Firebase Exception Docs](https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuthException)

<br />
<br />

### Id

Call getFirebaseId to get the firebase id of the logged in user

```kotlin

val id = getFirebaseId() 

if (id != null) { 
    Log.d("ID", id)
}
                    
```

###### Result

The firebase id of the user, it can be null if the user is not logged in

<br />
<br />

### User

Subscribe to getCurrentFirebaseUser to get the current logged in firebase user

```kotlin

getCurrentFirebaseUser()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ socialUser -> Log.d("User", socialUser.toString())}) { /* error */ }
                    
```
###### Result :

The [Social User](#social-user) entity


###### Errors :

***FirebaseUserNotLogged*** error if the user is not logged in

***Cancelled*** error if the task is cancelled

***UnknownFirebaseError*** for any other error during the flow

***FirebaseException*** see [Firebase Exception Docs](https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuthException)

<br />
<br />

### Profile

Subscribe to updateFirebaseProfile to update some info of the firebase user's profile

```kotlin

updateFirebaseProfile("NewFirstName NewLastName", "www.new-photo-url.png")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ /* success */ }) { /* error */ }
                    
```

###### Result :

Unit object


###### Errors :

***FirebaseUserNotLogged*** error if the user is not logged in

***Cancelled*** error if the task is cancelled

***UnknownFirebaseError*** for any other error during the flow

***FirebaseException*** see [Firebase Exception Docs](https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuthException)


<br />
<br />


### Password

#### Update

Subscribe to updateFirebasePassword to update the password of the current logged in user

```kotlin

updateFirebasePassword("NewPassword)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ /* success */ }) { /* error */ }
                    
```
###### Result :

Unit object


###### Errors :

***FirebaseUserNotLogged*** error if the user is not logged in

***Cancelled*** error if the task is cancelled

***UnknownFirebaseError*** for any other error during the flow

***FirebaseException*** see [Firebase Exception Docs](https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuthException)

<br />

#### Reset

Subscribe to resetFirebasePassword to start the password reset flow through email

```kotlin

resetFirebasePassword("email@email.com")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ /* success */ }) { /* error */ }
                    
```
###### Result :

Unit object


###### Errors :

***Cancelled*** error if the task is cancelled

***UnknownFirebaseError*** for any other error during the flow

***FirebaseException*** see [Firebase Exception Docs](https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuthException)

<br />
<br />
<br />

## Social User

The Social user entity contains all user information that was obtained from the chosen authentication method

<br />
<br />


Field | Description 
------ | ---------- 
id | The id that representing the user logged into the chosen authentication system 
token | The token that representing the user logged into the chosen authentication system 
displayName | The full name of the user ( can be null )
firstName | The first name ( can be null )
lastName | The last name ( can be null )
email | The email ( can be null )
profileImage | The url of the social profile image ( can be null )