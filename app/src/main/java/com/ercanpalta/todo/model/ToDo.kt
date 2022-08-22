package com.ercanpalta.todo.model

import com.ercanpalta.todo.enums.Priority

data class ToDo(var task:String, var priority: Priority = Priority.LOW, var isCompleted: Boolean = false){
    var uid = 0
}