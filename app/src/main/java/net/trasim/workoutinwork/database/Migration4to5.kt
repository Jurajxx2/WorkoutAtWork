package net.trasim.workoutinwork.database

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.RoomMasterTable.TABLE_NAME
import android.arch.persistence.room.migration.Migration
import net.trasim.workoutinwork.objects.Exercise
import org.jetbrains.anko.doAsync

class Migration4To5 : Migration(4,5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        val TABLE_TEMP_EXERCISES = "ExercisesTemp"
        val TABLE_NAME_EXERCISES = "Exercises"

        val TABLE_TEMP_TIPS = "TipsTemp"
        val TABLE_NAME_TIPS = "Tips"

        val TABLE_TEMP_WORKDAY = "WorkdaysTemp"
        val TABLE_NAME_WORKDAY = "Workdays"

        val TABLE_TEMP_WORKOUT = "WorkoutsTemp"
        val TABLE_NAME_WORKOUT = "Workouts"

        var exercises = database.execSQL("SELECT * FROM `$TABLE_NAME_EXERCISES`")

        // 1. Create new table
        database.execSQL("CREATE TABLE IF NOT EXISTS `$TABLE_TEMP_EXERCISES` " +
                "(`id` NUMBER, `nameEN` TEXT NOT NULL, `nameSK` TEXT NOT NULL, `headingEN` TEXT NOT NULL, `headingSK` TEXT NOT NULL, `descriptionEN` TEXT NOT NULL, `descriptionSK` TEXT NOT NULL, `duration` NUMBER, `duration` NUMBER, `img` TEXT NOT NULL, `isEnabled` BOOLEAN," +
                "PRIMARY KEY(`id`))")

        database.execSQL("CREATE TABLE IF NOT EXISTS `$TABLE_TEMP_TIPS` " +
                "(`id` NUMBER, `heading` TEXT NOT NULL, `headingSK` TEXT NOT NULL, `text` TEXT NOT NULL, `textSK` TEXT NOT NULL," +
                "PRIMARY KEY(`id`))")

        database.execSQL("CREATE TABLE IF NOT EXISTS `$TABLE_TEMP_WORKDAY` " +
                "(`id` NUMBER, `date` TEXT NOT NULL, `start` NUMBER, `end` NUMBER, `workoutsNo` NUMBER, `workoutsSum` NUMBER, `caloriesSum` NUMBER, `length` NUMBER, `finished` NUMBER," +
                "PRIMARY KEY(`id`))")

        database.execSQL("CREATE TABLE IF NOT EXISTS `$TABLE_TEMP_WORKOUT` " +
                "(`id` NUMBER, `exerciseID` NUMBER, `workdayID` NUMBER, `duration` NUMBER, `repetitions` NUMBER," +
                "PRIMARY KEY(`id`))")

        // 2. Copy the data
        database.execSQL("INSERT INTO $TABLE_TEMP_WORKDAY "
                + "SELECT * "
                + "FROM $TABLE_NAME_WORKDAY")

        database.execSQL("INSERT INTO $TABLE_TEMP_WORKOUT "
                + "SELECT * "
                + "FROM $TABLE_NAME_WORKOUT")

        // 3. Remove the old table
        database.execSQL("DROP TABLE $TABLE_NAME_EXERCISES")
        database.execSQL("DROP TABLE $TABLE_NAME_TIPS")
        database.execSQL("DROP TABLE $TABLE_NAME_WORKOUT")
        database.execSQL("DROP TABLE $TABLE_NAME_WORKDAY")

        // 4. Change the table name to the correct one
        database.execSQL("ALTER TABLE $TABLE_TEMP_EXERCISES RENAME TO $TABLE_NAME_EXERCISES")
        database.execSQL("ALTER TABLE $TABLE_TEMP_TIPS RENAME TO $TABLE_NAME_TIPS")
        database.execSQL("ALTER TABLE $TABLE_TEMP_WORKDAY RENAME TO $TABLE_NAME_WORKDAY")
        database.execSQL("ALTER TABLE $TABLE_TEMP_WORKOUT RENAME TO $TABLE_NAME_WORKOUT")
    }
}