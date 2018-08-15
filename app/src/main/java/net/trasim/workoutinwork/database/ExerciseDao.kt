package net.trasim.workoutinwork.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update

import net.trasim.workoutinwork.objects.Exercise

@Dao
interface ExerciseDao {

    @get:Query("SELECT * FROM Exercises")
    val allExercises: List<Exercise>

    @Query("SELECT * FROM Exercises where id = :id")
    fun getExerciseByID(id: Int): Exercise

    @Insert
    fun insertExercise(exercise: Exercise)

    @Delete
    fun deleteExercise(exercise: Exercise)

    @Update
    fun updateExercise(exercise: Exercise)
}
