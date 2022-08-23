package com.ercanpalta.todo.adapter

import android.graphics.Color
import android.graphics.drawable.Icon
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.ercanpalta.todo.R
import com.ercanpalta.todo.enums.Priority
import com.ercanpalta.todo.model.ToDo

class HomeAdapter (private val dataSet: ArrayList<ToDo>): RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val textView: TextView
        var checkBox = CheckBox(view.context)
        var priorityText: TextView
        var priorityCard: CardView

        init {
            textView = view.findViewById(R.id.task_text)
            checkBox = view.findViewById(R.id.checkbox)
            priorityText = view.findViewById((R.id.priority_text))
            priorityCard = view.findViewById((R.id.priority_card))
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
            Priority.LOW ->{
                holder.priorityText.setText(R.string.low_priority)
                holder.priorityCard.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.low))
            }
            Priority.MEDIUM -> {
                holder.priorityText.setText(R.string.medium_priority)
                holder.priorityCard.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.medium))
            }
            Priority.HIGH ->{
                holder.priorityText.setText(R.string.high_priority)
                holder.priorityCard.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.high))
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

}