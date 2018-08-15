package net.trasim.workoutinwork.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update

import net.trasim.workoutinwork.objects.User

@Dao
interface UserDao {
    @get:Query("SELECT * FROM User")
    val allUsers: List<User>

    @Query("SELECT * FROM User where id = :id")
    fun getUserByID(id: Int): User

    @Query("DELETE FROM User")
    fun deleteAllUsers()

    @Insert
    fun insertUser(User: User)

    @Delete
    fun deleteUser(User: User)

    @Update
    fun updateUser(User: User)
}
