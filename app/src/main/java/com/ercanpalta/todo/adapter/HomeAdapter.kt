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
import com.ercanpalta.todo.enums.Repeat
import com.ercanpalta.todo.enums.TrackerType
import com.ercanpalta.todo.model.ToDo
import com.ercanpalta.todo.view.HomeFragment
import java.util.*

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
                binding.progressText.setText(R.string.done)
            }else{
                binding.checkbox.isChecked = false
                binding.taskText.paintFlags = Paint.LINEAR_TEXT_FLAG
                binding.progressText.setText(R.string.progress)
            }

            //to set activity icon colors
            fun setActivityIconColors(counter:Int){
                if (counter < 7){
                    binding.activityIcon.setImageResource(R.drawable.heat_1_not_completed)
                }
                else if (counter < 14){
                    binding.activityIcon.setImageResource(R.drawable.heat_2_not_completed)
                }else{
                    binding.activityIcon.setImageResource(R.drawable.heat_3_not_completed)
                }
            }

            //to set tracker colors
            fun setTrackerColors(counter:Int){
                if (counter == 0){
                    binding.trackerIcon.setImageResource(R.drawable.heat_0)
                    binding.trackerCounterText.setTextColor(ContextCompat.getColor(context,R.color.heat0))
                    binding.trackerMaxText.setTextColor(ContextCompat.getColor(context,R.color.heat0))
                }else if (counter < 7){
                    binding.trackerIcon.setImageResource(R.drawable.heat_1)
                    binding.trackerCounterText.setTextColor(ContextCompat.getColor(context,R.color.heat1))
                    binding.trackerMaxText.setTextColor(ContextCompat.getColor(context,R.color.heat1))
                }
                else if (counter < 14){
                    binding.trackerIcon.setImageResource(R.drawable.heat_2)
                    binding.trackerCounterText.setTextColor(ContextCompat.getColor(context,R.color.heat2))
                    binding.trackerMaxText.setTextColor(ContextCompat.getColor(context,R.color.heat2))
                }else{
                    binding.trackerIcon.setImageResource(R.drawable.heat_3)
                    binding.trackerCounterText.setTextColor(ContextCompat.getColor(context,R.color.heat3))
                    binding.trackerMaxText.setTextColor(ContextCompat.getColor(context,R.color.heat3))
                }
            }




            // to show tracker icons
            if(task.tracker.trackerType == TrackerType.STREAK){
                // to check if streak continue, then if not counter will be zero
                val canStreakCont = fragment.canStreakCont(task.tracker.trackerTimeInMillis, task.tracker.trackerCounter)
                if (canStreakCont == -1){
                    task.tracker.trackerCounter = 0
                    binding.activityIcon.visibility = View.GONE
                    fragment.updateTracker(task)
                }else if(canStreakCont == 0){
                    binding.activityIcon.visibility = View.GONE
                } else if(canStreakCont == 1 && task.tracker.trackerCounter != 0){
                    setActivityIconColors(task.tracker.trackerCounter)
                    binding.activityIcon.visibility = View.VISIBLE
                }

                binding.trackerLayout.visibility = View.VISIBLE
                val counter = task.tracker.trackerCounter
                val max = task.tracker.trackerMax
                binding.trackerCounterText.text = counter.toString()
                binding.trackerMaxText.text = context.getString(R.string.tracker_max_format, max)
                setTrackerColors(counter)
            }else{
                binding.trackerLayout.visibility = View.GONE
                binding.activityIcon.visibility = View.GONE
            }

            if (task.requestCode != -1){
                binding.reminderIcon.visibility = View.VISIBLE

                if (task.repeat != Repeat.NOT){
                    binding.repeatIcon.visibility = View.VISIBLE
                }else{
                    binding.repeatIcon.visibility = View.GONE
                }

                // to add reminder time info to the row item

                val reminderText = TextView(context)
                reminderText.textSize = 12f
                reminderText.setTextColor(context.getColor(android.R.color.darker_gray))
                reminderText.text = fragment.getFormattedDate(task.remindTimeInMillis)
                binding.reminderDateContainer.removeAllViews()
                binding.reminderDateContainer.addView(reminderText)
            }
            else{
                binding.reminderIcon.visibility = View.GONE
                binding.repeatIcon.visibility = View.GONE
                binding.reminderDateContainer.removeAllViews()
            }

            binding.checkbox.setOnClickListener {
                if(it is CheckBox ){
                    val checked: Boolean = it.isChecked
                    if (checked){
                        // to cancel reminder and hide icons about reminder
                        fragment.cancelReminder(task.requestCode)
                        binding.reminderIcon.visibility = View.GONE
                        binding.repeatIcon.visibility = View.GONE
                        binding.reminderDateContainer.removeAllViews()
                        task.requestCode = -1

                        // to update completion from ui and database
                        fragment.updateListToDo(task,true)
                        binding.taskText.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                        binding.checkbox.alpha = 0.7f
                        binding.progressText.setText(R.string.done)
                        fragment.updateCompletion(task.uid,true)
                        fragment.moveDown(this.absoluteAdapterPosition,task)
                    }else{
                        fragment.updateListToDo(task,false)
                        binding.taskText.paintFlags = Paint.LINEAR_TEXT_FLAG
                        binding.progressText.setText(R.string.progress)
                        fragment.updateCompletion(task.uid,false)
                        fragment.moveUp(this.absoluteAdapterPosition,task)
                    }

                }
            }

            binding.root.setOnClickListener {
                val detail = binding.detailText
                val menu = binding.longclickMenu
                val dateContainer = binding.reminderDateContainer
                val addFireIcon = binding.addFireIcon
                val trackerCounter= binding.trackerCounterText
                val trackerMax = binding.trackerMaxText

                if(menu.visibility == View.GONE){
                    fragment.clearAllSelections()
                    fragment.clearAllListSelections()
                    if (detail.visibility == View.GONE){
                        detail.visibility = View.VISIBLE
                        dateContainer.visibility = View.VISIBLE
                        addFireIcon.visibility = View.VISIBLE
                        trackerCounter.visibility = View.VISIBLE
                        trackerMax.visibility = View.VISIBLE

                    }else{
                        detail.visibility = View.GONE
                        dateContainer.visibility = View.GONE
                        addFireIcon.visibility = View.GONE
                        trackerCounter.visibility = View.GONE
                        trackerMax.visibility = View.GONE
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

            binding.addFireIcon.setOnClickListener {
                val canStreakContinue = fragment.canStreakCont(task.tracker.trackerTimeInMillis, task.tracker.trackerCounter)
                if (canStreakContinue == 1){
                    task.tracker.trackerCounter += 1
                    binding.activityIcon.visibility = View.GONE
                    if (task.tracker.trackerCounter > task.tracker.trackerMax){
                        task.tracker.trackerMax = task.tracker.trackerCounter
                        binding.trackerMaxText.text = context.getString(R.string.tracker_max_format, task.tracker.trackerMax)
                    }
                    task.tracker.trackerTimeInMillis = Calendar.getInstance().timeInMillis
                    binding.trackerCounterText.text = task.tracker.trackerCounter.toString()
                    setTrackerColors(task.tracker.trackerCounter)
                    fragment.updateTracker(task)
                }else{
                    Toast.makeText(context, R.string.tracked_before, Toast.LENGTH_LONG).show()
                }
            }

            binding.closeButton.setOnClickListener {
                binding.longclickMenu.visibility = View.GONE
            }

            binding.deleteButton.setOnClickListener {
                fragment.cancelReminder(task.requestCode)
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
                            filteredList.reverse()
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
                            filteredList.reverse()
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