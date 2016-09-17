package io.memorylane;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import io.memorylane.model.Album;
import io.memorylane.videomaker.SxmlFactory;
import io.memorylane.videomaker.VideomakerApi;
import io.memorylane.videomaker.VideomakerRequest;
import io.memorylane.videomaker.VideomakerRequestFactory;
import io.memorylane.videomaker.VideomakerResponse;
import io.memorylane.videomaker.VideomakerTask;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieCreatorActivity extends AppCompatActivity implements Callback<List<VideomakerResponse>> {

    Realm mRealm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_creator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRealm = Realm.getDefaultInstance();

        Long albumId = null;
        Album album;
        if(getIntent().getExtras() != null) {
            albumId = getIntent().getExtras().getLong("AlbumId");
            album = mRealm.where(Album.class).equalTo("id", albumId).findFirst();
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendRequest();
            }
        });
    }

    private void sendRequest() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://dragon.stupeflix.com")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        // prepare call in Retrofit 2.0
        VideomakerApi videomakerApi = retrofit.create(VideomakerApi.class);

        VideomakerRequest request = VideomakerRequestFactory.produce();
        Call<List<VideomakerResponse>> call = videomakerApi.post(request);

        //asynchronous call
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<List<VideomakerResponse>> call, Response<List<VideomakerResponse>> response) {
        Log.i("response ok", response.body().toString());
    }

    @Override
    public void onFailure(Call<List<VideomakerResponse>> call, Throwable t) {
        Log.i("failure", String.valueOf(t));
    }

}
