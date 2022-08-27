package com.ercanpalta.todo.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ercanpalta.todo.R
import com.ercanpalta.todo.database.ToDoDatabase
import com.ercanpalta.todo.databinding.FragmentAddListBinding
import com.ercanpalta.todo.databinding.FragmentHomeBinding
import com.ercanpalta.todo.model.TaskList
import com.ercanpalta.todo.viewmodel.HomeViewModel

class AddListFragment : Fragment() {

    private var _binding: FragmentAddListBinding? = null

    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentAddListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addListButton.setOnClickListener {
            val listName = binding.listNameText.text.toString()
            val listColorChipId = binding.colorChipsLayout.colorChipsGroup.checkedChipId
            val listColor = when(listColorChipId){
                R.id.chip_purple -> R.color.list_color_purple
                R.id.chip_blue -> R.color.list_color_blue
                R.id.chip_green -> R.color.list_color_green
                R.id.chip_yellow -> R.color.list_color_yellow
                R.id.chip_red -> R.color.list_color_red
                R.id.chip_brown -> R.color.list_color_brown
                R.id.chip_pink -> R.color.list_color_pink
                else -> {R.color.list_color_orange}
            }

            if (listName.isNotEmpty()){
                val newList = TaskList(listName,listColor)
                homeViewModel.addTaskList(newList)
                println(listName)
            }

            val action = AddListFragmentDirections.actionAddListFragmentToNavHome()
            findNavController().navigate(action)
        }

    }
}