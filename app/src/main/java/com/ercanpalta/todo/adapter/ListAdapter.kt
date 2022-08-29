package com.ercanpalta.todo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ercanpalta.todo.R
import com.ercanpalta.todo.enums.FilterType
import com.ercanpalta.todo.model.TaskList
import com.ercanpalta.todo.view.HomeFragment
import java.util.*
import kotlin.collections.ArrayList

class ListAdapter(private val dataSet: ArrayList<TaskList>, val fragment: HomeFragment): RecyclerView.Adapter<ListAdapter.ViewHolder>() {

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

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = dataSet[position].name
        holder.colorCard.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, dataSet[position].color))

        holder.itemView.setOnClickListener {
            if(holder.textView.text != "New List"){
                if(holder.adapterPosition > 2){
                    Collections.swap(dataSet, holder.adapterPosition, 1)
                    notifyItemMoved(holder.adapterPosition, 1)
                    fragment.scrollToStart()
                }
                fragment.changeCurrentListName(holder.textView.text.toString())
                fragment.filterList(holder.textView.text.toString(), FilterType.List)
            }else if(holder.textView.text == "New List"){
                fragment.navigateToAddList()
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}