package deeathex.androidapp.api;

import deeathex.androidapp.model.Auth;
import deeathex.androidapp.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface LoginApi {
    String BASE_IP = "172.30.113.52";
    String BASE_URL = "http://" + BASE_IP + ":5000/";

    @Headers("Content-Type: application/json")
    @POST("login")
    Call<Auth> login(@Body User user);

    @Headers("Content-Type: application/json")
    @POST("logout")
    Call<Auth> logout(@Body User user);
}
