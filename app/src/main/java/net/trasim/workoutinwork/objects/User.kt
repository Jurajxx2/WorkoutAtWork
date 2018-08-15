package net.trasim.workoutinwork.objects

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

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
    var level: Int = 0
    @ColumnInfo(name = "countdown")
    var countDown: Int = 0
    @ColumnInfo(name = "language")
    var language: String? = null
    @ColumnInfo(name = "remindDays")
    var remindDays: String? = null
    @ColumnInfo(name = "remindTime")
    var remindTime: String? = null
    @ColumnInfo(name = "reminder")
    var reminder: String? = null
    @ColumnInfo(name = "remindInterval")
    var remindInterval: Int = 0
    @ColumnInfo(name = "nextReminder")
    var nextReminder: Long = 0


    constructor()

    @Ignore
    constructor(weight: Int, height: Int, birthYear: Int, gender: String, countDown: Int, language: String, remindDays: String, remindTime: String, reminder: String, remindInterval: Int, level: Int) {
        this.weight = weight
        this.height = height
        this.birthYear = birthYear
        this.gender = gender

        this.countDown = countDown
        this.language = language
        this.remindDays = remindDays
        this.remindTime = remindTime
        this.reminder = reminder
        this.remindInterval = remindInterval
        this.level = level



        countBMI(weight, height)
    }

    private fun countBMI(w: Int, h: Int) {
        val bmiIndex = (w / (h * h)).toFloat()
        bmi = bmiIndex
    }
}
