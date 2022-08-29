package com.ercanpalta.todo.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.ercanpalta.todo.MainActivity
import com.ercanpalta.todo.R
import com.ercanpalta.todo.adapter.HomeAdapter
import com.ercanpalta.todo.adapter.ListAdapter
import com.ercanpalta.todo.databinding.FragmentHomeBinding
import com.ercanpalta.todo.enums.FilterType
import com.ercanpalta.todo.model.TaskList
import com.ercanpalta.todo.model.ToDo
import com.ercanpalta.todo.util.CustomOnBackPressedCallback
import com.ercanpalta.todo.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var homeAdapter: HomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listToDo:ArrayList<ToDo> = arrayListOf()
        homeViewModel.toDoList.observe(viewLifecycleOwner) {
            listToDo.clear()
            listToDo.addAll(it)
            if(listToDo.isEmpty()){
                showNoDataText()
            }else{
                hideNoDataText()
            }
            filterList(homeViewModel.currentListName, FilterType.List)
            binding.slidingPaneLayout.closePane()
        }

        val listList:ArrayList<TaskList> = arrayListOf()
        val listAll = TaskList("All", R.color.list_color_all)
        val listNew = TaskList("New List", R.color.list_color_yellow)
        homeViewModel.listList.observe(viewLifecycleOwner) {
            listList.clear()
            listList.add(listAll)
            listList.addAll(it)
            listList.add(listNew)
            binding.mainPane.rvHomeList.adapter?.notifyDataSetChanged()
        }

        homeAdapter = HomeAdapter(listToDo, this@HomeFragment)
        binding.mainPane.rvHome.adapter = homeAdapter
        val homeLayoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        binding.mainPane.rvHome.layoutManager = homeLayoutManager
        binding.mainPane.rvHome.addItemDecoration(DividerItemDecoration(context,homeLayoutManager.orientation))


        val listAdapter = ListAdapter(listList, this@HomeFragment)
        binding.mainPane.rvHomeList.adapter = listAdapter
        val listLayoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        binding.mainPane.rvHomeList.layoutManager = listLayoutManager

        homeViewModel.updateData()

        binding.mainPane.fab.setOnClickListener {
            binding.slidingPaneLayout.openPane()
        }

        binding.slidingPaneLayout.lockMode = SlidingPaneLayout.LOCK_MODE_LOCKED
        // Connect the SlidingPaneLayout to the system back button.
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            CustomOnBackPressedCallback(binding.slidingPaneLayout)
        )
    }

    fun scrollToStart(){
        binding.mainPane.rvHomeList.scrollToPosition(1)
    }

    fun showNoDataText(){
        binding.mainPane.noData.noDataLayout.visibility = View.VISIBLE
    }

    fun hideNoDataText(){
        binding.mainPane.noData.noDataLayout.visibility = View.INVISIBLE
    }

    fun changeCurrentListName(listName:String){
        homeViewModel.currentListName = listName
    }

    fun filterList(filterText:String, filterType: FilterType){
        when(filterType){
            FilterType.List -> homeAdapter.filter.filter(filterText + "L")
            FilterType.Priority -> homeAdapter.filter.filter(filterText + "P")
            FilterType.Text -> homeAdapter.filter.filter(filterText + "T")
        }
    }

    fun navigateToAddList(){
        val action = HomeFragmentDirections.actionNavHomeToAddListFragment()
        findNavController().navigate(action)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}