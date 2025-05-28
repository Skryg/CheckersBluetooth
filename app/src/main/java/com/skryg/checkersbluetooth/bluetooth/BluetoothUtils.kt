package com.skryg.checkersbluetooth.bluetooth

import android.Manifest
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import java.util.UUID


class BluetoothUtils {
    companion object {
        val MY_UUID: UUID by lazy { UUID.fromString("d17b99ce-233c-43fc-8960-1c188b38b2b8") }
        const val NAME = "checkers_bluetooth"

        const val REQUEST_ENABLE_BT = 100
        const val REQUEST_CODE_BLUETOOTH_PERMISSIONS = 2001

        fun requiredPermissions(): Array<String> {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                return arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_ADVERTISE,
                )
            }

            return arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        fun checkGranted(applicationContext: Context, permissions: Array<String>): Boolean {
            permissions.forEach { permission ->
                if (ContextCompat.checkSelfPermission(applicationContext, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
            return true
        }
    }
}

//fun ComponentActivity.requestBluetoothPermissions() {
//    fun checkPermission(permission: String): Boolean {
//        return ContextCompat.checkSelfPermission(this.applicationContext, permission) == PackageManager.PERMISSION_GRANTED
//    }
//    // Check for Bluetooth permissions based on Android version
//
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//        if (!checkPermission(Manifest.permission.BLUETOOTH_CONNECT) ||
//            !checkPermission(Manifest.permission.BLUETOOTH_SCAN) ||
//            !checkPermission(Manifest.permission.BLUETOOTH_ADVERTISE)
//        ) {
//            requestPermissions(
//                arrayOf(
//                    Manifest.permission.BLUETOOTH_CONNECT,
//                    Manifest.permission.BLUETOOTH_SCAN,
//                    Manifest.permission.BLUETOOTH_ADVERTISE
//                ),
//                REQUEST_CODE_BLUETOOTH_PERMISSIONS
//            )
//        }
//    } else {
//        if(!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)){
//            requestPermissions(
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                REQUEST_CODE_BLUETOOTH_PERMISSIONS
//            )
//        }
//    }
//}
