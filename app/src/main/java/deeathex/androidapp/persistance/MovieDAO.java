package deeathex.androidapp.persistance;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import deeathex.androidapp.model.Movie;

@Dao
public interface MovieDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addMovie(Movie movie);

    @Query("select * from Movie")
    List<Movie> getMovies();

    @Query("delete from Movie")
    void removeAll();

}
