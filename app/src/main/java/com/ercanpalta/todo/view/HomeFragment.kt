package com.ercanpalta.todo.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ercanpalta.todo.MainActivity
import com.ercanpalta.todo.R
import com.ercanpalta.todo.adapter.HomeAdapter
import com.ercanpalta.todo.adapter.ListAdapter
import com.ercanpalta.todo.databinding.FragmentHomeBinding
import com.ercanpalta.todo.enums.FilterType
import com.ercanpalta.todo.model.TaskList
import com.ercanpalta.todo.model.ToDo
import com.ercanpalta.todo.viewmodel.HomeViewModel
import com.google.android.material.card.MaterialCardView
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var homeAdapter: HomeAdapter
    private val listToDo:ArrayList<ToDo> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
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
        binding.myTasksText.text = getString(R.string.my_tasks_format,this.getString(R.string.all).lowercase())

        // to set theme when app started
        val sharedPreferences = requireContext().getSharedPreferences("com.ercanpalta.todo",
            AppCompatActivity.MODE_PRIVATE
        )
        val isDarkModeOpen = sharedPreferences.getBoolean("isDarkModeOpen",false)
        if (isDarkModeOpen){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun onPause() {
        super.onPause()
        val sharedPreferences = requireContext().getSharedPreferences("com.ercanpalta.todo",
            AppCompatActivity.MODE_PRIVATE
        )
        sharedPreferences.edit().putBoolean("isAppOpen",false).apply()
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = requireContext().getSharedPreferences("com.ercanpalta.todo",
            AppCompatActivity.MODE_PRIVATE
        )
        sharedPreferences.edit().putBoolean("isAppOpen",true).apply()
    }

    fun showNoDataText(){
        binding.noData.noDataLayout.visibility = View.VISIBLE
    }

    fun hideNoDataText(){
        binding.noData.noDataLayout.visibility = View.INVISIBLE
    }

    fun changeCurrentListName(listName:String){
        var name = listName
        homeViewModel.currentListName = listName
        if (listName == "All"){
            name = context?.getString(R.string.all) ?: "All"
        }
        binding.myTasksText.text = getString(R.string.my_tasks_format,name.lowercase())
    }

    fun cancelReminder(requestCode:Int){
        (activity as MainActivity).cancelReminder(requestCode)
        homeViewModel.updateRequestCode(requestCode)
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

    fun updateTask(task: ToDo){
        homeViewModel.updateTask(task)
    }

    fun updateListToDo(task: ToDo, isCompleted: Boolean){
        for (todo in listToDo){
            if (todo == task){
                task.isCompleted = isCompleted
            }
        }
    }

    fun moveDown(position: Int, task:ToDo){
        homeAdapter.filteredList.remove(task)
        homeAdapter.filteredList.add(task)
        homeAdapter.notifyItemMoved(position,homeAdapter.itemCount-1)
    }

    fun moveUp(position: Int, task:ToDo){
        homeAdapter.filteredList.remove(task)
        homeAdapter.filteredList.add(0,task)
        homeAdapter.notifyItemMoved(position,0)
    }

    fun deleteTask(task: ToDo, position:Int){
        val builder = AlertDialog.Builder(this.context, R.style.MyDialogTheme)
        builder.setTitle(R.string.delete)
        builder.setMessage(R.string.ask_delete)
        builder.setPositiveButton(R.string.delete) { _, _ ->
                homeViewModel.deleteTask(task.uid)
                listToDo.remove(task)
                filterList(homeViewModel.currentListName, FilterType.List)
                homeAdapter.notifyItemRemoved(position)
            }
        builder.setNegativeButton(R.string.cancel){ _, _ ->

            }
        builder.show()
    }


    fun moveToEditTask(uid: Int){
        val action = HomeFragmentDirections.actionNavHomeToEditFragment(uid)
        findNavController().navigate(action)
    }

    fun moveToEditTaskList(uid: Int){
        val action = HomeFragmentDirections.actionNavHomeToEditListFragment(uid)
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

    fun clearAllListSelections(){
        val itemCount = binding.rvHomeList.adapter?.itemCount
        for(i in 0..itemCount!!){
            val holder = binding.rvHomeList.findViewHolderForAdapterPosition(i)
            if (holder != null) {
                val menu = holder.itemView.findViewById<View>(R.id.longclick_menu)
                menu.visibility = View.GONE
                val cardView = holder.itemView.findViewById<MaterialCardView>(R.id.colorCard)
                cardView.strokeWidth = 6
            }
        }
    }

    /*
    * Return 1 then streak can increase by one
    * Return 0 then streak increased today show toast
    * Return -1 then streak cannot continue, reset counter
    * */
    fun CanStreakCont(trackedDayMillis:Long, counter:Int):Int{
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_MONTH)
        val thisMonth = calendar.get(Calendar.MONTH)
        calendar.timeInMillis = trackedDayMillis
        val trackerDay = calendar.get(Calendar.DAY_OF_MONTH)
        val trackerMonth = calendar.get(Calendar.MONTH)
        var value:Int = 0

        if (counter == 0){
            value = 1
        }else if (thisMonth == trackerMonth){
            if (today == trackerDay){
                value = 0
            }
            else if (today == trackerDay + 1){
                value = 1
            }else{
                value = -1
            }
        }
        return  value
    }

    fun getFormattedDate(millis:Long):String{
        return (activity as MainActivity).getFormattedDate(millis)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}