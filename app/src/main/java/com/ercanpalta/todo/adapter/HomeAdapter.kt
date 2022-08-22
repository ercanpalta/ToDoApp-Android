package com.ercanpalta.todo.adapter

import android.graphics.drawable.Icon
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.ercanpalta.todo.R
import com.ercanpalta.todo.enums.Priority
import com.ercanpalta.todo.model.ToDo

class HomeAdapter (private val dataSet: ArrayList<ToDo>): RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val textView: TextView
        var checkBox = CheckBox(view.context)
        var imageView1:ImageView = ImageView(view.context)
        var imageView2:ImageView = ImageView(view.context)
        var imageView3:ImageView = ImageView(view.context)

        init {
            textView = view.findViewById(R.id.task_text)
            checkBox = view.findViewById(R.id.checkbox)
            imageView1 = view.findViewById(R.id.imageView1)
            imageView2 = view.findViewById(R.id.imageView2)
            imageView3 = view.findViewById(R.id.imageView3)
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

        when(dataSet[position].priority){
            Priority.LOW -> {}
            Priority.MEDIUM ->
                holder.imageView2.visibility = View.VISIBLE
            Priority.HIGH -> {
                holder.imageView1.visibility = View.VISIBLE
                holder.imageView2.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

}