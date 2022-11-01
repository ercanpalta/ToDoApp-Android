package com.ercanpalta.todo.view

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.ercanpalta.todo.MainActivity
import com.ercanpalta.todo.R
import com.ercanpalta.todo.databinding.FragmentEditBinding
import com.ercanpalta.todo.enums.Priority
import com.ercanpalta.todo.enums.Repeat
import com.ercanpalta.todo.enums.TrackerType
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

class EditFragment : Fragment() {
    private var _binding:FragmentEditBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // custom backpress
        val callback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                if (binding.reminderFrame.visibility == View.VISIBLE){
                    binding.reminderFrame.visibility = View.GONE
                    isEnabled = false
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        // to add appbar menu icon
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.add_reminder -> {
                        if (binding.reminderChipContainer.isEmpty()){
                            callback.isEnabled = true
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

        addPriorityChips()

        // to get lists
        val listList:ArrayList<TaskList> = arrayListOf()
        homeViewModel.listList.value?.let { listList.addAll(it) }
        addListChips(listList)

        val uid = arguments?.getInt("uid")

        homeViewModel.getTask(uid!!)

        lateinit var toDo:ToDo
        val previousCalendar = Calendar.getInstance()
        val reminderCalendar = Calendar.getInstance()
        homeViewModel.toDo.observe(viewLifecycleOwner){
            toDo = it
            binding.editField.nameText.setText(it.task)
            binding.editField.descriptionText.setText(it.description)

            // to select one from tracker buttons
            when(it.tracker.trackerType){
                TrackerType.NON -> binding.trackerLayout.trackersToggleGroup.check(R.id.no_tracker_button)
                TrackerType.STREAK -> binding.trackerLayout.trackersToggleGroup.check(R.id.streak_tracker_button)
            }

            // to select priority chip
            val id:Int
            id = when(it.priority){
                Priority.LOW -> R.id.chip_low
                Priority.MEDIUM -> R.id.chip_medium
                Priority.HIGH -> R.id.chip_high
            }
            binding.priorityChipsGroup.check(id)

            // to select list chip
            val chipListIterator = binding.chipList.iterator()
            while(chipListIterator.hasNext()){
                val listId = chipListIterator.next().id
                if(binding.chipList.findViewById<Chip>(listId).text.toString() == it.listName){
                    binding.chipList.check(listId)
                }
            }

            // to add reminder chip if reminder set before
            if (toDo.requestCode != -1){

                // to show previous reminder when clicked to the reminder chip
                previousCalendar.timeInMillis = toDo.remindTimeInMillis
                reminderCalendar.timeInMillis = toDo.remindTimeInMillis
                binding.dateText.text = (activity as MainActivity).getFormattedDate(previousCalendar.timeInMillis).dropLast(6)
                var hour = previousCalendar.get(Calendar.HOUR_OF_DAY).toString()
                var minute = previousCalendar.get(Calendar.MINUTE).toString()
                if (previousCalendar.get(Calendar.HOUR_OF_DAY) < 10){
                    hour = "0$hour"
                }
                if (previousCalendar.get(Calendar.MINUTE) < 10){
                    minute = "0$minute"
                }
                binding.timeText.text = getString(R.string.time_format,hour,minute)
                binding.repeatSpinner.setSelection(getRepeatPosition(toDo.repeat))


                val chip = Chip(context)
                chip.apply {
                    this.id = View.generateViewId()
                    text = (activity as MainActivity).getFormattedDate(previousCalendar.timeInMillis)
                    textSize = 16f
                    isCloseIconVisible = true
                    setOnCloseIconClickListener {
                        val builder = alertDialog()
                        builder.setPositiveButton(R.string.delete) { _, _ ->
                            binding.reminderChipContainer.removeAllViews()
                            if(toDo.requestCode != -1){
                                cancelReminder(toDo.requestCode)
                                toDo.requestCode = -1
                            }
                        }
                        builder.show()

                    }
                    if(toDo.repeat != Repeat.NOT){
                        setChipIconResource(R.drawable.ic_repeat_16)
                    }else{
                        setChipIconResource(R.drawable.ic_alarm_16)
                    }
                    setOnClickListener {
                        callback.isEnabled = true
                        binding.reminderFrame.visibility = View.VISIBLE
                    }
                    setTextColor(ContextCompat.getColor(this.context,R.color.white))
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                    setChipBackgroundColorResource(android.R.color.darker_gray)
                }

                binding.reminderChipContainer.addView(chip)
            }else{
                //previousCalendar.timeInMillis = System.currentTimeMillis()
                binding.reminderChipContainer.removeAllViews()
                binding.dateText.text = getString(R.string.date)
                binding.timeText.text = getString(R.string.time)
                binding.repeatSpinner.setSelection(0)
            }

            // to pick date
            binding.dateText.setOnClickListener {

                val constraintsBuilder =
                    CalendarConstraints.Builder()
                        .setValidator(DateValidatorPointForward.now())

                val datePicker =
                    MaterialDatePicker.Builder.datePicker()
                        .setTheme(R.style.DatePickerTheme)
                        .setTitleText(this.getString(R.string.select_date))
                        .setSelection(reminderCalendar.timeInMillis)
                        .setCalendarConstraints(constraintsBuilder.build())
                        .build()
                datePicker.show(parentFragmentManager, "tag")

                datePicker.addOnPositiveButtonClickListener {
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = datePicker.selection!!
                    reminderCalendar[Calendar.YEAR] = calendar[Calendar.YEAR]
                    reminderCalendar[Calendar.MONTH] = calendar[Calendar.MONTH]
                    reminderCalendar[Calendar.DAY_OF_MONTH] = calendar[Calendar.DAY_OF_MONTH]
                    binding.dateText.text = (activity as MainActivity).getFormattedDate(reminderCalendar.timeInMillis).dropLast(6)
                }
                datePicker.addOnNegativeButtonClickListener {

                }
            }

            // to pick time
            binding.timeText.setOnClickListener {
                val picker =
                    MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(reminderCalendar.get(Calendar.HOUR_OF_DAY))
                        .setMinute(reminderCalendar.get(Calendar.MINUTE))
                        .setTitleText(this.getString(R.string.select_time))
                        .setTheme(R.style.TimePickerTheme)
                        .build()
                picker.show(parentFragmentManager, "tag")


                picker.addOnPositiveButtonClickListener {
                    var hour = picker.hour.toString()
                    var minute = picker.minute.toString()
                    if (picker.hour < 10){
                        hour = "0$hour"
                    }
                    if (picker.minute < 10){
                        minute = "0$minute"
                    }

                    val time = getString(R.string.time_format,hour,minute)
                    binding.timeText.text = time

                    reminderCalendar[Calendar.HOUR_OF_DAY] = picker.hour
                    reminderCalendar[Calendar.MINUTE] = picker.minute
                    reminderCalendar[Calendar.SECOND] = 0
                }
                picker.addOnNegativeButtonClickListener {

                }
            }

            binding.cancelReminder.setOnClickListener {
                callback.isEnabled = false
                reminderCalendar.timeInMillis = previousCalendar.timeInMillis
                binding.dateText.text = (activity as MainActivity).getFormattedDate(reminderCalendar.timeInMillis).dropLast(6)
                binding.timeText.text = (activity as MainActivity).getFormattedDate(reminderCalendar.timeInMillis).takeLast(5)
                binding.reminderFrame.visibility = View.GONE
            }

            binding.applyReminder.setOnClickListener {
                val dateText = binding.dateText.text.toString()
                val timeText = binding.timeText.text.toString()
                if(dateText != this.getString(R.string.date) && timeText != this.getString(R.string.time)){
                    callback.isEnabled = false
                    val chip = Chip(context)
                    chip.apply {
                        this.id = View.generateViewId()
                        text = (activity as MainActivity).getFormattedDate(reminderCalendar.timeInMillis)
                        textSize = 16f
                        isCloseIconVisible = true
                        setOnCloseIconClickListener {
                            val builder = alertDialog()
                            builder.setPositiveButton(R.string.delete) { _, _ ->
                                binding.reminderChipContainer.removeAllViews()
                            }
                            builder.show()
                        }
                        if(binding.repeatSpinner.selectedItem.toString() != resources.getStringArray(R.array.repeat)[0]){
                            setChipIconResource(R.drawable.ic_repeat_16)
                        }else{
                            setChipIconResource(R.drawable.ic_alarm_16)
                        }
                        setOnClickListener {
                            callback.isEnabled = true
                            binding.reminderFrame.visibility = View.VISIBLE
                        }
                        setTextColor(ContextCompat.getColor(this.context,R.color.white))
                        textAlignment = View.TEXT_ALIGNMENT_CENTER
                        setChipBackgroundColorResource(android.R.color.darker_gray)
                    }

                    binding.reminderChipContainer.addView(chip)

                    binding.reminderFrame.visibility = View.GONE
                }else{
                    Toast.makeText(requireContext(),R.string.please_enter_date,Toast.LENGTH_LONG).show()
                }

            }
        }

        binding.editField.nameText.addTextChangedListener {
            if (it != null) {
                if (it.length > 56){
                    binding.editField.nameField.error = getString(R.string.text_limit_error)
                }else{
                    binding.editField.nameField.error = null
                }
            }
        }

        // creates spinner to choose whether reminder will be repeated or not
        val adapter = ArrayAdapter.createFromResource(this.requireContext(),R.array.repeat,android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.repeatSpinner.adapter = adapter



        binding.saveButton.setOnClickListener {
            val name = binding.editField.nameText.text.toString()
            val description = binding.editField.descriptionText.text.toString()
            val priorityChipId = binding.priorityChipsGroup.checkedChipId
            val listChipId = binding.chipList.checkedChipId
            val listChip: String = if(listChipId != -1){
                binding.chipList.findViewById<Chip>(listChipId).text.toString()
            }else{
                "All"
            }


            var priority = Priority.LOW
            if(priorityChipId != -1){
                when(priorityChipId){
                    R.id.chip_low -> priority = Priority.LOW
                    R.id.chip_medium -> priority = Priority.MEDIUM
                    R.id.chip_high-> priority = Priority.HIGH
                }
            }

            toDo.apply {
                this.task = name
                this.description = description
                this.priority = priority
                this.listName = listChip
            }


            if (name.isNotEmpty() && description.isNotEmpty()){
                val prevReminder = previousCalendar.timeInMillis
                val nowReminder = reminderCalendar.timeInMillis
                if (name.length <= 56){
                    if(binding.reminderChipContainer.isNotEmpty()){ // if reminder set with add button
                        toDo.repeat = Repeat.values()[binding.repeatSpinner.selectedItemPosition]
                        if (toDo.requestCode == -1){
                            // to generate request code
                            val sharedPreferences = requireContext().getSharedPreferences("com.ercanpalta.todo",
                                Context.MODE_PRIVATE
                            )
                            var requestCode = sharedPreferences.getInt("requestNumber",0)

                            if(requestCode < 9999){
                                requestCode += 1
                            }else{
                                requestCode = 1
                            }
                            sharedPreferences.edit().putInt("requestNumber",requestCode).apply()
                            toDo.requestCode = requestCode

                            toDo.remindTimeInMillis = reminderCalendar.timeInMillis
                            (activity as MainActivity).setReminder(toDo)
                        }else if (prevReminder != nowReminder){
                            toDo.remindTimeInMillis = reminderCalendar.timeInMillis
                            (activity as MainActivity).cancelReminder(toDo.requestCode)
                            (activity as MainActivity).setReminder(toDo)
                        }
                    }

                    when(binding.trackerLayout.trackersToggleGroup.checkedButtonId){
                        R.id.streak_tracker_button ->  {
                            if(toDo.tracker.trackerType == TrackerType.NON){
                                toDo.tracker.trackerType = TrackerType.STREAK
                                toDo.tracker.trackerTimeInMillis = Calendar.getInstance().timeInMillis
                            }
                        }
                        R.id.no_tracker_button ->  {
                            if(toDo.tracker.trackerType == TrackerType.STREAK){
                                toDo.tracker.trackerType = TrackerType.NON
                                toDo.tracker.trackerTimeInMillis = 0
                                toDo.tracker.trackerCounter = 0
                            }
                        }
                    }

                    homeViewModel.updateTask(toDo)
                    val action = EditFragmentDirections.actionEditFragmentToNavHome()
                    findNavController().navigate(action)
                }else{
                    Toast.makeText(this.requireContext(),R.string.text_limit_error, Toast.LENGTH_LONG).show()
                }

            }else{
                Toast.makeText(this.requireContext(),R.string.please_enter_task, Toast.LENGTH_LONG).show()
            }
        }




    }

    private fun cancelReminder(requestCode:Int){
        (activity as MainActivity).cancelReminder(requestCode)
    }

    private fun alertDialog():AlertDialog.Builder{
        val builder = AlertDialog.Builder(this.context, R.style.MyDialogTheme)
        builder.setTitle(R.string.delete_reminder)
        builder.setMessage(R.string.ask_delete)

        builder.setNegativeButton(R.string.cancel){ _, _ ->

        }
        return builder
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
            setTextColor(ContextCompat.getColor(this.context, R.color.white))
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setChipBackgroundColorResource(R.color.low)
        }
        chipMedium.apply {
            id = R.id.chip_medium
            text = getString(R.string.medium_priority)
            textSize = 16f
            isCheckable = true
            setTextColor(ContextCompat.getColor(this.context, R.color.white))
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setChipBackgroundColorResource(R.color.medium)
        }
        chipHigh.apply {
            id = R.id.chip_high
            text = getString(R.string.high_priority)
            textSize = 16f
            isCheckable = true
            setTextColor(ContextCompat.getColor(this.context, R.color.white))
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setChipBackgroundColorResource(R.color.high)
        }

        chipGroup.addView(chipLow)
        chipGroup.addView(chipMedium)
        chipGroup.addView(chipHigh)

    }

    private fun getRepeatPosition(repeat: Repeat):Int{
        val position: Int = when (repeat) {
            Repeat.NOT -> 0
            Repeat.DAILY -> 1
            Repeat.WEEKLY -> 2
        }
        return position
    }
}