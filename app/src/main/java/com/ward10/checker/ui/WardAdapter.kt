package com.ward10.checker.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ward10.checker.R
import com.ward10.checker.db.WardPerson

class WardAdapter(private val onCheck: (WardPerson) -> Unit)
    : RecyclerView.Adapter<WardAdapter.VH>() {

    private val items = mutableListOf<WardPerson>()

    fun setData(list: List<WardPerson>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val root: LinearLayout = v.findViewById(R.id.itemRoot)
        val tvName: TextView = v.findViewById(R.id.tvName)
        val tvMobile: TextView = v.findViewById(R.id.tvMobile)
        val cb: CheckBox = v.findViewById(R.id.cbCheck)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_person, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(h: VH, pos: Int) {
        val p = items[pos]
        h.tvName.text = p.name
        h.tvMobile.text = p.mobile
        h.cb.setOnCheckedChangeListener(null)
        h.cb.isChecked = p.isChecked

        h.root.setBackgroundColor(if (p.isChecked) Color.parseColor("#C8E6C9") else Color.WHITE)

        h.cb.setOnCheckedChangeListener { _, checked ->
            if (checked && !p.isChecked) onCheck(p)
        }
    }
}
