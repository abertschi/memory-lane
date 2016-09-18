package io.memorylane;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;

import io.memorylane.cloud.CloudController;
import io.memorylane.cloud.MemoryLaneAsyncTask;
import io.memorylane.model.Album;
import io.memorylane.model.Asset;
import io.memorylane.model.AssetsModel;
import io.memorylane.videomaker.VideomakerApi;
import io.memorylane.videomaker.VideomakerRequest;
import io.memorylane.videomaker.VideomakerRequestFactory;
import io.memorylane.videomaker.VideomakerResponse;
import io.memorylane.videomaker.VideomakerResponseResult;
import io.memorylane.view.AssetGridAdapter;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MovieCreatorActivity extends AppCompatActivity implements Callback<List<VideomakerResponse>> {

    Realm mRealm;
    private RecyclerView mRecycleView;
    private AssetGridAdapter mGridAdapter;
    private Album mAlbum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_creator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        mRealm = Realm.getDefaultInstance();


        Long albumId = null;
        if (getIntent().getExtras() != null) {
            albumId = getIntent().getExtras().getLong("AlbumId");
            mAlbum = Utils.deepCopy(mRealm.where(Album.class).equalTo("id", albumId).findFirst());

            CollapsingToolbarLayout ctl = (CollapsingToolbarLayout) findViewById(R.id.main_collapsing);
            ctl.setTitle(mAlbum.getName());



            toolbar.setTitle(mAlbum.getName());

            initRecycleView(mAlbum.getAssets());
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Asset> assets = mAlbum.getAssets().subList(0, 2);

                Asset[] assetsArray = new Asset[assets.size()];
                assets.toArray(assetsArray);
                new MemoryLaneAsyncTask().execute(assetsArray);

                sendVideomakerRequest();
            }
        });
    }


    private void sendVideomakerRequest() {
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
        Log.i("response ok", "");

        VideomakerResponseResult result = response.body().get(0).result;
        Log.i("video url", result.export);
        Log.i("video preview url", result.preview);
        Log.i("video thumbnail url", result.thumbnail);
        Log.i("video duration", String.valueOf(result.duration));
    }

    @Override
    public void onFailure(Call<List<VideomakerResponse>> call, Throwable t) {
        Log.i("failure", String.valueOf(t));
    }


    private void initRecycleView(List<Asset> assets) {
        mRecycleView = (RecyclerView) findViewById(R.id.assets_grid);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.setNestedScrollingEnabled(false);
        AssetsModel model = new AssetsModel(assets);
        mGridAdapter = new AssetGridAdapter(this, model);
        mRecycleView.setAdapter(mGridAdapter);
    }

}
