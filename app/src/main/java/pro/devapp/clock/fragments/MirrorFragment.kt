package pro.devapp.clock.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import pro.devapp.clock.R
import pro.devapp.clock.databinding.FragmentMirrorBinding
import pro.devapp.clock.viewModels.MirrorViewModel
import android.hardware.Camera
import java.io.IOException
import android.graphics.RectF
import android.graphics.Matrix
import android.hardware.Camera.CameraInfo
import android.view.*
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.core.content.ContextCompat

class MirrorFragment: Fragment(){
    private lateinit var mBinding: FragmentMirrorBinding

    private lateinit var holder: SurfaceHolder
    private lateinit var holderCallback: HolderCallback
    private var camera: Camera? = null

    private val REQUEST_CAMERA_PERMISSION = 121

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_mirror, container,false)
        mBinding.lifecycleOwner = this
        mBinding.model = ViewModelProviders.of(this).get(MirrorViewModel::class.java)

        holder = mBinding.surfaceView.getHolder()

        holderCallback = HolderCallback()
        holder.addCallback(holderCallback)

        return  mBinding.root
    }

    override fun onResume() {
        super.onResume()
        openCamera()
    }

    override fun onPause() {
        super.onPause()
        if (camera != null){
            camera!!.release()
        }
        camera = null
    }

    private fun openCamera() {
        val permission = ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission()
            return
        }
        camera = Camera.open(0)
        setPreviewSize(true)
    }

    private fun requestCameraPermission() {
        requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.size != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Error", LENGTH_LONG).show()
            } else {
                openCamera()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    internal inner class HolderCallback : SurfaceHolder.Callback {

        override fun surfaceCreated(holder: SurfaceHolder) {
            try {
                if (camera != null){
                    camera!!.setPreviewDisplay(holder)
                    camera!!.startPreview()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        override fun surfaceChanged(
            holder: SurfaceHolder, format: Int, width: Int,
            height: Int
        ) {
            if (camera != null){
                camera!!.stopPreview()
                setCameraDisplayOrientation(0)
                try {
                    camera!!.setPreviewDisplay(holder)
                    camera!!.startPreview()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {

        }

    }

    fun setPreviewSize(fullScreen: Boolean) {
        if (camera == null) {
            return
        }
        // получаем размеры экрана
        val display = activity!!.getWindowManager().getDefaultDisplay()
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

        // установка размеров surface из получившегося преобразования
        mBinding.surfaceView.getLayoutParams().height = rectPreview.bottom.toInt()
        mBinding.surfaceView.getLayoutParams().width = rectPreview.right.toInt()
    }

    fun setCameraDisplayOrientation(cameraId: Int) {
        // определяем насколько повернут экран от нормального положения
        val rotation = activity!!.getWindowManager().getDefaultDisplay().getRotation()
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }

        var result = 0

        // получаем инфо по камере cameraId
        val info = CameraInfo()
        Camera.getCameraInfo(cameraId, info)

        // задняя камера
        if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
            result = 360 - degrees + info.orientation
        } else
        // передняя камера
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                result = 360 - degrees - info.orientation
                result += 360
            }
        result = result % 360
        if(camera != null) {
            camera!!.setDisplayOrientation(result)
        }
    }
}