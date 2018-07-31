package net.trasim.workoutinwork.Database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update

import net.trasim.workoutinwork.Workout

import java.util.ArrayList

@Dao
interface WorkoutDao {
    @get:Query("SELECT * FROM Workouts")
    val allWorkouts: List<Workout>

    @Query("SELECT * FROM Workouts where id = :id")
    fun getWorkoutByID(id: Int): Workout

    @Insert
    fun insertWorkout(Workout: Workout)

    @Delete
    fun deleteWorkout(Workout: Workout)

    @Update
    fun updateWorkout(Workout: Workout)
}
