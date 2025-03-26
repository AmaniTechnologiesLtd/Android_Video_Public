package com.example.amani_sdk

import ai.amani.videosdk.VideoSDK
import ai.amani.videosdk.observer.AmaniVideoButtonEvents
import ai.amani.videosdk.observer.AmaniVideoCallObserver
import ai.amani.videosdk.observer.AmaniVideoRemoteEvents
import ai.amani.videosdk.observer.CameraPosition
import ai.amani.videosdk.observer.ConnectionState
import ai.amani.videosdk.observer.SwitchCameraObserver
import ai.amani.videosdk.observer.ToggleTorchObserver
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.amani.ai.BuildConfig
import com.amani.ai.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope


class CallActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CallActivity"
    }

    /** Call starter button to start a call */
    private val callStart: Button by lazy { findViewById(R.id.call_start) }

    /** Name surname edit text input to pass it SDK */
    private val nameSurnameTxt: EditText by lazy { findViewById(R.id.name_surname_txt) }

    /** Progressbar to show until call is answered */
    private val progressLoader: ProgressBar by lazy { findViewById(R.id.progress_loader) }

    /** TextView to show current app version*/
    private val appVersion: TextView by lazy { findViewById(R.id.app_version) }

    /** Video Call configuration object*/
    private lateinit var videoBuilder: VideoSDK.Builder

    /** Video Call fragment instance*/
    private var videoCallFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appVersion.text = BuildConfig.VERSION_NAME

        clickEvent()
    }

    /**
     *  # Amani Video Call Observer
     *
     * ## [AmaniVideoCallObserver.onConnectionState]: The current states of the current call.
     *
     * [ConnectionState.CONNECTING]
     * * The current call state is on connecting, set loader or etc.
     *
     * [ConnectionState.FAILED]
     * * The current connection is failed somehow, remove call fragment
     * && hide loader/show error message
     *
     * [ConnectionState.CONNECTED]
     * * The current connection is succeed
     *
     *  [ConnectionState.DISCONNECTED]
     * * The current connection is disconnected, This event can be
     * triggered more than once, depending on internet network factors, it can be triggered from
     * time to time and the connection can be successfully established again. Unless it falls into
     * the failed state, the fragment should not be removed and the connection should be waited
     * for completion.
     *
     * ## [AmaniVideoCallObserver.onRemoteEvent]: Remote events/actions done by studio/web.
     *
     * [AmaniVideoRemoteEvents.TORCH]
     * * The event of turning on the camera's flash is requested by
     * the agent via web/studio. When this action is triggered, the function required to
     * activate the flash can be used by the SDK.
     *
     * [AmaniVideoRemoteEvents.CALL_END]
     * * Triggered if the call is terminated by the
     * studio. When this situation is triggered, the call fragment must be removed from the stack,
     * as in the example.
     *
     * [AmaniVideoRemoteEvents.CAMERA_SWITCH]
     * * The event triggered by web/studio to switch the camera.
     * In this case, it may be necessary to use the camera switch function required by the SDK.
     *
     * [AmaniVideoRemoteEvents.CALL_ESCALATED]
     * * The event triggered when the studio requests the call
     * to be transferred/forwarded to another agent. In this case, the call can be set to escalated
     * true again and started as in the example.
     *
     * ## [AmaniVideoCallObserver.onUiEvent]: Current SDK user actions within ui/fragment.
     *
     * [AmaniVideoButtonEvents.CALL_END]
     * * The event of the user to voluntarily close the call by
     * pressing the button.
     *
     * [AmaniVideoButtonEvents.CAMERA_SWITCH]
     * * The event of the user's own request to rotate the camera
     *
     * [AmaniVideoButtonEvents.MUTE]
     * * The user voluntarily turns off his microphone
     *
     * [AmaniVideoButtonEvents.CAMERA_CLOSE]
     * * The event when the user turns off their camera
     *
     * ## [AmaniVideoCallObserver.onException]: Exception messages from SDK.
     */
    private val videoCallObserver: AmaniVideoCallObserver = object :
        AmaniVideoCallObserver {
        override fun onConnectionState(connectionState: ConnectionState) {
            when (connectionState) {
                ConnectionState.CONNECTING -> {
                    Log.i(TAG, "Connecting: ")
                }

                ConnectionState.FAILED -> {
                    snackBar("Connection failed")
                    removeFragment(videoCallFragment)
                    visibleLoader(false)
                }

                ConnectionState.CONNECTED -> {
                    Log.i(TAG, "Connected: ")
                    visibleLoader(false)
                }

                ConnectionState.DISCONNECTED -> {
                    snackBar("Connection disconnected")
                    visibleLoader(false)
                }
            }
        }

        override fun onException(exception: String) {
            Log.e("TAG", "onException: $exception")
            visibleLoader(false)
            removeFragment(videoCallFragment)
            Snackbar.make(
                findViewById(R.id.layout),
                exception,
                Snackbar.LENGTH_SHORT
            ).show()
        }

        override fun onRemoteEvent(
            amaniVideoRemoteEvents: AmaniVideoRemoteEvents,
            isActivated: Boolean
        ) {
            when (amaniVideoRemoteEvents) {
                AmaniVideoRemoteEvents.CALL_END -> {
                    snackBar("Call is ended")
                    removeFragment(videoCallFragment)
                }

                AmaniVideoRemoteEvents.CAMERA_SWITCH -> {

                    alertDialog(
                        title = "Camera Switch Request",
                        message = "Camera switch is requested by agent. Could you give permission to switch it?",
                        positiveButton = "Sure",
                        negativeButton = "Not now",
                        positiveClick = {
                            VideoSDK.switchCamera(object : SwitchCameraObserver {
                                override fun onSuccess(cameraPosition: CameraPosition) {

                                }

                                override fun onException(exception: Throwable) {

                                }

                            })
                        },
                        negativeClick = {
                            snackBar("Camera switch request is denied")
                        }
                    )
                }

                AmaniVideoRemoteEvents.TORCH -> {
                    alertDialog(
                        title = "Flash Request",
                        message = "Flash is requested by agent. Could you give permission to enable it?",
                        positiveButton = "Sure",
                        negativeButton = "Not now",
                        positiveClick = {
                            VideoSDK.toggleTorch(object : ToggleTorchObserver {
                                override fun onSuccess(isEnabled: Boolean) {

                                }

                                override fun onError(error: Throwable) {
                                }
                            })
                        },
                        negativeClick = {
                            snackBar("Flash request is denied")
                        }
                    )
                }

                AmaniVideoRemoteEvents.CALL_ESCALATED -> {

                    removeFragment(videoCallFragment)

                    setVideoSDK(escalated = true)

                    navigateVideoSDKFragment()
                }
            }
        }

        override fun onUiEvent(
            amaniVideoButtonEvents: AmaniVideoButtonEvents,
            isActivated: Boolean
        ) {
            when (amaniVideoButtonEvents) {
                AmaniVideoButtonEvents.CALL_END -> {
                    if (isActivated) {
                        removeFragment(videoCallFragment)
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
                    } else Log.i(TAG, "Um-muted")
                }

                AmaniVideoButtonEvents.CAMERA_CLOSE -> {
                    if (isActivated) {
                        Log.i(TAG, "Camera closed")
                    } else Log.i(TAG, "Camera re-opened")
                }
            }
        }
    }

    /** Sets the Video SDK configuration, creates the Video Call Fragment instance
     *
     * @param escalated : To escalate the current call to another agent
     */
    private fun setVideoSDK(escalated: Boolean = false) {
        videoBuilder = VideoSDK.Builder()
            .nameSurname(nameSurname = nameSurnameTxt.text.toString())
            .escalatedCall(escalated = escalated)
            .servers(
                mainServerURL = VideoSDKCredentials.mainServerURL,
                stunServerURL = VideoSDKCredentials.stunServerURL,
                turnServerURL = VideoSDKCredentials.turnServerURL
            )
            .authentication(
                token = VideoSDKCredentials.token,
                userName = VideoSDKCredentials.userName,
                password = VideoSDKCredentials.password
            )
            .remoteViewAspectRatio(
                VideoSDK.RemoteViewAspectRatio.Vertical
            )
            .audioOptions(
                VideoSDK.AudioOptions.SpeakerPhoneOn
            )
            .userInterface(
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
            .videoCallObserver(videoCallObserver)
            .build()

        videoCallFragment = VideoSDK.startVideoCall(videoBuilder)
    }

    /**
     * Listen click events
     */
    private fun clickEvent() {
        callStart.setOnClickListener {
            if (nameSurnameTxt.text.isBlank()) {
                Snackbar.make(
                    findViewById(R.id.layout),
                    resources.getString(R.string.name_surname_blank),
                    Snackbar.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            setVideoSDK()
            navigateVideoSDKFragment()
        }
    }

    /**
     * Navigating Video Call Fragment
     */
    private fun navigateVideoSDKFragment() {
        visibleLoader(true)
        videoCallFragment?.let {
            replaceFragmentWithBackStack(R.id.container, it)
        }
    }

    /**
     * Show/hide progressbar
     */
    private fun visibleLoader(b: Boolean) {
        runOnUiThread {
            progressLoader.apply {
                if (b) this.visibility = View.VISIBLE
                else this.visibility = View.INVISIBLE
            }
        }
    }
}
