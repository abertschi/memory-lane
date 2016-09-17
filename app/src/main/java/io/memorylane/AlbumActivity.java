package io.memorylane;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import io.memorylane.model.AlbumModel;
import io.memorylane.view.AlbumAdapter;

/**
 * Created by abertschi on 17/09/16.
 */
public class AlbumActivity extends AppCompatActivity {

    private RecyclerView mRecycleView;
    private AlbumAdapter mAlbumAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_activity);

        initAlbums();
    }

    private void initAlbums() {
        mRecycleView = (RecyclerView) findViewById(R.id.album_recycler_view);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        AlbumModel model = AlbumModel.createData();
        mAlbumAdapter = new AlbumAdapter(model);
        mRecycleView.setAdapter(mAlbumAdapter);
    }
}
