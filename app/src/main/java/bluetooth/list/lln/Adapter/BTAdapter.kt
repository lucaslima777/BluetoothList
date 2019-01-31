package bluetooth.list.lln.Adapter

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import bluetooth.list.lln.Model.BTModel
import bluetooth.list.lln.R
import kotlinx.android.synthetic.main.item_bluetooth_device.view.*


class BTAdapter(private var item: ArrayList<BTModel>) :
    RecyclerView.Adapter<BTAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bluetooth_device, parent, false)
        return ViewHolder(view as ConstraintLayout)
    }

    override fun getItemCount() = item.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            device_name.text = item[position].name
            macAddress.text = item[position].macNumber
        }
    }

    class ViewHolder(val view: ConstraintLayout) : RecyclerView.ViewHolder(view)
}