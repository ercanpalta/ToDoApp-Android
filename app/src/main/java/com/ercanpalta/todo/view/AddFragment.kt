package com.ercanpalta.todo.view

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.ercanpalta.todo.MainActivity
import com.ercanpalta.todo.R
import com.ercanpalta.todo.databinding.FragmentAddBinding
import com.ercanpalta.todo.enums.Priority
import com.ercanpalta.todo.model.TaskList
import com.ercanpalta.todo.model.ToDo
import com.ercanpalta.todo.viewmodel.HomeViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*
import kotlin.collections.ArrayList

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null

    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // to observe tasklist source
        val listList:ArrayList<TaskList> = arrayListOf()
        homeViewModel.listList.observe(viewLifecycleOwner) {
            listList.clear()
            listList.addAll(it)
            addListChips(listList)

            val chipListIterator = binding.chipList.iterator()
            while(chipListIterator.hasNext()){
                val listId = chipListIterator.next().id
                if(binding.chipList.findViewById<Chip>(listId).text.toString() == homeViewModel.currentListName){
                    binding.chipList.check(listId)
                }
            }
        }

        // checks if text longer than 56 character
        binding.editField.nameText.addTextChangedListener {
            if (it != null) {
                if (it.length > 56){
                    binding.editField.nameField.error = getString(R.string.text_limit_error)
                }else{
                    binding.editField.nameField.error = null
                }
            }
        }

        addPriorityChips()

        // to add appbar menu icon
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.add_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.add_reminder -> {
                        if (binding.reminderChipContainer.isEmpty()){
                            binding.reminderFrame.visibility = View.VISIBLE
                        }else{
                            Toast.makeText(requireContext(),R.string.already_have_reminder,Toast.LENGTH_LONG).show()
                        }
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // creates spinner to choose whether reminder will be repeated or not
        val adapter = ArrayAdapter.createFromResource(this.requireContext(),R.array.repeat,android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.repeatSpinner.adapter = adapter

        // to pick date
        val reminderCalendar = Calendar.getInstance()
        var date = "date"
        binding.dateText.setOnClickListener {
            val calendar = Calendar.getInstance()

            val constraintsBuilder =
                CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointForward.now())

            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTheme(R.style.DatePickerTheme)
                    .setTitleText("Select date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setCalendarConstraints(constraintsBuilder.build())
                    .build()
            datePicker.show(parentFragmentManager, "tag")

            datePicker.addOnPositiveButtonClickListener {
                calendar.timeInMillis = datePicker.selection!!
                reminderCalendar.timeInMillis = datePicker.selection!!
                val monthList = arrayOf("Jan.","Feb.","Mar.", "Apr.", "May", "Jun.", "Jul.",
                    "Aug.", "Sep.", "Oct.", "Nov.",
                    "Dec.")
                val day = calendar.get(Calendar.DAY_OF_MONTH).toString()
                val month = monthList[calendar.get(Calendar.MONTH)]
                val year = calendar.get(Calendar.YEAR).toString()

                date = getString(R.string.date_format,day,month,year)
                binding.dateText.text = date
            }
            datePicker.addOnNegativeButtonClickListener {
                println("negative")
            }
        }

        // to pick time
        var time = "time"
        binding.timeText.setOnClickListener {
            val picker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(12)
                    .setTitleText("Select time")
                    .setTheme(R.style.TimePickerTheme)
                    .build()
            picker.show(parentFragmentManager, "tag")


            picker.addOnPositiveButtonClickListener {
                var hour = picker.hour.toString()
                var minute = picker.minute.toString()
                if (picker.hour < 10){
                    hour = "0" + hour
                }
                if (picker.minute < 10){
                    minute = "0" + minute
                }

                time = getString(R.string.time_format,hour,minute)
                binding.timeText.text = time

                reminderCalendar[Calendar.HOUR] = picker.hour
                reminderCalendar[Calendar.MINUTE] = picker.minute
            }
            picker.addOnNegativeButtonClickListener {
                println("negative")
            }
        }

        binding.cancelReminder.setOnClickListener {
            binding.reminderFrame.visibility = View.GONE
        }

        binding.applyReminder.setOnClickListener {
            val chip = Chip(context)
            chip.apply {
                id = View.generateViewId()
                text = getString(R.string.date_time_format,date,time)
                textSize = 16f
                isCloseIconVisible = true
                setOnCloseIconClickListener {
                    binding.reminderChipContainer.removeAllViews()
                }
                setChipIconResource(R.drawable.ic_alarm_24)
                setTextColor(ContextCompat.getColor(this.context,R.color.white))
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                setChipBackgroundColorResource(android.R.color.darker_gray)
            }

            binding.reminderChipContainer.addView(chip)
            println(reminderCalendar.time)

            binding.reminderFrame.visibility = View.GONE
        }

        binding.addButton.setOnClickListener {
            val name = binding.editField.nameText.text.toString()
            val description = binding.editField.descriptionText.text.toString()
            val priorityChipId = binding.priorityChipsGroup.checkedChipId
            val listChipId = binding.chipList.checkedChipId
            var listChip = homeViewModel.currentListName
            if(listChipId != -1){
                listChip = binding.chipList.findViewById<Chip>(listChipId).text.toString()
            }


            var priority = Priority.LOW
            if(priorityChipId != -1){
                when(priorityChipId){
                    R.id.chip_low -> priority = Priority.LOW
                    R.id.chip_medium -> priority = Priority.MEDIUM
                    R.id.chip_high-> priority = Priority.HIGH
                }
            }


            val task = ToDo(name,priority,false,listChip)
            task.description = description

            if (name.isNotEmpty() && description.isNotEmpty()){
                if (name.length <= 56){
                    binding.editField.nameField.error = null
                    homeViewModel.addTask(task)
                    if(binding.reminderChipContainer.isNotEmpty()){
                        (activity as MainActivity).setReminder(reminderCalendar.timeInMillis)
                    }
                    val action = AddFragmentDirections.actionAddFragmentToNavHome()
                    findNavController().navigate(action)
                }else{
                    Toast.makeText(this.requireContext(),R.string.text_limit_error, Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this.requireContext(),R.string.please_enter_task,Toast.LENGTH_LONG).show()
            }


        }

    }

    private fun addListChips(listList:ArrayList<TaskList>){
        val chipGroup = binding.chipList
        chipGroup.removeAllViews()
        for (list in listList){
            println(listList.size)
            if (list.name != "All" && list.name != "New List"){
                val chip = Chip(context)
                chip.apply {
                    id = View.generateViewId()
                    text = list.name
                    textSize = 16f
                    isCheckable = true
                    setTextColor(ContextCompat.getColor(this.context,R.color.white))
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                    setChipBackgroundColorResource(list.color)
                }
                chipGroup.addView(chip)
            }

        }
    }

    private fun addPriorityChips(){
        val chipGroup = binding.priorityChipsGroup
        chipGroup.removeAllViews()


        val chipLow = Chip(context)
        val chipMedium= Chip(context)
        val chipHigh = Chip(context)
        chipLow.apply {
            id = R.id.chip_low
            text = getString(R.string.low_priority)
            textSize = 16f
            isCheckable = true
            setTextColor(ContextCompat.getColor(this.context,R.color.white))
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setChipBackgroundColorResource(R.color.low)
        }
        chipMedium.apply {
            id = R.id.chip_medium
            text = getString(R.string.medium_priority)
            textSize = 16f
            isCheckable = true
            setTextColor(ContextCompat.getColor(this.context,R.color.white))
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setChipBackgroundColorResource(R.color.medium)
        }
        chipHigh.apply {
            id = R.id.chip_high
            text = getString(R.string.high_priority)
            textSize = 16f
            isCheckable = true
            setTextColor(ContextCompat.getColor(this.context,R.color.white))
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setChipBackgroundColorResource(R.color.high)
        }

        chipGroup.addView(chipLow)
        chipGroup.addView(chipMedium)
        chipGroup.addView(chipHigh)

    }

}