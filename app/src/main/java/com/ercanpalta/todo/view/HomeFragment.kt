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
import com.ercanpalta.todo.databinding.FragmentHomeBinding
import com.ercanpalta.todo.model.ToDo
import com.ercanpalta.todo.viewmodel.HomeViewModel

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

        val adapter = HomeAdapter(listToDo)
        binding.rvHome.adapter = adapter
        val layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        binding.rvHome.layoutManager = LinearLayoutManager(context)
        binding.rvHome.addItemDecoration(DividerItemDecoration(context,layoutManager.orientation))

        homeViewModel.updateData()


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}