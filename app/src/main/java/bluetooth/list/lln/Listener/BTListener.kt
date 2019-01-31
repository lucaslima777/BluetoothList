package bluetooth.list.lln.Listener

import android.bluetooth.BluetoothDevice

interface BTListener {

    fun onStartDiscovery()

    fun onFinishDiscovery()

    fun onEnabledBluetooth()

    fun onDisabledBluetooh()

    fun getBluetoothDeviceList(device: BluetoothDevice)
}