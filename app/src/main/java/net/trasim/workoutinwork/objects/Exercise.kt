package net.trasim.workoutinwork.objects

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "Exercises")
class Exercise {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "name")
    var nameEN: String? = null
    @ColumnInfo(name = "nameSK")
    var nameSK: String? = null
    @ColumnInfo(name = "heading")
    var headingEN: String? = null
    @ColumnInfo(name = "headingSK")
    var headingSK: String? = null
    @ColumnInfo(name = "description")
    var descriptionEN: String? = null
    @ColumnInfo(name = "descriptionSK")
    var descriptionSK: String? = null
    @ColumnInfo(name = "repetitions")
    var repetitions: Int = 0
    @ColumnInfo(name = "duration")
    var duration: Int = 0
    @ColumnInfo(name = "img")
    var img: String? = null
    @ColumnInfo(name = "enabled")
    var isEnabled: Boolean = false


    constructor()

    @Ignore
    constructor(nameEN: String, nameSK: String, headingEN: String, headingSK: String, descriptionEN: String, descriptionSK: String, repetitions: Int, duration: Int, img: String, isEnabled: Boolean) {
        this.nameEN = nameEN
        this.nameSK = nameSK
        this.headingEN = headingEN
        this.headingSK = headingSK
        this.descriptionEN = descriptionEN
        this.descriptionSK = descriptionSK
        this.repetitions = repetitions
        this.duration = duration
        this.img = img
        this.isEnabled = isEnabled
    }
}
