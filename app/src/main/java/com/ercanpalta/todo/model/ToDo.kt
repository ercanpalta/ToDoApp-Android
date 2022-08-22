package com.ercanpalta.todo.model

data class ToDo(var task:String, var isCompleted: Boolean = false){
    var uid = 0
}