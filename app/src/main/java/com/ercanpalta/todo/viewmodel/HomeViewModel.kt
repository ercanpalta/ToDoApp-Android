package com.ercanpalta.todo.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ercanpalta.todo.R
import com.ercanpalta.todo.enums.Priority
import com.ercanpalta.todo.model.TaskList
import com.ercanpalta.todo.model.ToDo

class HomeViewModel : ViewModel() {

    private val _toDoList = MutableLiveData<List<ToDo>>()
    val toDoList:LiveData<List<ToDo>> = _toDoList

    private val _listList = MutableLiveData<List<TaskList<ToDo>>>()
    val listList:LiveData<List<TaskList<ToDo>>> = _listList



    fun updateData(){
        val todo1 = ToDo("Todo1", Priority.HIGH)
        val todo2 = ToDo("Todo2", Priority.MEDIUM)
        val todo3 = ToDo("Todo3", Priority.LOW)
        val todo4 = ToDo("Todo4")

        val list1 = TaskList<ToDo>("All", R.color.list_color_all)
        val list2 = TaskList<ToDo>("School", R.color.list_color_blue)
        val list3 = TaskList<ToDo>("Work", R.color.list_color_pink)
        val list4 = TaskList<ToDo>("Business", R.color.list_color_orange)
        val list5 = TaskList<ToDo>("Business", R.color.list_color_purple)
        val list6 = TaskList<ToDo>("New List", R.color.list_color_yellow)

        val list_list = listOf<TaskList<ToDo>>(list1,list2,list3,list4,list5,list6)
        _listList.value = list_list

        val list_todo = listOf<ToDo>(todo1,todo2,todo3,todo4)
        _toDoList.value = list_todo
    }
}