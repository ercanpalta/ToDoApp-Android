package com.ercanpalta.todo.viewmodel


import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ercanpalta.todo.database.ToDoDatabase
import com.ercanpalta.todo.model.TaskList
import com.ercanpalta.todo.model.ToDo
import kotlinx.coroutines.launch
import java.util.*

class HomeViewModel(application: Application) : BaseViewModel(application){

    private val _toDoList = MutableLiveData<List<ToDo>>()
    val toDoList:LiveData<List<ToDo>> = _toDoList

    private val _listList = MutableLiveData<List<TaskList>>()
    val listList:LiveData<List<TaskList>> = _listList

    private var lastUpdateTime = GregorianCalendar().get(Calendar.SECOND)
    private var refreshTime = 10

    private val dao = ToDoDatabase(getApplication()).dao()

    fun updateData(){
        launch {
            if((GregorianCalendar().get(Calendar.SECOND) - lastUpdateTime) < refreshTime){
                getDataFromRoom()
                println("refreshed from Room")
            }else{
                getDataFromFirebase()
                println("refreshed from Firebase")
            }
        }
    }

    suspend fun getDataFromRoom(){
        val list = dao.getAllLists()
        _listList.value = list

        val taskList = dao.getAllTasks()
        _toDoList.value = taskList
    }

    suspend fun  getDataFromFirebase(){
        getDataFromRoom()
    }

    fun addTask(newTask:ToDo){
        launch {
            dao.insert(newTask)
        }
    }

    fun addTaskList(newList:TaskList){
        launch {
            dao.insertList(newList)
        }
    }

    fun addAllLists(vararg lists: TaskList){
        launch {
            dao.insertAllLists(*lists)
        }
    }
}