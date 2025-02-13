package ch.y.bitite.safespot.model.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import ch.y.bitite.safespot.model.ReportValidated;
import ch.y.bitite.safespot.utils.DateConverter;

@Database(entities = {ReportValidated.class}, version = 3, exportSchema = false) // Version is now 2
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract ReportDao reportDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_database")
                            .fallbackToDestructiveMigration() // Added this line
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}