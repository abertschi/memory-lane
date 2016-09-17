package io.memorylane;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import io.memorylane.model.AlbumModel;
import io.memorylane.view.AlbumAdapter;
import io.memorylane.view.HideScrollListener;

/**
 * Created by abertschi on 17/09/16.
 */
public class AlbumActivity extends AppCompatActivity {

    private RecyclerView mRecycleView;
    private AlbumAdapter mAlbumAdapter;
    private Toolbar mToolbar;
    private AppBarLayout mAppBarLayout;
    private FloatingActionButton mAddAlbumButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_activity);
        mAddAlbumButton = (FloatingActionButton) findViewById(R.id.fab);
        mAddAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDialog dialog = new MaterialDialog.Builder(AlbumActivity.this)
                        .title("Create new album")
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("Album Name", "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                // Do something
                            }
                        })
                        .build();
                dialog.show();

                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        dialog.dismiss();
//                        Snackbar.make(mRecycleView, "Replace with your own action", Snackbar.LENGTH_LONG)
//                                .setAction("Action", null).show();
                        return false;
                    }
                });
            }
        });

        initRecycleView();
        initToolbar();
    }

    private void initToolbar() {
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbarLayout);
        mToolbar = (Toolbar) findViewById(R.id.memorylane_toolbar);
        setSupportActionBar(mToolbar);
        setTitle(getString(R.string.app_name));
    }

    private void initRecycleView() {
        mRecycleView = (RecyclerView) findViewById(R.id.album_recycler_view);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        AlbumModel model = AlbumModel.createData();
        mAlbumAdapter = new AlbumAdapter(model, this);
        mRecycleView.setAdapter(mAlbumAdapter);
        mRecycleView.addOnScrollListener(new HideScrollListener() {
            @Override
            public void onHide() {
                mAppBarLayout.animate().translationY((float) (-mAppBarLayout.getHeight() * 1.5));
                mAddAlbumButton.animate().translationY(mAddAlbumButton.getHeight() * 2);

            }

            @Override
            public void onShow() {
                mAppBarLayout.animate().translationY(0);
                mAddAlbumButton.animate().translationY(0);
            }
        });
    }
}
