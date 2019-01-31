package bluetooth.list.lln

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import bluetooth.list.lln.Adapter.BTAdapter
import bluetooth.list.lln.Helper.BTHelper
import bluetooth.list.lln.Listener.BTListener
import bluetooth.list.lln.Model.BTModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BTListener {

    private lateinit var bluetoothHelper: BTHelper

    private lateinit var viewAdapter: RecyclerView.Adapter<*>

    private var itemList = ArrayList<BTModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bluetoothHelper = BTHelper(this, this)
            .setPermissionRequired(true)
            .create()

        if (bluetoothHelper.isBluetoothEnabled()) enable_disable.text = "Bluetooth State Off"
        else enable_disable.text = "Bluetooth State On"

        if (bluetoothHelper.isBluetoothScanning()) start_stop.text = "Stop discovery"
        else start_stop.text = "Start discovery"


        enable_disable.setOnClickListener {
            if (bluetoothHelper.isBluetoothEnabled()) {

                bluetoothHelper.disableBluetooth()

            } else {
                bluetoothHelper.enableBluetooth()
            }
        }

        start_stop.setOnClickListener {
            if (bluetoothHelper.isBluetoothScanning()) {
                bluetoothHelper.stopDiscovery()

            } else {
                bluetoothHelper.startDiscovery()
            }
        }

        viewAdapter = BTAdapter(itemList)
        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = viewAdapter
        }
    }

    override fun onStartDiscovery() {
        start_stop.text = "Stop discovery"

    }

    override fun onFinishDiscovery() {
        start_stop.text = "Start discovery"
        itemList.clear()
    }

    override fun onEnabledBluetooth() {
        enable_disable.text = "Bluetooth State Off"
    }

    override fun onDisabledBluetooh() {
        enable_disable.text = "Bluetooth State On"

    }

    override fun getBluetoothDeviceList(device: BluetoothDevice) {
        itemList.add(BTModel(device.name, device.address))
        viewAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        bluetoothHelper.registerBluetoothStateChanged()
    }


    override fun onStop() {
        super.onStop()
        bluetoothHelper.unregisterBluetoothStateChanged()
    }
}
