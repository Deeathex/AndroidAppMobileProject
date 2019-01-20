package deeathex.androidapp.api;

import java.util.List;

import deeathex.androidapp.model.Auth;
import deeathex.androidapp.model.Movie;
import deeathex.androidapp.model.MovieWithToken;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface MovieAPI {
    String BASE_URL = "http://" + LoginApi.BASE_IP + ":5000/";

    @GET("movies")
    Call<List<Movie>> getMovies(@Query("token") String token);

    @PUT("movies")
    Call<Auth> updateMovie(@Body MovieWithToken movie);
}
