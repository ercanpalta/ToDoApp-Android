package com.ercanpalta.todo.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.ercanpalta.todo.R
import com.ercanpalta.todo.databinding.FragmentEditListBinding
import com.ercanpalta.todo.model.TaskList
import com.ercanpalta.todo.viewmodel.HomeViewModel

class EditListFragment : Fragment() {

    private var _binding: FragmentEditListBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uid = arguments?.getInt("uid")

        homeViewModel.getTaskList(uid!!)

        lateinit var taskList:TaskList
        homeViewModel.taskList.observe(viewLifecycleOwner){
            taskList = it
            binding.listNameText.setText(it.name)
            val chipId = when(it.color){
                R.color.list_color_purple -> R.id.chip_purple
                R.color.list_color_blue -> R.id.chip_blue
                R.color.list_color_green -> R.id.chip_green
                R.color.list_color_yellow -> R.id.chip_yellow
                R.color.list_color_red -> R.id.chip_red
                R.color.list_color_brown -> R.id.chip_brown
                R.color.list_color_pink -> R.id.chip_pink
                else -> {R.id.chip_orange}
            }

            binding.colorChipsLayout.colorChipsGroup.check(chipId)
        }

        binding.listNameText.addTextChangedListener {
            if (it != null) {
                if (it.length > 20){
                    binding.listNameField.error = getString(R.string.text_limit_error)
                }else{
                    binding.listNameField.error = null
                }
            }
        }

        // to add appbar menu icon
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.delete_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.delete_list -> {
                        val builder = AlertDialog.Builder(requireContext(), R.style.MyDialogTheme)
                        builder.setTitle(R.string.delete_list)
                        builder.setMessage(R.string.delete_warning)
                        builder.setPositiveButton(R.string.delete) { _, _ ->
                            homeViewModel.deleteTaskList(uid)
                            homeViewModel.deleteAllTasksInsideList(taskList.name)
                            val action = EditListFragmentDirections.actionEditListFragmentToNavHome()
                            findNavController().navigate(action)
                        }
                        builder.setNegativeButton(R.string.cancel){ _, _ ->

                        }
                        builder.show()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding.saveButton.setOnClickListener {
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
                    taskList.name = listName
                    taskList.color = listColor
                    homeViewModel.updateTaskList(taskList)

                    val action = EditListFragmentDirections.actionEditListFragmentToNavHome()
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