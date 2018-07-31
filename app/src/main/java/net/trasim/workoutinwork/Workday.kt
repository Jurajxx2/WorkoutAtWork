package net.trasim.workoutinwork

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

import java.util.ArrayList
import java.util.Date

@Entity(tableName = "Workdays")
class Workday {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "date")
    var date: String? = null

    @ColumnInfo(name = "start")
    var start: Long = 0

    @ColumnInfo(name = "end")
    var end: Long = 0

    @ColumnInfo(name = "workoutsNo")
    var workouts: Int = 0

    @ColumnInfo(name = "minutes")
    var minutesDone: Int = 0

    @ColumnInfo(name = "workoutsSum")
    var workoutsDone: Int = 0

    @ColumnInfo(name = "caloriesSum")
    var caloriesDone: Int = 0

    @ColumnInfo(name = "length")
    var workDayLength: Long = 0

    @ColumnInfo(name = "finished")
    var finished: Boolean = false

    constructor()

    @Ignore
    constructor(date: String, start: Long, end: Long, workDayLength: Long) {
        this.date = date
        this.start = start
        this.end = end
        this.workDayLength = workDayLength
    }
}
