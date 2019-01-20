package deeathex.androidapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import deeathex.androidapp.ui.MovieListFragment;

public class MovieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MovieListFragment.newInstance())
                    .commitNow();
        }
    }

}
