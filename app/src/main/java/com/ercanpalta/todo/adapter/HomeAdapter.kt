package com.ercanpalta.todo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ercanpalta.todo.R
import com.ercanpalta.todo.model.ToDo

class HomeAdapter (private val dataSet: ArrayList<ToDo>): RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val textView: TextView
        var checkBox = CheckBox(view.context)

        init {
            textView = view.findViewById(R.id.task_text)
            checkBox = view.findViewById(R.id.checkbox)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = dataSet[position].task
        holder.checkBox.setOnClickListener {
            if(it is CheckBox){
                val checked: Boolean = it.isChecked
                if (checked){
                    println("checked")
                }else{
                    println("unChecked")
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

}