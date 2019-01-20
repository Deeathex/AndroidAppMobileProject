package deeathex.androidapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import deeathex.androidapp.model.Auth;
import deeathex.androidapp.model.StoredAuth;
import deeathex.androidapp.persistance.AppDatabase;
import deeathex.androidapp.viewModel.AuthenticationViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    AuthenticationViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(deeathex.androidapp.R.layout.activity_main);
        this.viewModel = new AuthenticationViewModel();

        AppDatabase database = AppDatabase.getDatabase(getApplicationContext());
//        database.authDao().removeAll();
        List<StoredAuth> auths = database.authDao().getAuth();

        System.out.println(auths.size());
        if (auths.size() > 0) {
            StoredAuth auth = auths.get(0);
            if (!auth.getUsername().isEmpty()) {

                System.out.println("Stored was " + auth);
                viewModel.login(auth.getUsername(), auth.getPassword(), new Callback<Auth>() {
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) {

                        if (response.isSuccessful()) {

                            Intent intent = new Intent(MainActivity.this, MovieActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }, getApplicationContext());
            }
        }

        Button loginBtn = findViewById(R.id.login_button);


        loginBtn.setOnClickListener(getLoginButtonListener());
    }

    public View.OnClickListener getLoginButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("BUTTON", "Login clicked");
                String username = ((EditText) findViewById(R.id.user_name)).getText().toString();
                String password = ((EditText) findViewById(R.id.password)).getText().toString();
                viewModel.login(username, password, new Callback<Auth>() {
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) {
                        if (response.isSuccessful()) {

                            Intent intent = new Intent(MainActivity.this, MovieActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "login failed", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }, getApplicationContext());

            }
        };
    }


}
