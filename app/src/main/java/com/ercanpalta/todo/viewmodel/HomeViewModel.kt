package com.ercanpalta.todo.viewmodel

import android.graphics.Color
import android.graphics.Color.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ercanpalta.todo.R
import com.ercanpalta.todo.model.TaskList
import com.ercanpalta.todo.model.ToDo

class HomeViewModel : ViewModel() {

    private val _toDoList = MutableLiveData<List<ToDo>>()
    val toDoList:LiveData<List<ToDo>> = _toDoList

    private val _listList = MutableLiveData<List<TaskList<ToDo>>>()
    val listList:LiveData<List<TaskList<ToDo>>> = _listList



    fun updateData(){
        val todo1 = ToDo("Todo1")
        val todo2 = ToDo("Todo2")
        val todo3 = ToDo("Todo3")
        val todo4 = ToDo("Todo4")

        val list1 = TaskList<ToDo>("All", Color.CYAN)
        val list2 = TaskList<ToDo>("School", Color.GREEN)
        val list3 = TaskList<ToDo>("School", Color.MAGENTA)
        val list4 = TaskList<ToDo>("School", Color.YELLOW)
        val list5 = TaskList<ToDo>("New List", Color.LTGRAY)

        val list_list = listOf<TaskList<ToDo>>(list1,list2,list3,list4,list5)
        _listList.value = list_list

        val list_todo = listOf<ToDo>(todo1,todo2,todo3,todo4)
        _toDoList.value = list_todo
    }
}