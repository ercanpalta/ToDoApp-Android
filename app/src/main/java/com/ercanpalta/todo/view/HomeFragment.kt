package com.ercanpalta.todo.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ercanpalta.todo.R
import com.ercanpalta.todo.adapter.HomeAdapter
import com.ercanpalta.todo.adapter.ListAdapter
import com.ercanpalta.todo.databinding.FragmentHomeBinding
import com.ercanpalta.todo.enums.FilterType
import com.ercanpalta.todo.model.TaskList
import com.ercanpalta.todo.model.ToDo
import com.ercanpalta.todo.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var homeAdapter: HomeAdapter
    val listToDo:ArrayList<ToDo> = arrayListOf()

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


        homeViewModel.toDoList.observe(viewLifecycleOwner) {
            listToDo.clear()
            listToDo.addAll(it)
            if(listToDo.isEmpty()){
                showNoDataText()
            }else{
                hideNoDataText()
            }
            filterList(homeViewModel.currentListName, FilterType.List)
        }

        val listList:ArrayList<TaskList> = arrayListOf()
        val listAll = TaskList("All", R.color.list_color_all)
        val listNew = TaskList("New List", R.color.list_color_yellow)
        homeViewModel.listList.observe(viewLifecycleOwner) {
            listList.clear()
            listList.add(listAll)
            listList.addAll(it)
            listList.add(listNew)
            binding.rvHomeList.adapter?.notifyDataSetChanged()
        }

        homeAdapter = HomeAdapter(listToDo, this@HomeFragment)
        homeAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.rvHome.adapter = homeAdapter
        val homeLayoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        binding.rvHome.layoutManager = homeLayoutManager
        //binding.rvHome.addItemDecoration(DividerItemDecoration(context,homeLayoutManager.orientation))

        val listAdapter = ListAdapter(listList, this@HomeFragment)
        listAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.rvHomeList.adapter = listAdapter
        val listLayoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvHomeList.layoutManager = listLayoutManager

        homeViewModel.updateData()

        binding.fab.setOnClickListener {
            val action = HomeFragmentDirections.actionNavHomeToAddFragment()
            findNavController().navigate(action)
        }
    }

    fun showNoDataText(){
        binding.noData.noDataLayout.visibility = View.VISIBLE
    }

    fun hideNoDataText(){
        binding.noData.noDataLayout.visibility = View.INVISIBLE
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

    fun updateCompletion(uid:Int, isCompleted:Boolean){
        homeViewModel.updateCompletion(uid, isCompleted)
    }

    fun updateListToDo(task: ToDo, isCompleted: Boolean){
        for (todo in listToDo){
            if (todo == task){
                task.isCompleted = isCompleted
            }
        }
    }

    fun deleteTask(uid: Int, position:Int){
        val builder = AlertDialog.Builder(this.context, R.style.MyDialogTheme)
        builder.setTitle(R.string.delete)
        builder.setMessage(R.string.ask_delete)
        builder.setPositiveButton(R.string.delete) { dialog, i ->
                homeViewModel.deleteTask(uid)
                listToDo.removeAt(position)
                filterList(homeViewModel.currentListName, FilterType.List)
                homeAdapter.notifyItemRemoved(position)
            }
        builder.setNegativeButton(R.string.cancel){ dialog, i ->

            }
        builder.show()
    }

    fun moveToEditTask(uid: Int){
        val action = HomeFragmentDirections.actionNavHomeToEditFragment(uid)
        findNavController().navigate(action)
    }

    fun clearAllSelections(){
        val itemCount = binding.rvHome.adapter?.itemCount
        for(i in 0..itemCount!!){
            val holder = binding.rvHome.findViewHolderForAdapterPosition(i)
            if (holder != null) {
                val menu = holder.itemView.findViewById<View>(R.id.longclick_menu)
                menu.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}