package net.trasim.workoutinwork.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import net.trasim.workoutinwork.objects.*

@Database(entities = [(Workday::class), (Workout::class), (Exercise::class), (Tip::class)], version = 5)
abstract class AppDatabase : RoomDatabase() {

    abstract fun workdayModel(): WorkdayDao

    abstract fun workoutModel(): WorkoutDao

    abstract fun exerciseModel(): ExerciseDao

    abstract fun tipModel(): TipDao

    companion object {

        @JvmField
        val MIGRATION_4_5 = Migration4To5()

        private var sInstance: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            if (sInstance == null) {
                sInstance = Room
                        .databaseBuilder(context.applicationContext, AppDatabase::class.java, "database")
                        .addMigrations(AppDatabase.MIGRATION_4_5)
                        .build()
            }
            return sInstance!!
        }

        fun destroyInstance() {
            sInstance = null
        }


    }
}