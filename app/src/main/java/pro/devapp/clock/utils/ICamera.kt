package pro.devapp.clock.utils

import android.view.SurfaceView
import androidx.fragment.app.FragmentActivity

interface ICamera: IPermissionManager {
    fun getActivity(): FragmentActivity?
    fun getSurfaceView(): SurfaceView
}