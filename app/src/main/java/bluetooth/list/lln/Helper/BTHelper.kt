package bluetooth.list.lln.Helper

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import bluetooth.list.lln.Constant.BTConstant
import bluetooth.list.lln.Listener.BTListener
import bluetooth.list.lln.Receiver.BTReceiver
import bluetooth.list.lln.Receiver.BTStateReceiver
import java.util.logging.Logger

class BTHelper(private val context: Context, private val listener: BTListener) {

    val LOG = Logger.getLogger(BTHelper::class.java.name)

    private val mBluetoothAdapter by lazy {
        return@lazy BluetoothAdapter.getDefaultAdapter()
    }

    private var isRequiredPermission = false

    private var isEnabled = mBluetoothAdapter.isEnabled

    private var isDiscovering = mBluetoothAdapter.isDiscovering

    private val mBluetoothStateChangeReceiver by lazy {
        object : BTStateReceiver() {
            override fun onStartDiscovering() {
                isDiscovering = true
                listener.onStartDiscovery()
            }

            override fun onFinishDiscovering() {
                isDiscovering = false
                try {
                    context.unregisterReceiver(mBluetoothDeviceFounderReceiver)
                }catch (e : IllegalArgumentException ){
                    LOG.warning("Erro" + e)
                }finally {
                    listener.onFinishDiscovery()
                }

            }

            override fun onEnabledBluetooth() {
                isEnabled = true
                listener.onEnabledBluetooth()
            }

            override fun onDisabledBluetooth() {
                isEnabled = false
                listener.onDisabledBluetooh()
            }
        }
    }

    private val mBluetoothDeviceFounderReceiver by lazy {
        object : BTReceiver() {
            override fun getFoundDevices(device: BluetoothDevice) {
                listener.getBluetoothDeviceList(device)
            }
        }
    }

    fun isBluetoothEnabled() = isEnabled

    fun isBluetoothScanning() = isDiscovering

    fun enableBluetooth() {
        if (!isEnabled) mBluetoothAdapter.enable()
    }

    fun disableBluetooth() {
        if (isEnabled) mBluetoothAdapter.disable()
    }

    fun registerBluetoothStateChanged() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        context.registerReceiver(mBluetoothStateChangeReceiver, intentFilter)
    }

    fun unregisterBluetoothStateChanged() {
        context.unregisterReceiver(mBluetoothStateChangeReceiver)
    }

    fun startDiscovery() {
        if (isEnabled && !isDiscovering) {
            mBluetoothAdapter.startDiscovery()
            val discoverDevicesIntent = IntentFilter(BluetoothDevice.ACTION_FOUND)
            context.registerReceiver(mBluetoothDeviceFounderReceiver, discoverDevicesIntent)
        }
    }

    fun stopDiscovery() {
        if (isEnabled && isDiscovering) {
            mBluetoothAdapter.cancelDiscovery()

            if (!mBluetoothAdapter.isDiscovering && mBluetoothDeviceFounderReceiver.isOrderedBroadcast) {
                context.unregisterReceiver(mBluetoothDeviceFounderReceiver)
            }
        }
    }

    private fun checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            var permissionCheck = context.checkSelfPermission(BTConstant.ACCESS_FINE_LOCATION)
            permissionCheck += context.checkSelfPermission(BTConstant.ACCESS_COARSE_LOCATION)

            if (permissionCheck != 0)
                (context as Activity).requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), BTConstant.REQ_CODE
                )
        }
    }

    fun setPermissionRequired(isRequired: Boolean): BTHelper {
        this.isRequiredPermission = isRequired
        return this
    }

    fun create(): BTHelper {
        if (this.isRequiredPermission) checkBTPermissions()
        return BTHelper(context, listener)
    }
}