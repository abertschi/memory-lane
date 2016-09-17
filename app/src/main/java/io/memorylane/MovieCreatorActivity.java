package io.memorylane;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

import io.memorylane.cloud.CloudController;
import io.memorylane.model.Album;
import io.memorylane.model.Asset;
import io.memorylane.model.AssetsModel;
import io.memorylane.view.AssetGridAdapter;
import io.realm.Realm;
import io.realm.Sort;

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
            mAlbum = mRealm.where(Album.class).equalTo("id", albumId).findFirst();
            initRecycleView(mAlbum.getAssets());
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CloudController cloudController = new CloudController();
                cloudController.putImage(mAlbum.getAssets().get(0));
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
