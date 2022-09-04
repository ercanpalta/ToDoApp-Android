package com.ercanpalta.todo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ercanpalta.todo.model.TaskList
import com.ercanpalta.todo.model.ToDo

@Database(entities = [ToDo::class, TaskList::class], version = 1)
abstract class ToDoDatabase:RoomDatabase() {
        abstract fun  dao(): ToDoDao

        companion object{
            @Volatile private var instance : ToDoDatabase? = null

            private val lock = Any()

            operator fun invoke(context: Context) = instance ?: synchronized(lock){
                instance ?: createDatabase(context).also{
                    instance = it
                }
            }

            private fun createDatabase(context: Context) = Room.databaseBuilder(
                context.applicationContext, ToDoDatabase::class.java, "toDoDatabase"
            ).allowMainThreadQueries().build()

        }

}