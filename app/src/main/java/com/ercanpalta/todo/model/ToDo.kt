package com.ercanpalta.todo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ercanpalta.todo.enums.Priority

@Entity
data class ToDo(
    @ColumnInfo
    var task:String,
    @ColumnInfo
    var priority: Priority = Priority.LOW,
    @ColumnInfo
    var isCompleted: Boolean = false,
    @ColumnInfo
    var listName:String = "All"){
    @PrimaryKey(autoGenerate = true)
    var uid:Int = 0

    @ColumnInfo
    var description = ""
}