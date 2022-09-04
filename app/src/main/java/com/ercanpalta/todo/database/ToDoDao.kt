package com.ercanpalta.todo.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ercanpalta.todo.model.TaskList
import com.ercanpalta.todo.model.ToDo

@Dao
interface ToDoDao {

    @Insert
    suspend fun insertTask(todo:ToDo):Long

    @Insert
    suspend fun  insertList(list : TaskList):Long

    @Insert
    suspend fun  insertAllLists(vararg lists: TaskList) : List<Long>

    @Query("SELECT * FROM todo")
    suspend fun getAllTasks(): List<ToDo>

    @Query("SELECT * FROM tasklist")
    suspend fun getAllLists(): List<TaskList>

    @Query("DELETE FROM todo")
    suspend fun deleteAllTasks()

    @Query("DELETE FROM TaskList")
    suspend fun deleteAllTaskLists()

    @Query("DELETE FROM ToDo WHERE uid= :uid")
    suspend fun deleteTask(uid:Int)

    @Query("UPDATE TODO SET isCompleted= :isCompleted WHERE uid= :uid")
    suspend fun updateCompletion(uid:Int, isCompleted:Boolean)


}