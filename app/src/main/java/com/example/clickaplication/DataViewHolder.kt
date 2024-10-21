package com.example.clickaplication

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DataViewHolder(view: View) : RecyclerView.ViewHolder(view){

    private var dataTitle: TextView?
    private var dataValue: TextView?
    private var dataImage: ImageView?

    init {
        dataTitle = view.findViewById(R.id.nadpis)
        dataValue = view.findViewById(R.id.udaj)
        dataImage = view.findViewById(R.id.image)
    }

    fun setNewData(data: Data) {
        dataTitle?.text = data.title
        dataValue?.text = data.value
        dataImage?.setImageResource(data.imageResId)
    }
}