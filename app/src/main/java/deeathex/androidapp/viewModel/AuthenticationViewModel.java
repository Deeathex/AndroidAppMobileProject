package deeathex.androidapp.viewModel;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.util.Log;

import java.io.UnsupportedEncodingException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import deeathex.androidapp.TokenService;
import deeathex.androidapp.api.LoginApi;
import deeathex.androidapp.model.Auth;
import deeathex.androidapp.model.StoredAuth;
import deeathex.androidapp.model.User;
import deeathex.androidapp.persistance.AppDatabase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.util.Base64.encodeToString;

public class AuthenticationViewModel extends ViewModel {

    public static User currentUser;
    public static String token = "";


    public void login(final String username, final String password, final Callback<Auth> callback, final Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(LoginApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LoginApi api = retrofit.create(LoginApi.class);
        Call<Auth> call = api.login(new User(username, password));
        call.enqueue(new Callback<Auth>() {
            @Override
            public void onResponse(Call<Auth> call, Response<Auth> response) {
                if (response.isSuccessful()) {


                    Log.i("LOGIN", "success");
                    String token = response.body().toString();
                    AppDatabase database = AppDatabase.getDatabase(context);
                    database.authDao().removeAll();
                    database.authDao().addAuth(new StoredAuth(username, password, token));
                    TokenService.token = token;
                    Log.i("TOKEN", token);
                    try {
                        String keyBase64 = encodeToString("sadf86d6sfdsf76dfsa76dgs7a6g7dsa697dsag6".getBytes("utf-8"), 0);
                        //String base64token = encodeToString(token.getBytes("utf-8"), 0);
                        Claims body = Jwts.parser().setSigningKey(keyBase64).parseClaimsJws(token).getBody();
                        TokenService.username = body.get("username", String.class);
                        Log.i("LOGIN", body.toString());
                        callback.onResponse(call, response);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        callback.onFailure(call, new Throwable("Something went wrong"));
                    }
                }
            }

            @Override
            public void onFailure(Call<Auth> call, Throwable t) {
                Log.i("LOGIN", "failed");
                callback.onFailure(call, new Throwable("Login failed, try again!"));

            }
        });
    }

    public void logout(String username, String password, String token, final Callback callback, final Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(LoginApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LoginApi api = retrofit.create(LoginApi.class);
        Call<Auth> call = api.logout(new User(username, password, token));
        call.enqueue(new Callback<Auth>() {
            @Override
            public void onResponse(Call<Auth> call, Response<Auth> response) {
                Log.i("LOGOUT", "success");

                AppDatabase database = AppDatabase.getDatabase(context);
                database.authDao().removeAll();
                database.authDao().addAuth(new StoredAuth("", "", ""));
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<Auth> call, Throwable t) {
                Log.i("LOGIN", "failed");
                callback.onFailure(call, t);
            }
        });
    }

}
