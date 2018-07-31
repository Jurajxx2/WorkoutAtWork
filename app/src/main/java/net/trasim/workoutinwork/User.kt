package net.trasim.workoutinwork

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

import java.util.ArrayList

@Entity(tableName = "User")
class User {

    @PrimaryKey
    var id: Int = 1

    @ColumnInfo(name = "weight")
    var weight: Int = 0
    @ColumnInfo(name = "height")
    var height: Int = 0
    @ColumnInfo(name = "birthYear")
    var birthYear: Int = 0
    @ColumnInfo(name = "gender")
    var gender: String? = null
    @ColumnInfo(name = "bmi")
    var bmi: Float = 0.toFloat()
    @ColumnInfo(name = "level")
    var level: String? = null
    @ColumnInfo(name = "countdown")
    var countDown: Int = 0
    @ColumnInfo(name = "language")
    var language: String? = null
    @ColumnInfo(name = "reminder")
    var reminderD: String? = null
    @ColumnInfo(name = "remindDays")
    var remindDays: String? = null
    @ColumnInfo(name = "remindTime")
    var remindTime: String? = null
    @ColumnInfo(name = "remindWorkout")
    var remindWorkout: String? = null
    @ColumnInfo(name = "remindInterval")
    var remindInterval: Int = 0

    constructor()

    @Ignore
    constructor(weight: Int, height: Int, birthYear: Int, gender: String) {
        this.weight = weight
        this.height = height
        this.birthYear = birthYear
        this.gender = gender

        countBMI(weight, height)
    }

    private fun countBMI(w: Int, h: Int) {
        val bmiIndex = (w / (h * h)).toFloat()
        bmi = bmiIndex
    }
}
