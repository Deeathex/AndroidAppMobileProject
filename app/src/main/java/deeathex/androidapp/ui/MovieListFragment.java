package deeathex.androidapp.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import deeathex.androidapp.MainActivity;
import deeathex.androidapp.R;
import deeathex.androidapp.api.LoginApi;
import deeathex.androidapp.model.Auth;
import deeathex.androidapp.model.Movie;
import deeathex.androidapp.model.StoredAuth;
import deeathex.androidapp.model.MovieWithToken;
import deeathex.androidapp.persistance.AppDatabase;
import deeathex.androidapp.viewModel.AuthenticationViewModel;
import deeathex.androidapp.viewModel.MovieViewModel;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieListFragment extends Fragment {
    private MovieViewModel movieViewModel;
    private AuthenticationViewModel authViewModel;
    private RecyclerView movieList;
    private Button logoutBtn;
    private Button updateBtn;
    private EditText idInput;
    private EditText titleInput;
    private EditText descriptionInput;
    private AppDatabase database;
    private WebSocketClient mWebSocketClient;
    private PieChartView pieChartView;
    List<SliceValue> pieData = new ArrayList<>();

    public static MovieListFragment newInstance() {
        return new MovieListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.movie_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        movieList = view.findViewById(R.id.movie_list);
        logoutBtn = view.findViewById(R.id.logout_btn);
        idInput = view.findViewById(R.id.movie_id_input);
        titleInput = view.findViewById(R.id.movie_title_input);
        descriptionInput = view.findViewById(R.id.movie_description_input);
        updateBtn = view.findViewById(R.id.update_btn);

        pieChartView = view.findViewById(R.id.chart);
        pieData.add(new SliceValue(5, Color.BLUE).setLabel("Burns my mind: 5"));
        pieData.add(new SliceValue(1, Color.GRAY).setLabel("Adventure: 1"));
        pieData.add(new SliceValue(3, Color.RED).setLabel("Romance: 3"));
        pieData.add(new SliceValue(1, Color.MAGENTA).setLabel("Comedy: 1"));
        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true);
        pieChartData.setHasCenterCircle(true).setCenterText1("Genres").setCenterText1FontSize(20).setCenterText1Color(Color.parseColor("#000000"));
        pieChartView.setPieChartData(pieChartData);

        movieList.setAdapter(new MovieListAdapter(getActivity()));
        connectWebSocket();
    }

    public OnItemClickListener itemClickListener() {
        return new OnItemClickListener() {
            @Override
            public void onItemClick(Movie item) {
                Toast.makeText(getContext(), "Clicked on " + item.toString(), Toast.LENGTH_SHORT).show();
                setSelection(item);
            }
        };
    }

    private void setSelection(Movie item) {
        idInput.setText(item.getId().toString());
        titleInput.setText(item.getTitle());
        descriptionInput.setText(item.getDescription());
    }

    private void clearFields() {
        idInput.setText("");
        titleInput.setText("");
        descriptionInput.setText("");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        movieList.setLayoutManager(new LinearLayoutManager(getActivity()));

        database = AppDatabase.getDatabase(getContext());
        List<Movie> movieBackup = database.movieDao().getMovies();
        MovieListAdapter movieListAdapter = new MovieListAdapter(getActivity(), movieBackup, itemClickListener());
        movieList.setAdapter(movieListAdapter);
        Toast.makeText(getContext(), "Showing most recent items", Toast.LENGTH_LONG).show();

        movieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        movieViewModel.getData(getContext()).observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                System.out.println("Changed");
                database.movieDao().removeAll();
                for (Movie t : movies) {
                    database.movieDao().addMovie(t);
                }
                MovieListAdapter movieListAdapter = new MovieListAdapter(getActivity(), movies, itemClickListener());
                movieList.setAdapter(movieListAdapter);

                Toast.makeText(getContext(), "Showing api items", Toast.LENGTH_LONG).show();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database = AppDatabase.getDatabase(getContext());
                List<StoredAuth> auths = database.authDao().getAuth();
                if (auths.size() > 0) {

                    StoredAuth auth = auths.get(0);
                    if (!auth.getUsername().equals("")) {

                        authViewModel = ViewModelProviders.of(MovieListFragment.this).get(AuthenticationViewModel.class);
                        authViewModel.logout(auth.getUsername(), auth.getPassword(), auth.getToken(), new Callback() {
                            @Override
                            public void onResponse(Call call, Response response) {
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onFailure(Call call, Throwable t) {
                                Toast.makeText(getContext(), "Error logging out", Toast.LENGTH_LONG).show();
                            }
                        }, getContext());
                    }

                }
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movieViewModel = ViewModelProviders.of(MovieListFragment.this).get(MovieViewModel.class);
                database = AppDatabase.getDatabase(getContext());
                List<StoredAuth> auths = database.authDao().getAuth();

                if (auths.size() > 0) {
                    StoredAuth auth = auths.get(0);
                    if (!auth.getUsername().isEmpty()) {

                        Integer id = Integer.parseInt(idInput.getText().toString());
                        String title = titleInput.getText().toString();
                        String description = descriptionInput.getText().toString();
                        final String token = auth.getToken();
                        MovieWithToken movieWithToken = new MovieWithToken(
                                id,
                                title,
                                description,
                                token
                        );
                        clearFields();
                        movieViewModel.update(movieWithToken, new Callback<Auth>() {
                            @Override
                            public void onResponse(Call<Auth> call, Response<Auth> response) {
                                movieViewModel.loadData(token);

                                Toast.makeText(getContext(), "update success", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onFailure(Call<Auth> call, Throwable t) {
                                Toast.makeText(getContext(), "update error", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        });
    }

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://" + LoginApi.BASE_IP + ":5001");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onMessage(String s) {
                List<StoredAuth> auths = database.authDao().getAuth();
                if (auths.size() > 0) {
                    StoredAuth auth = auths.get(0);
                    if (!auth.getUsername().isEmpty()) {
                        final String token = auth.getToken();
                        movieViewModel.loadData(token);
                    }
                }
            }

            @Override
            public void onOpen(ServerHandshake serverHandshake) {
            }

            @Override
            public void onClose(int i, String s, boolean b) {
            }

            @Override
            public void onError(Exception e) {
            }
        };
        mWebSocketClient.connect();
    }
}
