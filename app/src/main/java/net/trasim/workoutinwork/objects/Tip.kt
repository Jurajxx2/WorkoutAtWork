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
    var heading: String = ""
    @ColumnInfo(name = "text")
    var text: String = ""

    constructor()

    @Ignore
    constructor(heading: String, text: String){
        this.heading = heading
        this.text = text
    }
}