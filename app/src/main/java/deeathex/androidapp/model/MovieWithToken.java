package deeathex.androidapp.model;

import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieWithToken {
    @SerializedName("id")
    @Expose
    @PrimaryKey
    @NonNull
    private Integer id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("token")
    @Expose
    private String token;

    public MovieWithToken(Integer id, String title, String description, String token) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.token = token;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "" + id + ", " + title + ", " + description;
    }
}
