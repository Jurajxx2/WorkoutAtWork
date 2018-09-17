package net.trasim.workoutinwork.objects


import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "Workouts")
class Workout {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "exerciseID")
    var exerciseID: Int = 0
    @ColumnInfo(name = "workdayID")
    var workdayID: Int = 0
    @ColumnInfo(name = "duration")
    var duration: Int = 0
    @ColumnInfo(name = "repetitions")
    var repetitions: Int = 0

    constructor()

    @Ignore
    constructor(workdayID: Int) {
        this.workdayID = workdayID
    }
}
