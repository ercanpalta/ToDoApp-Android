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

    private val _toDo = MutableLiveData<ToDo>()
    val toDo:LiveData<ToDo> = _toDo

    private val _taskList = MutableLiveData<TaskList>()
    val taskList:LiveData<TaskList> = _taskList

    private var lastUpdateTime = GregorianCalendar().get(Calendar.SECOND)
    private var refreshTime = 10



    var currentListName = "All"



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

    private suspend fun getDataFromRoom(){
        val list = dao.getAllLists()
        _listList.value = list

        val taskList = dao.getAllTasks()
        _toDoList.value = taskList
    }

    private suspend fun  getDataFromFirebase(){
        getDataFromRoom()
    }


    // Database functions
    fun getTask(uid: Int){
        launch {
            _toDo.value = dao.getTask(uid)
        }
    }


    fun addTask(newTask:ToDo){
        launch {
            dao.insertTask(newTask)
        }
        updateData()
    }

    fun updateTask(task:ToDo){
        launch {
            dao.updateTask(task)
        }
        updateData()
    }

    fun deleteTask(uid: Int){
        launch {
            dao.deleteTask(uid)
        }
    }

    fun getTaskList(uid: Int){
        launch {
            _taskList.value = dao.getTaskList(uid)
        }
    }

    fun addTaskList(newList:TaskList){
        launch {
            dao.insertList(newList)
        }
    }

    fun updateTaskList(list:TaskList){
        launch {
            dao.updateTaskList(list)
        }
        updateData()
    }

    fun deleteTaskList(uid: Int){
        launch {
            dao.deleteTaskList(uid)
        }
        updateData()
    }

    fun deleteAllTasksInsideList(listName:String){
        launch {
            dao.deleteAllTasksInsideList(listName)
        }
        currentListName = "All"
        updateData()
    }

    fun updateCompletion(uid:Int, isCompleted:Boolean){
        launch {
            dao.updateCompletion(uid, isCompleted)
        }
    }

    fun updateRequestCode(requestCode:Int){
        launch {
            dao.updateRequestCode(requestCode)
        }
    }

    fun updateTracker(task:ToDo){
        launch {
            dao.updateTask(task)
        }
    }
}