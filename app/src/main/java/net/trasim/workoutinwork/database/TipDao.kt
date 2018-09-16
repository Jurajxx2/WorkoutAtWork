package net.trasim.workoutinwork.database

import android.arch.persistence.room.*
import net.trasim.workoutinwork.objects.Exercise
import net.trasim.workoutinwork.objects.Tip

@Dao
interface TipDao {

    @get:Query("SELECT * FROM Tips")
    val allTips: List<Tip>

    @Query("SELECT * FROM Tips where id = :id")
    fun getTipByID(id: Int): Tip

    @Insert
    fun insertTip(tip: Tip)

    @Delete
    fun deleteTip(tip: Tip)

    @Update
    fun updateTip(tip: Tip)
}