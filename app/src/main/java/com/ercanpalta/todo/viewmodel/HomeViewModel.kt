package com.ercanpalta.todo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ercanpalta.todo.model.ToDo

class HomeViewModel : ViewModel() {

    private val _toDoList = MutableLiveData<List<ToDo>>()
    val toDoList:LiveData<List<ToDo>> = _toDoList


    fun updateData(){
        val todo1 = ToDo("Todo1")
        val todo2 = ToDo("Todo2")
        val todo3 = ToDo("Todo3")
        val todo4 = ToDo("Todo4")

        val list_todo = listOf<ToDo>(todo1,todo2,todo3,todo4)
        _toDoList.value = list_todo
    }
}