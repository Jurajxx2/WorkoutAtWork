package net.trasim.workoutinwork.database

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.RoomMasterTable.TABLE_NAME
import android.arch.persistence.room.migration.Migration
import org.jetbrains.anko.doAsync

class Migration4To5 : Migration(4,5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        val TABLE_NAME_TEMP = "ExercisesTemp"
        val TABLE_NAME = "Exercises"
        val TABLE_NAME_TEMP_2 = "TipsTemp"
        val TABLE_NAME_2 = "Tips"

        // 1. Create new table
        database.execSQL("CREATE TABLE IF NOT EXISTS `$TABLE_NAME_TEMP` " +
                "(`id` NUMBER, `name` TEXT NOT NULL, `heading` TEXT NOT NULL, `description` TEXT NOT NULL, `duration` NUMBER, `duration` NUMBER, `img` TEXT NOT NULL, `isEnabled` BOOLEAN," +
                "PRIMARY KEY(`id`))")

        database.execSQL("CREATE TABLE IF NOT EXISTS `$TABLE_NAME_TEMP_2` " +
                "(`id` NUMBER, `heading` TEXT NOT NULL, `headingsk` TEXT NOT NULL, `text` TEXT NOT NULL, `textsk` TEXT NOT NULL," +
                "PRIMARY KEY(`id`))")

        // 2. Copy the data
//        database.execSQL("INSERT INTO $TABLE_NAME_TEMP (game_name) "
//                + "SELECT * "
//                + "FROM $TABLE_NAME")

        // 3. Remove the old table
        database.execSQL("DROP TABLE $TABLE_NAME")
        database.execSQL("DROP TABLE $TABLE_NAME_2")

        // 4. Change the table name to the correct one
        database.execSQL("ALTER TABLE $TABLE_NAME_TEMP RENAME TO $TABLE_NAME")
        database.execSQL("ALTER TABLE $TABLE_NAME_TEMP_2 RENAME TO $TABLE_NAME_2")

        //5. insert data with translations
    }
}