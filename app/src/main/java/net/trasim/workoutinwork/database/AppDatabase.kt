package net.trasim.workoutinwork.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

import net.trasim.workoutinwork.objects.Exercise
import net.trasim.workoutinwork.objects.User
import net.trasim.workoutinwork.objects.Workday
import net.trasim.workoutinwork.objects.Workout

@Database(entities = arrayOf(User::class, Workday::class, Workout::class, Exercise::class), version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userModel(): UserDao

    abstract fun workdayModel(): WorkdayDao

    abstract fun workoutModel(): WorkoutDao

    abstract fun exerciseModel(): ExerciseDao

    companion object {

        private var sInstance: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            if (sInstance == null) {
                sInstance = Room
                        .databaseBuilder(context.applicationContext, AppDatabase::class.java, "database")
                        .fallbackToDestructiveMigration()
                        .build()
            }
            return sInstance!!
        }

        fun destroyInstance() {
            sInstance = null
        }
    }
}
