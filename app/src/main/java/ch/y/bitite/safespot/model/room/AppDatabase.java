package ch.y.bitite.safespot.model.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import ch.y.bitite.safespot.model.ReportValidated;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

import javax.inject.Singleton;

@Database(entities = {ReportValidated.class}, version = 6, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ReportDao reportDao();

    private static final String DATABASE_NAME = "app_database";

    static final Migration MIGRATION_5_6 = new Migration(8, 9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE reports_validated RENAME TO reports_validated_old");
            database.execSQL("CREATE TABLE reports_validated (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, longitude REAL NOT NULL, latitude REAL NOT NULL, date_Time TEXT, description TEXT, image TEXT, video TEXT, comment TEXT, date_Time_Validation TEXT)");
            database.execSQL("INSERT INTO reports_validated (id, longitude, latitude, date_Time, description, image, video, comment, date_Time_Validation) SELECT id, longitude, latitude, date_Time, description, image, video, comment, date_Time_Validation FROM reports_validated_old");
            database.execSQL("DROP TABLE reports_validated_old");
        }
    };

    @Module
    @InstallIn(SingletonComponent.class)
    public static class DatabaseModule {

        @Provides
        @Singleton
        public static AppDatabase provideAppDatabase(@ApplicationContext Context context) {
            return Room.databaseBuilder(
                            context,
                            AppDatabase.class,
                            DATABASE_NAME)
                    .addMigrations(MIGRATION_5_6)
                    .fallbackToDestructiveMigration()
                    .build();
        }

        @Provides
        public static ReportDao provideReportDao(AppDatabase appDatabase) {
            return appDatabase.reportDao();
        }
    }
}