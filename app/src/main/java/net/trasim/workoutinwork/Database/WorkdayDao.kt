package net.trasim.workoutinwork.Database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update

import net.trasim.workoutinwork.Workday

import java.util.ArrayList

@Dao
interface WorkdayDao {
    @get:Query("SELECT * FROM Workdays")
    val allWorkdays: List<Workday>

    @Query("SELECT * FROM Workdays where id = :id")
    fun getWorkdayByID(id: Int): Workday

    @Query("SELECT * FROM Workdays WHERE   id = (SELECT MAX(id)  FROM Workdays)")
    fun getLastWorkday() : Workday

    @Insert
    fun insertWorkday(Workday: Workday)

    @Delete
    fun deleteWorkday(Workday: Workday)

    @Update
    fun updateWorkday(Workday: Workday)
}
