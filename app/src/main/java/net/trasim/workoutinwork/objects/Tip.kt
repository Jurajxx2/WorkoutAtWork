package net.trasim.workoutinwork.objects

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "Tips")
class Tip {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "heading")
    var headingEN: String = ""
    @ColumnInfo(name = "headingsk")
    var headingSK: String = ""
    @ColumnInfo(name = "text")
    var textEN: String = ""
    @ColumnInfo(name = "textsk")
    var textSK: String = ""

    constructor()

    @Ignore
    constructor(headingEN: String, headingSK: String, textEN: String, textSK: String){
        this.headingEN = headingEN
        this.headingSK = headingSK
        this.textEN = textEN
        this.textSK = textSK
    }
}