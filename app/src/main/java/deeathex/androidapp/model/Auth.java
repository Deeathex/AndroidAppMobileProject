package deeathex.androidapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Auth {
    @SerializedName("auth")
    @Expose
    private boolean auth;

    @SerializedName("token")
    @Expose
    private String token;

    public String toString() {
        return token;
    }

}
