package pro.devapp.clock.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import pro.devapp.clock.R
import pro.devapp.clock.databinding.FragmentMirrorBinding
import pro.devapp.clock.viewModels.MirrorViewModel
import android.view.*
import androidx.core.content.ContextCompat
import pro.devapp.clock.utils.CameraManager
import pro.devapp.clock.utils.ICamera
import pro.devapp.clock.utils.checkPermissionCamera


class MirrorFragment: Fragment(), ICamera{

    private lateinit var mBinding: FragmentMirrorBinding
    private lateinit var cameraManager: CameraManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_mirror, container,false)
        mBinding.lifecycleOwner = this
        mBinding.model = ViewModelProviders.of(this).get(MirrorViewModel::class.java)

        cameraManager = CameraManager(this.lifecycle, this)
        lifecycle.addObserver(cameraManager)

        return  mBinding.root
    }

    override fun checkPermission(permission: String): Boolean {
        return checkPermissionCamera(activity!!)
    }

    override fun getSurfaceView(): SurfaceView {
        return mBinding.surfaceView
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if(!cameraManager.onRequestPermissionsResult(requestCode,permissions, grantResults)){
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}