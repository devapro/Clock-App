package pro.devapp.clock.utils

interface IPermissionCallback{
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray): Boolean
}