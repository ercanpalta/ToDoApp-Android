package com.ercanpalta.todo.model

import com.ercanpalta.todo.enums.Priority

data class ToDo(var task:String, var priority: Priority = Priority.LOW, var isCompleted: Boolean = false, var listName:String = "All"){
    var uid = 0
}