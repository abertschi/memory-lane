package io.memorylane;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
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


public class MovieCreatorActivity extends AppCompatActivity {

    Realm mRealm;
    private RecyclerView mRecycleView;
    private AssetGridAdapter mGridAdapter;
    private Album mAlbum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_creator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRealm = Realm.getDefaultInstance();

        Long albumId = null;
        if(getIntent().getExtras() != null) {
            albumId = getIntent().getExtras().getLong("AlbumId");
            mAlbum = Utils.deepCopy(mRealm.where(Album.class).equalTo("id", albumId).findFirst());

            initRecycleView(mAlbum.getAssets());
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Asset> assets = mAlbum.getAssets().subList(0, 10);

                Asset[] assetsArray = new Asset[assets.size()];
                assets.toArray(assetsArray);
                new MemoryLaneAsyncTask().execute(assetsArray);
            }
        });
    }

    private void initRecycleView(List<Asset> assets) {
        mRecycleView = (RecyclerView) findViewById(R.id.assets_grid);
        mRecycleView.setLayoutManager(new GridLayoutManager(this, 2));
        AssetsModel model = new AssetsModel(assets);
        mGridAdapter = new AssetGridAdapter(this, model);
        mRecycleView.setAdapter(mGridAdapter);
    }

}
