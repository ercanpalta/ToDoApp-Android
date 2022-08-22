package com.ercanpalta.todo.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.ercanpalta.todo.R
import com.ercanpalta.todo.model.TaskList
import com.ercanpalta.todo.model.ToDo

class ListAdapter(private val dataSet: ArrayList<TaskList<ToDo>>): RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val textView: TextView
        val colorCard: CardView
        init {
            textView = view.findViewById(R.id.list_text)
            colorCard = view.findViewById(R.id.colorCard)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)

        return ListAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = dataSet[position].name
        holder.colorCard.setCardBackgroundColor(dataSet[position].color)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}