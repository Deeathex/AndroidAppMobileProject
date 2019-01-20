package deeathex.androidapp.persistance;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import deeathex.androidapp.model.StoredAuth;

@Dao
public interface AuthDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addAuth(StoredAuth auth);

    @Query("select * from storedauth")
    List<StoredAuth> getAuth();

    @Query("delete from storedauth")
    void removeAll();

}
