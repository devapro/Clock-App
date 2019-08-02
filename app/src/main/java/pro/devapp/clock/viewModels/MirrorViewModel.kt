package pro.devapp.clock.viewModels

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.graphics.RectF
import android.hardware.Camera
import android.view.Surface
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import pro.devapp.clock.fragments.MirrorFragment
import java.io.IOException

class MirrorViewModel : ViewModel(), LifecycleObserver {
    private lateinit var holderCallback: MirrorFragment.HolderCallback
    private var fullScreen = true
    private var cameraId = 0

    fun getCamera(): Int{
        return cameraId
    }

    fun setPreviewSize(camera: Camera?, context: Activity?): RectF?{
        if (camera == null || context == null) {
            return null
        }
        // получаем размеры экрана
        val display = context!!.getWindowManager().getDefaultDisplay()
        val widthIsMax = display.getWidth() > display.getHeight()

        // определяем размеры превью камеры
        val size = camera!!.getParameters().previewSize

        val rectDisplay = RectF()
        val rectPreview = RectF()

        // RectF экрана, соотвествует размерам экрана
        rectDisplay.set(0f, 0f, display.getWidth().toFloat(), display.getHeight().toFloat())

        // RectF первью
        if (widthIsMax) {
            // превью в горизонтальной ориентации
            rectPreview.set(0f, 0f, size.width * 1f, size.height * 1f)
        } else {
            // превью в вертикальной ориентации
            rectPreview.set(0f, 0f, size.height * 1f, size.width * 1f)
        }

        val matrix = Matrix()
        // подготовка матрицы преобразования
        if (!fullScreen) {
            // если превью будет "втиснут" в экран (второй вариант из урока)
            matrix.setRectToRect(
                rectPreview, rectDisplay,
                Matrix.ScaleToFit.START
            )
        } else {
            // если экран будет "втиснут" в превью (третий вариант из урока)
            matrix.setRectToRect(
                rectDisplay, rectPreview,
                Matrix.ScaleToFit.START
            )
            matrix.invert(matrix)
        }
        // преобразование
        matrix.mapRect(rectPreview)

        return rectPreview;
    }

    fun setCameraDisplayOrientation(context: Activity?): Int {
        if(context == null) {
            return 0
        }
        // определяем насколько повернут экран от нормального положения
        val rotation = context!!.getWindowManager().getDefaultDisplay().getRotation()
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }

        var result = 0

        // получаем инфо по камере cameraId
        val info = Camera.CameraInfo()
        Camera.getCameraInfo(cameraId, info)

        // задняя камера
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
            result = 360 - degrees + info.orientation
        } else
        // передняя камера
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = 360 - degrees - info.orientation
                result += 360
            }
        result = result % 360
        return result
    }
}