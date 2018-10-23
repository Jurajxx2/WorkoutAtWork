package net.trasim.workoutinwork;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory;
import android.arch.persistence.room.testing.MigrationTestHelper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import net.trasim.workoutinwork.database.AppDatabase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static net.trasim.workoutinwork.database.AppDatabase.MIGRATION_4_5;

@RunWith(AndroidJUnit4.class)
public class MigrationTest {
    private static final String TEST_DB = "migration-test";
    private static final String TABLE_NAME = "Exercises";

    @Rule
    public MigrationTestHelper helper;

    public MigrationTest() {
        helper = new MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
                AppDatabase.class.getCanonicalName(),
                new FrameworkSQLiteOpenHelperFactory());
    }

    @Test
    public void migrate1To2() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 1);

        // db has schema version 1. insert some data using SQL queries.
        // You cannot use DAO classes because they expect the latest schema.
        db.execSQL("CREATE TABLE IF NOT EXISTS `$TEST_DB` " +
                "(`id` NUMBER, `name` TEXT NOT NULL, `heading` TEXT NOT NULL, `description` TEXT NOT NULL, `duration` NUMBER, `duration` NUMBER, `img` TEXT NOT NULL, `isEnabled` BOOLEAN," +
                "PRIMARY KEY(`id`))");

        db.execSQL("INSERT INTO `$TEST_DB` (game_name) "
                + "SELECT * "
                + "FROM `$TABLE_NAME`");

        // Prepare for the next version.
        db.close();

        // Re-open the database with version 2 and provide
        // MIGRATION_1_2 as the migration process.
        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_4_5);

        // MigrationTestHelper automatically verifies the schema changes,
        // but you need to validate that the data was migrated properly.
    }
}
