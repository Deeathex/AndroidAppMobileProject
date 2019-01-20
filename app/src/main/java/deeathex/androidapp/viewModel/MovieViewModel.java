package deeathex.androidapp.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.util.Log;

import java.util.List;

import deeathex.androidapp.api.MovieAPI;
import deeathex.androidapp.model.Auth;
import deeathex.androidapp.model.Movie;
import deeathex.androidapp.model.MovieWithToken;
import deeathex.androidapp.persistance.AppDatabase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieViewModel extends ViewModel {
    private static final String TAG = "Movies APP 2018";
    private MutableLiveData<List<Movie>> data;

    public void loadData(String token) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MovieAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieAPI api = retrofit.create(MovieAPI.class);
        Call<List<Movie>> call = api.getMovies(token);
        Log.i(TAG, "Load movies");
        System.out.println("Loaded movies");
        call.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                Log.i(TAG, "Load successful");
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                Log.i(TAG, "Load failed");
                Log.e(TAG, t.toString());
            }
        });
    }

    public LiveData<List<Movie>> getData(final Context context) {
        if (data == null) {
            data = new MutableLiveData<>();
            AppDatabase database = AppDatabase.getDatabase(context);
            String token = database.authDao().getAuth().get(0).getToken();
            System.out.println("Getting with token " + token);
            loadData(token);
        }
        return data;
    }

    public void update(MovieWithToken movie, Callback<Auth> callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MovieAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieAPI api = retrofit.create(MovieAPI.class);
        Call<Auth> call = api.updateMovie(movie);
        call.enqueue(callback);
    }

}
