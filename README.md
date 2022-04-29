# Amani Video SDK #

This README would normally document whatever steps are necessary to get your application up and running.

# Table of Content
- [Initial Configuration](#initial-configuration)
- [SDK Usage](#sdk-usage)
- [ProGuard Rule Usage](#proguard-rule-usage)

## Initial Configuration ##

   1. Add the following dependencies to your Module build.gradle file.
```groovy
    implementation 'ai.amani.android:amanivideosdk:1.0.6'
```

   2. Enable view-binding in the Module build.gradle by adding this line into code block of android {}:

```groovy
buildFeatures {
        viewBinding true
    }
```

  3. Add the following in the Project build.gradle within in buildscript within the buildscript->repositories and buildscript->allprojects.
```gradle
    maven { url "https://jfrog.amani.ai/artifactory/amani-video-sdk"}
```

## SDK Usage ##

 * Step 1: Create a global value of type VideoSDK.Builder.
 ```kotlin
     /** Video Call configuration object*/
    private lateinit var videoBuilder: VideoSDK.Builder
 ```
  * Step 2: Create a global AmaniVideoCallObserver object like below
  
  ```kotlin
     private val videoCallObserver: AmaniVideoCallObserver = object :
        AmaniVideoCallObserver {
        override fun onConnectionState(connectionState: ConnectionState) {
            when (connectionState) {
                ConnectionState.CONNECTING -> {
                    Log.i(TAG, "Connecting: ")
                }

                ConnectionState.FAILED -> {
                    Log.i(TAG, "Failed: ")
                }

                ConnectionState.CONNECTED -> {
                    Log.i(TAG, "Connected: ")
                    visibleLoader(false)
                }

                ConnectionState.DISCONNECTED -> {
                    Log.i(TAG, "Disconnected: ")
                }
            }
        }

        override fun onException(exception: String) {
            Log.e("TAG", "onException: $exception")
        }

        override fun onUiEvent(
            amaniVideoButtonEvents: AmaniVideoButtonEvents,
            isActivated: Boolean
        ) {
            when (amaniVideoButtonEvents) {
                AmaniVideoButtonEvents.CALL_END -> {
                    if (isActivated) {
                        Log.i(TAG, "Call is end-up")
                    }
                }
                AmaniVideoButtonEvents.CAMERA_SWITCH -> {
                    if (isActivated) {
                        Log.i(TAG, "Camera switched to back camera")
                    } else Log.i(TAG, "Camera re-switch to front camera")
                }

                AmaniVideoButtonEvents.MUTE -> {
                    if (isActivated) {
                        Log.i(TAG, "Muted")
                    } else Log.i(TAG, "Unmuted")
                }

                AmaniVideoButtonEvents.CAMERA_CLOSE -> {
                    if (isActivated) {
                        Log.i(TAG, "Camera closed")
                    } else Log.i(TAG, "Camera re-opened")
                }
            }
        }
    }
 ```

 * Step 3: Configure the VideoSDK.Builder type object you created as follows.

 ```kotlin
         videoBuilder = VideoSDK.Builder()
                .context(this) //Application context
                .serverUrl("SERVER_URL") // Server URL as String
                .nameSurname("Name Surname") //Name and Surname of user
                .token("TOKEN") //Must use customerToken
                .videoCallObserver(videoCallObserver) //Use the object created at step2!
                .build()
 ```

  * Step 4: In this step, we will use startCallVideoCall method to get a Fragment (it could be null check it out while using it). You can commit or navigate this fragment. For this, use the object as typed VideoSDK.Builder that we created step 3 to get Fragment.

 ```kotlin
      val callFragment: Fragment? = VideoSDK.startVideoCall(videoBuilder)
            callFragment?.let {
                replaceFragment(R.id.container, callFragment)
            }
 ```

## ProGuard Rule Usage ##

   * If you are using ProGuard in your application, you just need to add this line into your ProGuard Rules!

   ```java
-keep class ai.amani** {*;}
-dontwarn ai.amani**

   ```     

