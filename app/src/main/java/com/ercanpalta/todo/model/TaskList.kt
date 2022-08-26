package com.ercanpalta.todo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TaskList(
    @ColumnInfo
    val name:String,
    @ColumnInfo
    val color: Int ){
    @PrimaryKey(autoGenerate = true)
    var uid = 0
}