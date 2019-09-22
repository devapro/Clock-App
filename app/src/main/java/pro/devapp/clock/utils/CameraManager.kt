package pro.devapp.clock.utils

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Camera
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import java.io.IOException

class CameraManager (private val lifecycle: Lifecycle, private val iCamera: ICamera) : LifecycleObserver, IPermissionCallback {

    private var surfaceViewHolder: HolderCallback? = null
    private var camera: Camera? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        surfaceViewHolder = HolderCallback(iCamera.getSurfaceView())
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        openCamera()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        releaseCamera()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        surfaceViewHolder?.release()
        surfaceViewHolder = null
    }

    /**
     * Open camera or request permissions
     */
    private fun openCamera() {
        if(iCamera.checkPermission(Manifest.permission.CAMERA)){
            camera = Camera.open(getCamera(iCamera.getActivity()))
            val rectPreview = setPreviewSize(camera, iCamera.getActivity())
            // set surface size
            if(rectPreview != null){
                iCamera.getSurfaceView().getLayoutParams().height = rectPreview.bottom.toInt()
                iCamera.getSurfaceView().getLayoutParams().width = rectPreview.right.toInt()
            }
        }
    }

    /**
     * Stop camera
     */
    private fun releaseCamera() {
        camera?.stopPreview()
        camera?.release()
        camera = null
    }

    /**
     * Restart after changes
     */
    private fun restartCamera() {
        if (camera != null){
            camera?.stopPreview()
            val result = setCameraDisplayOrientation(iCamera.getActivity(), getCamera(iCamera.getActivity()))
            if(camera != null) {
                camera?.setDisplayOrientation(result)
            }
            try {
                camera?.setPreviewDisplay(iCamera.getSurfaceView().holder)
                camera?.startPreview()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                    openCamera()
                }
            }
            return true
        }
        return false
    }


    internal inner class HolderCallback(val surfaceView: SurfaceView) : SurfaceHolder.Callback {

        init {
            surfaceView.holder.addCallback(this)
        }

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

        fun release() {
            surfaceView.holder.removeCallback(this)
        }

    }
}