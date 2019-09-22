package pro.devapp.clock.utils

import android.hardware.Camera
import android.view.SurfaceHolder
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import java.io.IOException

class CameraManager () : LifecycleObserver, IPermissionCallback {

    private lateinit var holder: SurfaceHolder
    private var camera: Camera? = null

    private val REQUEST_CAMERA_PERMISSION = 100

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        val surfaceView = uiProvider.getSurfaceView()
        surfaceViewHolder = SurfaceViewHolder(surfaceView)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    internal inner class HolderCallback : SurfaceHolder.Callback {

        override fun surfaceCreated(holder: SurfaceHolder) {
            try {
                camera?.setPreviewDisplay(holder)
                camera?.startPreview()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        override fun surfaceChanged(holder: SurfaceHolder,
                                    format: Int,
                                    width: Int,
                                    height: Int) {
            restartCamera()
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {

        }

    }
}