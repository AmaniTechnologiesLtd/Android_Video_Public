<h1 align="center">Amani Video SDK </h1></br>

![amani-label](https://github.com/AmaniTechnologiesLtd/Android_Video_Public/assets/75306240/47b89bb3-f6bc-4104-8047-bb7f6e5f7b39)


This README would normally document whatever steps are necessary to get your application up and running.

# Table of Content
- [Initial Configuration](#initial-configuration)
- [SDK Usage](#sdk-usage)
- [ProGuard Rule Usage](#proguard-rule-usage)

## Initial Configuration ##

   1. Add the following dependencies to your Module build.gradle file.
```groovy
    implementation 'ai.amani.android:amanivideosdk:1.1.3'
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
private val videoCallObserver: AmaniVideoCallObserver = object : AmaniVideoCallObserver{
    override fun onConnectionState(connectionState: ConnectionState) {
        when (connectionState) {
            ConnectionState.CONNECTING -> {
                //Agent is connecting to the call
            }

            ConnectionState.FAILED -> {
                //Connection is failed
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

    override fun onException(exception: String) {
        //Any exception during the call
    }

    override fun onUiEvent(
        amaniVideoButtonEvents: AmaniVideoButtonEvents,
        isActivated: Boolean
    ) {
        when (amaniVideoButtonEvents) {
            AmaniVideoButtonEvents.CALL_END -> {
                //The call end button is clicked by mobile SDK user and call ended
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
                //The call end button is clicked by studio agent side and call is ended
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
      val callFragment: Fragment? = VideoSDK.startVideoCall(videoBuilder)
            callFragment?.let {
                replaceFragment(R.id.container, callFragment)
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

## ProGuard Rule Usage ##

   * If you are using ProGuard in your application, you just need to add this line into your ProGuard Rules!

   ```java
-keep class ai.amani** {*;}
-dontwarn ai.amani**
-keep class com.cloudwebrtc.webrtc.** { *; }
-keep class org.webrtc.** { *; }

   ```     

