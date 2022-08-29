package com.ercanpalta.todo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ercanpalta.todo.R
import com.ercanpalta.todo.enums.Priority
import com.ercanpalta.todo.model.ToDo
import com.ercanpalta.todo.view.HomeFragment

class HomeAdapter (private val dataSet: ArrayList<ToDo>, val fragment: HomeFragment): RecyclerView.Adapter<HomeAdapter.ViewHolder>(), Filterable {

    val filteredList = ArrayList<ToDo>()

    init {
        filteredList.addAll(dataSet)
    }

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
        holder.textView.text = filteredList[position].task
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

        when(filteredList[position].priority){
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
        return filteredList.size
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val filterTextWithType = p0.toString()
                filteredList.clear()

                if (filterTextWithType.isEmpty()){
                    filteredList.addAll(dataSet)
                }else{
                    val filterType = filterTextWithType.get(filterTextWithType.lastIndex)
                    val filterText = filterTextWithType.dropLast(1)
                    if (filterType == 'L'){
                        if(filterText == "All"){
                            filteredList.addAll(dataSet)
                        }else{
                            for (data in dataSet){
                                if(data.listName == filterText){
                                    filteredList.add(data)
                                }
                            }
                        }

                    }

                }

                if(filteredList.isEmpty()){
                    fragment.showNoDataText()
                }else{
                    fragment.hideNoDataText()
                }

                val filterResult = FilterResults()
                filterResult.values = filteredList
                return  filterResult
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                notifyDataSetChanged()
            }

        }
    }

}