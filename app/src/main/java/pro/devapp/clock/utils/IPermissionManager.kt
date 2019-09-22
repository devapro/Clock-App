package pro.devapp.clock.utils

interface IPermissionManager {
    fun checkPermission(permission: String): Boolean
}