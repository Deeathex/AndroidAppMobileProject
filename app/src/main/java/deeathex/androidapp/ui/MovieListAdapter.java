package deeathex.androidapp.ui;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import deeathex.androidapp.R;
import deeathex.androidapp.model.Movie;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder> {
    private Context mContext;
    private List<Movie> mMovies;
    private OnItemClickListener listener;

    public MovieListAdapter(Context context, List<Movie> movies, OnItemClickListener listener) {
        this.mContext = context;
        this.mMovies = movies;
        this.listener = listener;
    }

    public MovieListAdapter(Activity activity) {
        super();
        mMovies = new ArrayList<>();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_layout, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, final int position) {
        final Movie movie = mMovies.get(position);
        holder.textView.setText(movie.toString());
        holder.textView.setPadding(10, 10, 10, 10);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.onItemClick(movie);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        MovieViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_view);
        }
    }
}
