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
            initRecycleView(album.getAssets());
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
