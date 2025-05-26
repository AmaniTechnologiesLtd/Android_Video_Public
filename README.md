<h1 align="center">Amani Video SDK </h1></br>

![amani-label](https://github.com/AmaniTechnologiesLtd/Android_Video_Public/assets/75306240/47b89bb3-f6bc-4104-8047-bb7f6e5f7b39)

![GitHub release (latest by date)](https://img.shields.io/github/v/release/AmaniTechnologiesLtd/Android_Video_Public)


# Table of Content
- [General Requirements](#general-requirements)
- [Initial Configuration](#initial-configuration)
- [SDK Usage](#sdk-usage)
- [ProGuard Rule Usage](#proguard-rule-usage)

## General Requirements

### The minimum requirements for the SDK

* minSdkVersion 21
* compileSdk 34

Compiled with Java 17, minimum Java Version should be 17 as follows.

```groovy
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget=17
    }

    kotlin {
        jvmToolchain(17)
    }
```

### Orientation Requirement 

The Amani Video Call SDK **only supports portrait orientation** 🔒

> ⚠️ **Warning**    
> You **must enforce portrait mode** before launching the SDK fragment.  
> Not doing so may result in malfunction or a broken user experience.



📱 Enforce portrait orientation in Android, before initializing the SDK to make sure the hosting activity is locked to portrait mode:

```kotlin
// Lock activity to portrait before launching the SDK
requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
```

After the Video Call is completed, you can change the screen orientation restriction as you wish.

```kotlin
// Restore original orientation behavior
requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
```

## Initial Configuration ##

   1. Add the following dependencies to your Module build.gradle file. Latest version -> ![GitHub release (latest by date)](https://img.shields.io/github/v/release/AmaniTechnologiesLtd/Android_Video_Public)

```groovy
    implementation 'ai.amani.android:amanivideosdk:latest_version'
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

  * Step 1: Create a global value of type VideoSDK.Builder and Video Call Fragment instance
 ```kotlin
     /** Video Call configuration object*/
    private lateinit var videoBuilder: VideoSDK.Builder

    /** Video Call Fragment instance*/
    private var videoCallFragment: Fragment? = null
 ```
  * Step 2: Create a global AmaniVideoCallObserver object like below

  ```kotlin
private val videoCallObserver: AmaniVideoCallObserver = object : AmaniVideoCallObserver{
    override fun onConnectionState(connectionState: ConnectionState) {
        when (connectionState) {
            ConnectionState.CONNECTING -> {
                //Agent is connecting to the call
            }

            ConnectionState.FAILED -> {
                //Connection is failed

                //Remove fragment with supported fragment manager from stack
                removeFragment(videoCallFragment)
            }

            ConnectionState.CONNECTED -> {
                //Agent is connect to the video call
            }

            ConnectionState.DISCONNECTED -> {
                //Agent is disconnected from call as unexpected situation like electricity gone
                //or possible internet connection issues
            }
        }
    }

    override fun onError(error: String) {
        //Any exception during the call

        //Remove fragment with supported fragment manager from stack
        removeFragment(videoCallFragment)
    }

    override fun onUiEvent(
        amaniVideoButtonEvents: AmaniVideoButtonEvents,
        isActivated: Boolean
    ) {
        when (amaniVideoButtonEvents) {
            AmaniVideoButtonEvents.CALL_END -> {
                //The call end button is clicked by mobile SDK user and call ended

                //Remove fragment with supported fragment manager from stack
                removeFragment(videoCallFragment)
            }

            AmaniVideoButtonEvents.CAMERA_SWITCH -> {
                //The camera switch button is clicked by mobile SDK user and camera switched
            }

            AmaniVideoButtonEvents.MUTE -> {
                //The mic mute button is clicked by mobile SDK user and mic muted
            }

            AmaniVideoButtonEvents.CAMERA_CLOSE -> {
                //The camera close button is clicked by mobile SDK user and camera closed
            }
        }
    }

    override fun onRemoteEvent(
        amaniVideoRemoteEvents: AmaniVideoRemoteEvents,
        isActivated: Boolean
    ) {
        when (amaniVideoRemoteEvents) {
            AmaniVideoRemoteEvents.CALL_END -> {
                 //Remove fragment with supported fragment manager from stack
                if (isActivated) {
                        removeFragment(videoCallFragment)
                    }
            }

            AmaniVideoRemoteEvents.CAMERA_SWITCH -> {
                //The camera switch request button is clicked by studio agent. At this time
                //you can ask user to switch camera or you can directly switch camera thanks to
                //switch function @see VideoSDK.switchCamera()
            }

            AmaniVideoRemoteEvents.TORCH -> {
                //The torch flash request button is clicked by studio agent. At this time
                //you can ask user to enable flash or you can directly enable flash thanks to
                //switch function @see VideoSDK.toggleTorch()
            }

             AmaniVideoRemoteEvents.CALL_ESCALATED -> {
                //The event triggered when the studio requests the call
                //to be transferred/forwarded to another agent. In this case, the call can be
                //set to escalated true again and started as in the example.

                //Remove fragment with supported fragment manager from stack
                removeFragment(videoCallFragment)
            }
        }
    }
}
 ```

 * Step 3: Configure the VideoSDK.Builder type object you created as follows.

 ```kotlin
videoBuilder = VideoSDK.Builder()
    .nameSurname("Name Surname") //Mandatory field
    .servers(
        mainServerURL = VideoSDKCredentials.mainServerURL, //Mandatory field
        stunServerURL = VideoSDKCredentials.stunServerURL, //Mandatory field
        turnServerURL = VideoSDKCredentials.turnServerURL  //Mandatory field
    )
    .escalatedCall(escalated = true/false) //To escalate the current call
    .authentication(
        token = VideoSDKCredentials.token, //Mandatory field
        userName = VideoSDKCredentials.userName, //Mandatory field
        password = VideoSDKCredentials.password //Mandatory field
    )
    .remoteViewAspectRatio(
        VideoSDK.RemoteViewAspectRatio.Vertical //Default is Vertical, non mandatory field
    )
    .audioOptions(
        VideoSDK.AudioOptions.SpeakerPhoneOn //Default is SpeakerPhoneOn, non mandatory field
    )
    .userInterface( //Has default user interface, non mandatory function
        cameraSwitchButton = R.drawable.ic_camera_switch,
        cameraSwitchButtonBackground = R.drawable.oval_gray,
        microphoneMuteButton = R.drawable.ic_mic_on,
        microphoneMuteButtonEnabled = R.drawable.ic_mic_off,
        microphoneMuteButtonBackground = R.drawable.oval_gray,
        cameraCloseButton = R.drawable.ic_camera_on,
        cameraCloseButtonEnabled = R.drawable.ic_camera_off,
        cameraCloseButtonBackground = R.drawable.oval_gray,
        callEndButton = R.drawable.call_end,
        callEndButtonBackground = R.drawable.oval_red
    )
    .videoCallObserver(videoCallObserver) //Mandatory field
    .build() //Mandatory function

 ```

  * Step 4: In this step, we will use startCallVideoCall method to get a Fragment (it could be null check it out while using it). You can commit or navigate this fragment. For this, use the object as typed VideoSDK.Builder that we created step 3 to get Fragment.

 ```kotlin
      videoCallFragment = VideoSDK.startVideoCall(videoBuilder)
            callFragment?.let {
                replaceFragment(R.id.container, it)
            }
 ```
   * Step 5: In this step, we will cover the use of functions such as camera rotation and flash activation in the SDK. The main purpose of use of these functions is to use with a remote request by the Agent. The reason why these functions are given separately is to allow you to get permission from the user before a Flash or Camera translation request from the Agent. You can respect user permissions by using these functions if the user allows.

Camera Switch:
```kotlin
VideoSDK.switchCamera(object : SwitchCameraObserver {
    override fun onSuccess(cameraPosition: CameraPosition) {
        //The camera is switched successfully
    }

    override fun onException(exception: Throwable) {
        //Exception happened during the camera switch
    }
})
 ```

Toggle Flash:
```kotlin
VideoSDK.toggleTorch(object : ToggleTorchObserver {
    override fun onSuccess(isEnabled: Boolean) {
        //The camera flash is enabled successfully
        //Note:If the current camera is front camera the flash will not be opened
    }

    override fun onError(error: Throwable) {
        //Exception happened during the camera switch
    }
})
 ```

### Helper Functions (Additional)

Extension functions used in the usage example to navigate to and remove the Video Call Fragment.

 ``` kotlin
fun AppCompatActivity?.replaceFragment(fragmentContainer: Int, fragment: Fragment) {
    this ?: return
    this.supportFragmentManager
        .beginTransaction()
        .addToBackStack(fragment.javaClass.name)
        .replace(fragmentContainer, fragment, fragment.javaClass.name)
        .commit()
}

fun AppCompatActivity.removeFragment(fragment: Fragment?) {
    if (fragment == null) return
    this.supportFragmentManager.beginTransaction().remove(fragment).commit()
}
   ```

## ProGuard Rule Usage ##

   * If you are using ProGuard in your application, you just need to add this line into your ProGuard Rules!

   ```pro
-keep class ai.amani** {*;}
-dontwarn ai.amani**
-keep class com.cloudwebrtc.webrtc.** { *; }
-keep class org.webrtc.** { *; }

   ```     

