package net.trasim.workoutinwork

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "Exercises")
class Exercise {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "name")
    var name: String? = null
    @ColumnInfo(name = "heading")
    var heading: String? = null
    @ColumnInfo(name = "description")
    var description: String? = null
    @ColumnInfo(name = "repetitions")
    var repetitions: Int = 0
    @ColumnInfo(name = "duration")
    var duration: Int = 0
    @ColumnInfo(name = "img")
    var img: String? = null

    constructor() {}

    @Ignore
    constructor(name: String, heading: String, description: String, repetitions: Int, duration: Int, img: String) {
        this.name = name
        this.heading = heading
        this.description = description
        this.repetitions = repetitions
        this.duration = duration
        this.img = img
    }
}
