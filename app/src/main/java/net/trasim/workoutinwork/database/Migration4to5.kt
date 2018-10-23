package net.trasim.workoutinwork.database

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.RoomMasterTable.TABLE_NAME
import android.arch.persistence.room.migration.Migration

class Migration4To5 : Migration(4,5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        val TABLE_NAME_TEMP = "GameNew"
        val TABLE_NAME = "Exercises"

        // 1. Create new table
        database.execSQL("CREATE TABLE IF NOT EXISTS `$TABLE_NAME_TEMP` " +
                "(`id` NUMBER, `name` TEXT NOT NULL, `heading` TEXT NOT NULL, `description` TEXT NOT NULL, `duration` NUMBER, `duration` NUMBER, `img` TEXT NOT NULL, `isEnabled` BOOLEAN," +
                "PRIMARY KEY(`id`))")

        // 2. Copy the data
        database.execSQL("INSERT INTO $TABLE_NAME_TEMP (game_name) "
                + "SELECT * "
                + "FROM $TABLE_NAME")

        // 3. Remove the old table
        database.execSQL("DROP TABLE $TABLE_NAME")

        // 4. Change the table name to the correct one
        database.execSQL("ALTER TABLE $TABLE_NAME_TEMP RENAME TO $TABLE_NAME")
    }
}