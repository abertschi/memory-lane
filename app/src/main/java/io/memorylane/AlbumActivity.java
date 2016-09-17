package io.memorylane;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.memorylane.model.Album;
import io.memorylane.model.AlbumModel;
import io.memorylane.model.Asset;
import io.memorylane.view.AlbumAdapter;
import io.memorylane.view.HideScrollListener;
import io.realm.Realm;
import io.realm.RealmConfiguration;

import static io.memorylane.Utils.getDateCurrentTimeZone;
import static io.memorylane.Utils.getDateDiff;

/**
 * Created by abertschi on 17/09/16.
 */
public class AlbumActivity extends AppCompatActivity {

    public static final int PERMISSIONS_REQUEST_TO_READ_CONTACTS = 100;

    private RecyclerView mRecycleView;
    private AlbumAdapter mAlbumAdapter;
    private Toolbar mToolbar;
    private AppBarLayout mAppBarLayout;
    private FloatingActionButton mAddAlbumButton;
    private Realm mRealm;

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
                            public void onInput(final MaterialDialog dialog, final CharSequence input) {

                                mRealm.executeTransactionAsync(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm bgRealm) {
                                        Album album = bgRealm.createObject(Album.class);

                                        // add "latest" trip to just created trip
                                        album.setName(input.toString());

                                        List<Asset> assets = getImageAssets();

                                        int i = 1;
                                        for (; i < assets.size(); i++) {
                                            long timeDiff = getDateDiff(assets.get(i).getCreateDate(), assets.get(i - 1).getCreateDate(), TimeUnit.HOURS);
                                            if(timeDiff > 36){
                                                break;
                                            }
                                        }
                                        // limit size of initial list
                                        // TODO: remove it
                                        i = i < 150 ? i : 150;
                                        List<Asset> lastTrip = getImageAssets().subList(0, i);
                                        album.setStatDate(lastTrip.get(0).getCreateDate());
                                        album.setEndDate(lastTrip.get(lastTrip.size()-1).getCreateDate());
                                        album.getAssets().addAll(lastTrip);
                                    }
                                }, new Realm.Transaction.OnSuccess() {
                                    @Override
                                    public void onSuccess() {
                                        mAlbumAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        })
                        .build();
                dialog.show();

                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        dialog.dismiss();
                        return false;
                    }
                });
            }
        });

        initDatabase(this);
        initRecycleView();
        initToolbar();
        initPermissions();
    }

    private void initDatabase(Context context) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfig);
        mRealm = Realm.getDefaultInstance();
    }

    private void initPermissions() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // TODO: add explanation
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_TO_READ_CONTACTS);
        } else {
            getImageAssets();
        }
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
        mAlbumAdapter = new AlbumAdapter(new AlbumModel(), this);
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_TO_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getImageAssets();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private List<Asset> getImageAssets() {
        return getImageAssets(this);
    }

    private List<Asset> getImageAssets(Activity activity) {
        ArrayList<Asset> assets = new ArrayList<>();

        try (Cursor cursor = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.MediaColumns.DATA, MediaStore.Images.Media.DATE_ADDED},
                null,
                null,
                MediaStore.Images.Media.DATE_ADDED + " DESC")) {
            if (cursor != null) {

                int data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                int addDate = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED);
                while (cursor.moveToNext()) {
                    Date createDate = getDateCurrentTimeZone(cursor.getLong(addDate));
                    String path = cursor.getString(data);
                    assets.add(new Asset(path, createDate));
                }
            }
        }

        try(Cursor cursor = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.DATE_ADDED},
                null,
                null,
                null)){

            if (cursor != null) {
                int data = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATA);
                int addDate = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATE_ADDED);
                while (cursor.moveToNext()) {
                    Date createDate = getDateCurrentTimeZone(cursor.getLong(addDate));
                    String path = cursor.getString(data);
                    assets.add(new Asset(path, createDate));
                }
            }
        }


        Collections.sort(assets, new Comparator<Asset>() {
            @Override
            public int compare(Asset a1, Asset a2) {
                return -a1.getCreateDate().compareTo(a2.getCreateDate());
            }
        });

        return assets;
    }
}
