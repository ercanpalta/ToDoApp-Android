package com.ercanpalta.todo.adapter

import android.content.Context
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
import com.google.android.material.chip.Chip
import java.util.Calendar
import java.util.GregorianCalendar

class HomeAdapter (private val dataSet: ArrayList<ToDo>, val fragment: HomeFragment): RecyclerView.Adapter<HomeAdapter.ViewHolder>(), Filterable {

    private lateinit var context: Context
    val filteredList = ArrayList<ToDo>()
    val completedList = ArrayList<ToDo>()

    init {
        filteredList.addAll(dataSet)
    }

    class ViewHolder(private var binding:RowItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(task:ToDo, fragment:HomeFragment, context: Context){
            binding.taskText.text = task.task
            binding.detailText.text = task.description

            if (task.isCompleted){
                binding.checkbox.isChecked = true
                binding.taskText.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                binding.checkbox.alpha = 0.7f
            }else{
                binding.checkbox.isChecked = false
                binding.taskText.paintFlags = Paint.LINEAR_TEXT_FLAG
            }

            if (task.requestCode != -1){
                binding.reminderIcon.visibility = View.VISIBLE

                // to add reminder time info to the row item
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = task.remindTimeInMillis

                val reminderText = TextView(context)
                reminderText.textSize = 12f
                reminderText.setTextColor(context.getColor(android.R.color.darker_gray))
                reminderText.text = calendar.time.toString().dropLast(18)
                binding.reminderChipContainer.addView(reminderText)
            }

            binding.checkbox.setOnClickListener {
                if(it is CheckBox ){
                    val checked: Boolean = it.isChecked
                    if (checked){
                        fragment.updateListToDo(task,checked)
                        binding.taskText.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                        binding.checkbox.alpha = 0.7f
                        fragment.updateCompletion(task.uid,true)
                        fragment.moveDown(this.absoluteAdapterPosition,task)
                        println("checked")
                    }else{
                        fragment.updateListToDo(task,checked)
                        binding.taskText.paintFlags = Paint.LINEAR_TEXT_FLAG
                        fragment.updateCompletion(task.uid,false)
                        fragment.moveUp(this.absoluteAdapterPosition,task)
                        println("unChecked")
                    }

                }
            }

            binding.root.setOnClickListener {
                val detail = binding.detailText
                val menu = binding.longclickMenu
                val chipContainer = binding.reminderChipContainer

                if(menu.visibility == View.GONE){
                    fragment.clearAllSelections()
                    if (detail.visibility == View.GONE){
                        detail.visibility = View.VISIBLE
                        chipContainer.visibility = View.VISIBLE
                    }else{
                        detail.visibility = View.GONE
                        chipContainer.visibility = View.GONE
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
                fragment.deleteTask(task, this.absoluteAdapterPosition)
                fragment.clearAllSelections()
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
        holder.bind(task,fragment,context)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val filterTextWithType = p0.toString()
                filteredList.clear()
                completedList.clear()

                if (filterTextWithType.isEmpty()){
                    filteredList.addAll(dataSet)
                }else{
                    val filterType = filterTextWithType.get(filterTextWithType.lastIndex)
                    val filterText = filterTextWithType.dropLast(1)
                    if (filterType == 'L'){
                        if(filterText == "All"){
                            for(data in dataSet){
                                if (data.isCompleted){
                                    completedList.add(data)
                                }else{
                                    filteredList.add(data)
                                }
                            }
                            filteredList.addAll(completedList)
                        }else{
                            for (data in dataSet){
                                if(data.listName == filterText){
                                    if (data.isCompleted){
                                        completedList.add(data)
                                    }else{
                                        filteredList.add(data)
                                    }
                                }
                            }
                            filteredList.addAll(completedList)
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