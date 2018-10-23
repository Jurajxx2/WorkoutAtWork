package net.trasim.workoutinwork.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update

import net.trasim.workoutinwork.objects.Workout

@Dao
interface WorkoutDao {
    @get:Query("SELECT * FROM Workouts")
    val allWorkouts: List<Workout>

    @get:Query("SELECT count() FROM Workouts")
    val countWorkouts: Int

    @Query("SELECT * FROM Workouts where id = :id")
    fun getWorkoutByID(id: Int): Workout

    @Query("SELECT * FROM Workouts where workdayID = :workdayID")
    fun getWorkoutsByWorkdayID(workdayID: Int): List<Workout>

    @Insert
    fun insertWorkout(Workout: Workout)

    @Delete
    fun deleteWorkout(Workout: Workout)

    @Update
    fun updateWorkout(Workout: Workout)
}
