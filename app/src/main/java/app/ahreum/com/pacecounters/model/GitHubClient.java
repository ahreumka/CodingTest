package app.ahreum.com.pacecounters.model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by P16086 on 2017-04-03.
 */

public interface GitHubClient {
    @GET("/users/{user}/repos")
    Call<List<GitHubRepo>> reposForUser(@Path("user") String user);
}
