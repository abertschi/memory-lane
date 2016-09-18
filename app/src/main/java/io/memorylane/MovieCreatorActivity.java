package io.memorylane;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
    private ImageView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_creator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mView = (ImageView) findViewById(R.id.main_backdrop);

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
                List<Asset> assets = mAlbum.getAssets();

                Asset[] assetsArray = new Asset[assets.size()];
                assets.toArray(assetsArray);
                new MemoryLaneAsyncTask(MovieCreatorActivity.this).execute(assetsArray);
            }
        });

        FloatingActionButton up = (FloatingActionButton) findViewById(R.id.back_to_camera);
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.startActivity(MovieCreatorActivity.this, new Intent(v.getContext(), AlbumContentActivity.class), null);

            }
        });

        Random randomizer = new Random();
        Glide.with(this)
                .load(mAlbum.getAssets().get(randomizer.nextInt(mAlbum.getAssets().size())).getPath())
                .into(mView);
        if(mAlbum.getAssets().size() > 0) {
            Random randomizer = new Random();
            Glide.with(this)
                    .load(mAlbum.getAssets().get(randomizer.nextInt(mAlbum.getAssets().size())).getPath())
                    .into(mView);
        }
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
