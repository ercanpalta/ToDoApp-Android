package com.ercanpalta.todo.model

import androidx.room.ColumnInfo
import com.ercanpalta.todo.enums.TrackerType

data class Tracker(
    @ColumnInfo
    var trackerType: TrackerType = TrackerType.NON,
    @ColumnInfo
    var trackerCounter:Int = 0,
    @ColumnInfo
    var trackerMax:Int = 0,
    @ColumnInfo
    var trackerTimeInMillis:Long = 0
)
