package pro.devapp.clock.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import pro.devapp.clock.R
import pro.devapp.clock.databinding.FragmentMirrorBinding
import pro.devapp.clock.viewModels.MirrorViewModel
import android.hardware.Camera
import java.io.IOException
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
        releaseCamera()
    }

    private fun openCamera() {
        val permission = ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission()
            return
        }
        camera = Camera.open(mBinding.model!!.getCamera())
        val rectPreview = mBinding.model!!.setPreviewSize(camera, activity)
        // установка размеров surface из получившегося преобразования
        if(rectPreview != null){
            mBinding.surfaceView.getLayoutParams().height = rectPreview.bottom.toInt()
            mBinding.surfaceView.getLayoutParams().width = rectPreview.right.toInt()
        }
    }

    private fun releaseCamera() {
        if (camera != null){
            camera!!.release()
        }
        camera = null
    }

    private fun restartCamera() {
        if (camera != null){
            camera!!.stopPreview()
            val result = mBinding.model!!.setCameraDisplayOrientation(activity)
            if(camera != null) {
                camera!!.setDisplayOrientation(result)
            }
            try {
                camera!!.setPreviewDisplay(holder)
                camera!!.startPreview()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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
            restartCamera()
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {

        }

    }
}