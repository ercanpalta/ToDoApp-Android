package com.ercanpalta.todo.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ercanpalta.todo.R
import com.ercanpalta.todo.databinding.RowItemBinding
import com.ercanpalta.todo.enums.Priority
import com.ercanpalta.todo.model.ToDo
import com.ercanpalta.todo.view.HomeFragment

class HomeAdapter (private val dataSet: ArrayList<ToDo>, val fragment: HomeFragment): RecyclerView.Adapter<HomeAdapter.ViewHolder>(), Filterable {

    private lateinit var context: Context
    val filteredList = ArrayList<ToDo>()

    init {
        filteredList.addAll(dataSet)
    }

    class ViewHolder(private var binding:RowItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(task:ToDo , position: Int, fragment:HomeFragment, context: Context){
            binding.taskText.text = task.task
            binding.detailText.text = task.description

            if (task.isCompleted){
                binding.checkbox.isChecked = true
                binding.taskText.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG)
                binding.checkbox.alpha = 0.7f
            }else{
                binding.checkbox.isChecked = false
                binding.taskText.setPaintFlags(Paint.LINEAR_TEXT_FLAG)
            }

            binding.checkbox.setOnClickListener {
                val menu = binding.longclickMenu
                if(it is CheckBox ){
                    val checked: Boolean = it.isChecked
                    if (checked){
                        fragment.updateListToDo(task,checked)
                        binding.taskText.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG)
                        binding.checkbox.alpha = 0.7f
                        fragment.updateCompletion(task.uid,true)
                        println("checked")
                    }else{
                        fragment.updateListToDo(task,checked)
                        binding.taskText.setPaintFlags(Paint.LINEAR_TEXT_FLAG)
                        fragment.updateCompletion(task.uid,false)
                        println("unChecked")
                    }

                }
            }

            binding.root.setOnClickListener {
                val detail = binding.detailText
                val menu = binding.longclickMenu
                if(menu.visibility == View.GONE){
                    fragment.clearAllSelections()
                    if (detail.visibility == View.GONE){
                        detail.visibility = View.VISIBLE
                    }else{
                        detail.visibility = View.GONE
                    }
                }
                fragment.clearAllListSelections()
            }

            binding.root.setOnLongClickListener {
                fragment.clearAllSelections()
                val detail = binding.detailText
                val menu = binding.longclickMenu
                if (menu.visibility == View.GONE){
                    detail.visibility = View.GONE
                    menu.visibility = View.VISIBLE
                }
                return@setOnLongClickListener true
            }

            binding.closeButton.setOnClickListener {
                binding.longclickMenu.visibility = View.GONE
            }

            binding.deleteButton.setOnClickListener {
                fragment.deleteTask(task.uid, position)
            }

            binding.editButton.setOnClickListener {
                fragment.moveToEditTask(task.uid)
            }

            when(task.priority){
                Priority.LOW ->{
                    binding.priorityText.setText(R.string.low_priority)
                    binding.priorityCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.low))
                }
                Priority.MEDIUM -> {
                    binding.priorityText.setText(R.string.medium_priority)
                    binding.priorityCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.medium))
                }
                Priority.HIGH ->{
                    binding.priorityText.setText(R.string.high_priority)
                    binding.priorityCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.high))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        context = parent.context
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = filteredList[position]
        val adapterPosition = holder.absoluteAdapterPosition
        holder.bind(task,adapterPosition,fragment,context)
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