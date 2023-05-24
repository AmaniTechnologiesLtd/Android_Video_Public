package com.example.amani_sdk

import ai.amani.videosdk.VideoSDK
import ai.amani.videosdk.observer.AmaniVideoButtonEvents
import ai.amani.videosdk.observer.AmaniVideoCallObserver
import ai.amani.videosdk.observer.AmaniVideoRemoteEvents
import ai.amani.videosdk.observer.ConnectionState
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.amani.ai.R
import com.google.android.material.snackbar.Snackbar


class CallActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CallActivity"
    }

    private val callStart: Button by lazy { findViewById(R.id.call_start) }
    private val nameSurnameTxt: EditText by lazy { findViewById(R.id.name_surname_txt) }
    private val progressLoader: ProgressBar by lazy { findViewById(R.id.progress_loader) }

    /** Video Call configuration object*/
    private lateinit var videoBuilder: VideoSDK.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clickEvent()
    }

    private val videoCallObserver: AmaniVideoCallObserver = object :
        AmaniVideoCallObserver {
        override fun onConnectionState(connectionState: ConnectionState) {
            when (connectionState) {
                ConnectionState.CONNECTING -> {
                    Log.i(TAG, "Connecting: ")
                }

                ConnectionState.FAILED -> {
                    Snackbar.make(
                        findViewById(R.id.layout),
                        "Connection failed",
                        Snackbar.LENGTH_SHORT
                    ).show()

                    popBackStack()
                    visibleLoader(false)
                }

                ConnectionState.CONNECTED -> {
                    Log.i(TAG, "Connected: ")
                    visibleLoader(false)
                }

                ConnectionState.DISCONNECTED -> {
                    Snackbar.make(
                        findViewById(R.id.layout),
                        "Connection disconnected",
                        Snackbar.LENGTH_SHORT
                    ).show()

                    popBackStack()
                    visibleLoader(false)
                }
            }
        }

        override fun onException(exception: String) {
            Log.e("TAG", "onException: $exception")
            visibleLoader(false)
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
            visibleLoader(true)

            videoBuilder = VideoSDK.Builder()
                .nameSurname(nameSurname = nameSurnameTxt.text.toString())
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

            val callFragment: Fragment? = VideoSDK.startVideoCall(videoBuilder)
            callFragment?.let {
                replaceFragmentWithBackStack(R.id.container, callFragment)
            }
        }
    }

    private fun visibleLoader(b: Boolean) {
        runOnUiThread {
            progressLoader.apply {
                if (b) this.visibility = View.VISIBLE
                else this.visibility = View.INVISIBLE
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        visibleLoader(false)
    }
}