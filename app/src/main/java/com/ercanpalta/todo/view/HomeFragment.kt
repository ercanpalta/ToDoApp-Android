package com.ercanpalta.todo.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ercanpalta.todo.adapter.HomeAdapter
import com.ercanpalta.todo.adapter.ListAdapter
import com.ercanpalta.todo.databinding.FragmentHomeBinding
import com.ercanpalta.todo.model.TaskList
import com.ercanpalta.todo.model.ToDo
import com.ercanpalta.todo.viewmodel.HomeViewModel
import java.util.Collections.addAll

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var homeViewModel:HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listToDo:ArrayList<ToDo> = arrayListOf()
        homeViewModel.toDoList.observe(viewLifecycleOwner) {
            listToDo.addAll(it)
        }

        val listList:ArrayList<TaskList<ToDo>> = arrayListOf()
        homeViewModel.listList.observe(viewLifecycleOwner) {
            listList.addAll(it)
        }

        val homeAdapter = HomeAdapter(listToDo)
        binding.rvHome.adapter = homeAdapter
        val homeLayoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        binding.rvHome.layoutManager = homeLayoutManager
        binding.rvHome.addItemDecoration(DividerItemDecoration(context,homeLayoutManager.orientation))


        val listAdapter = ListAdapter(listList)
        binding.rvHomeList.adapter = listAdapter
        val listLayoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvHomeList.layoutManager = listLayoutManager

        homeViewModel.updateData()


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}