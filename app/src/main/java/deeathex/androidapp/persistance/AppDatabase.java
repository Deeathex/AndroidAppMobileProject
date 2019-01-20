package deeathex.androidapp.persistance;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import deeathex.androidapp.model.Movie;
import deeathex.androidapp.model.StoredAuth;

@Database(entities = {Movie.class, StoredAuth.class}, version = 17, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public abstract MovieDAO movieDao();
    public abstract AuthDAO authDao();

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context, AppDatabase.class, "moviesDb")
                            .allowMainThreadQueries()
                            // recreate the database if necessary
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;

    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

}
