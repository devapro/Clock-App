package pro.devapp.clock.viewModels

import android.app.Activity
import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.hardware.Camera
import android.view.Surface
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build





class MirrorViewModel : ViewModel(), LifecycleObserver {

    /**
     * Get front camera id
     */
    fun getCamera(context: Context?): Int{
        if (Build.VERSION.SDK_INT < 22) {
            val ci = Camera.CameraInfo()
            for (i in 0 until Camera.getNumberOfCameras()) {
                Camera.getCameraInfo(i, ci)
                if (ci.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) return i
            }
        } else if(context != null) {
            val cManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            try {
                for (j in cManager.cameraIdList.indices) {
                    val cameraId = cManager.cameraIdList
                    val characteristics = cManager.getCameraCharacteristics(cameraId[j])
                    val cOrientation = characteristics.get(CameraCharacteristics.LENS_FACING)!!
                    if (cOrientation == CameraCharacteristics.LENS_FACING_FRONT)
                        return j
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return  -1
    }

    /**
     * Calculate preview sizes
     */
    fun setPreviewSize(camera: Camera?, context: Activity?): RectF?{
        if (camera == null || context == null) {
            return null
        }
        // get screen sizes
        val display = context.getWindowManager().getDefaultDisplay()
        // check current screen orientation
        val widthIsMax = display.getWidth() > display.getHeight()

        // get preview sizes
        val size = camera.getParameters().previewSize

        val rectDisplay = RectF()
        val rectPreview = RectF()

        // RectF for screen sizes
        rectDisplay.set(0f, 0f, display.getWidth().toFloat(), display.getHeight().toFloat())

        // RectF for preview sizes
        if (widthIsMax) {
            // landscape
            rectPreview.set(0f, 0f, size.width * 1f, size.height * 1f)
        } else {
            // portrait
            rectPreview.set(0f, 0f, size.height * 1f, size.width * 1f)
        }

        val matrix = Matrix()
        matrix.setRectToRect(
            rectDisplay, rectPreview,
            Matrix.ScaleToFit.START
        )
        matrix.invert(matrix)
        matrix.mapRect(rectPreview)

        return rectPreview
    }

    fun setCameraDisplayOrientation(context: Activity?, cameraId: Int): Int {
        if(context == null) {
            return 0
        }
        // get current screen rotation
        val rotation = context.getWindowManager().getDefaultDisplay().getRotation()
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }

        var result = 0

        // get info about camera
        val info = Camera.CameraInfo()
        Camera.getCameraInfo(cameraId, info)

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) { // back camera
            result = 360 - degrees + info.orientation
        } else if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) { // front camera
            result = 360 - degrees - info.orientation
            result += 360
        }
        return result % 360
    }
}