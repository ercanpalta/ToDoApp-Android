package com.ercanpalta.todo.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.ercanpalta.todo.R
import com.ercanpalta.todo.databinding.FragmentAddBinding
import com.ercanpalta.todo.enums.Priority
import com.ercanpalta.todo.model.TaskList
import com.ercanpalta.todo.model.ToDo
import com.ercanpalta.todo.viewmodel.HomeViewModel
import com.google.android.material.chip.Chip

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null

    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listList:ArrayList<TaskList> = arrayListOf()
        homeViewModel.listList.observe(viewLifecycleOwner) {
            listList.clear()
            listList.addAll(it)
            addListChips(listList)
        }

        addPriorityChips()

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
                homeViewModel.addTask(task)
                clearInputs()
            }else{
                Toast.makeText(this.requireContext(),R.string.please_enter_task,Toast.LENGTH_LONG).show()
            }


        }

    }

    fun clearInputs(){
        binding.editField.nameText.text?.clear()
        binding.editField.descriptionText.text?.clear()
        binding.priorityChipsGroup.clearCheck()
        binding.chipList.clearCheck()
    }


    fun addListChips(listList:ArrayList<TaskList>){
        val chipGroup = binding.chipList
        chipGroup.removeAllViews()
        for (list in listList){
            println(listList.size)
            if (list.name != "All" && list.name != "New List"){
                val chip = Chip(context)
                chip.apply {
                    id = View.generateViewId()
                    text = list.name
                    setTextSize(16f)
                    isCheckable = true
                    setTextColor(ContextCompat.getColor(this.context,R.color.white))
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                    setChipBackgroundColorResource(list.color)
                }
                chipGroup.addView(chip)
            }

        }
    }

    fun addPriorityChips(){
        val chipGroup = binding.priorityChipsGroup
        chipGroup.removeAllViews()


        val chipLow = Chip(context)
        val chipMedium= Chip(context)
        val chipHigh = Chip(context)
        chipLow.apply {
            id = R.id.chip_low
            text = getString(R.string.low_priority)
            setTextSize(16f)
            isCheckable = true
            setTextColor(ContextCompat.getColor(this.context,R.color.white))
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setChipBackgroundColorResource(R.color.low)
        }
        chipMedium.apply {
            id = R.id.chip_medium
            text = getString(R.string.medium_priority)
            setTextSize(16f)
            isCheckable = true
            setTextColor(ContextCompat.getColor(this.context,R.color.white))
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setChipBackgroundColorResource(R.color.medium)
        }
        chipHigh.apply {
            id = R.id.chip_high
            text = getString(R.string.high_priority)
            setTextSize(16f)
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