package com.example.clickaplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class DataAdapter(val dataList: List<Data>): RecyclerView.Adapter<DataViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        var view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.data_row, parent, false)

        return DataViewHolder(view)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val data = dataList.get(position)

        holder.setNewData(data)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

}