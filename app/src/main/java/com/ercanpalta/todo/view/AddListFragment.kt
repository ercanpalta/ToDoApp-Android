package com.ercanpalta.todo.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ercanpalta.todo.R
import com.ercanpalta.todo.databinding.FragmentAddListBinding
import com.ercanpalta.todo.model.TaskList
import com.ercanpalta.todo.viewmodel.HomeViewModel

class AddListFragment : Fragment() {

    private var _binding: FragmentAddListBinding? = null

    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addListButton.setOnClickListener {
            val listName = binding.listNameText.text.toString()
            val listColor = when(binding.colorChipsLayout.colorChipsGroup.checkedChipId){
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
                if (listName.length <= 20){
                    val newList = TaskList(listName,listColor)
                    homeViewModel.addTaskList(newList)

                    val action = AddListFragmentDirections.actionAddListFragmentToNavHome()
                    findNavController().navigate(action)
                }else{
                    Toast.makeText(this.requireContext(),R.string.text_limit_error, Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this.requireContext(),R.string.please_enter_list, Toast.LENGTH_LONG).show()
            }
        }

    }
}