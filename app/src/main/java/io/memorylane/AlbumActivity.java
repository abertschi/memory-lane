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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.memorylane.model.Album;
import io.memorylane.model.AlbumModel;
import io.memorylane.model.Asset;
import io.memorylane.view.AlbumAdapter;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmResults;
import io.realm.Sort;

import static io.memorylane.Utils.getDateCurrentTimeZone;
import static io.memorylane.Utils.getDateDiff;

/**
 * Created by abertschi on 17/09/16.
 */
public class AlbumActivity extends AppCompatActivity {

    public static final int PERMISSIONS_REQUEST_TO_READ_CONTACTS = 100;
    public static final int PERMISSIONS_REQUEST_TO_CAMERA = 200;

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
                                        album.setId(System.nanoTime());
                                        album.setName(input.toString());

                                        RealmResults<Asset> result = bgRealm.where(Asset.class).findAllSorted("createDate", Sort.DESCENDING);

                                        List<Asset> assets;
                                        if(result.size() > 0) {
                                            Asset latestAsset = result.get(0);
                                            assets = getImageAssets(latestAsset.getCreateDate());
                                        } else {
                                            assets = getImageAssets(null);
                                        }

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
                                        RealmList<Asset> lastTrip;

                                        if(assets.size() > 0 && i > 1) {
                                            lastTrip = Utils.deepCopyToRealm(bgRealm, assets.subList(0, i));
                                            album.setAssets(lastTrip);
                                        } else {
                                            album.setStatDate(new Date(System.currentTimeMillis()));
                                        }

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
        updateDatabase();
        initRecycleView();
        initToolbar();
        initPermissions();
    }

    private void updateDatabase() {

        RealmResults<Asset> result = mRealm.where(Asset.class).findAllSorted("createDate", Sort.DESCENDING);

        List<Asset> assets;
        if(result.size() > 0) {
            Asset latestAsset = result.get(0);
            assets = getImageAssets(latestAsset.getCreateDate());

            if(assets != null && assets.size() >0){
                mRealm.beginTransaction();
                Album albums = mRealm.where(Album.class).isNull("endDate").findFirst();

                if(albums == null){
                    RealmResults<Album> results = mRealm.where(Album.class).findAllSorted("endDate", Sort.DESCENDING);
                    if(results.size() >0 ){
                        albums = results.get(0);
                    }
                }

                if(albums != null) {
                    albums.getAssets().addAll(0, Utils.deepCopyToRealm(mRealm, assets));
                }

                mRealm.commitTransaction();
            }
        }
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

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET}, PERMISSIONS_REQUEST_TO_READ_CONTACTS);
        } else {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    // TODO: add explanation
                }
                requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_TO_CAMERA);
            }
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
//        mRecycleView.addOnScrollListener(new HideScrollListener() {
//            @Override
//            public void onHide() {
//                mAppBarLayout.animate().translationY((float) (-mAppBarLayout.getHeight() * 1.5));
//                mAddAlbumButton.animate().translationY(mAddAlbumButton.getHeight() * 2);
//
//            }
//
//            @Override
//            public void onShow() {
//                mAppBarLayout.animate().translationY(0);
//                mAddAlbumButton.animate().translationY(0);
//            }
//        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_TO_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("TAG", "persmissions granted");
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

    private List<Asset> getImageAssets(Date lastEvent) {
        return getImageAssets(this, lastEvent);
    }

    private List<Asset> getImageAssets(Activity activity, Date lastEvent) {
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
                    assets.add(new Asset(true, path, createDate));
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
                    assets.add(new Asset(false, path, createDate));
                }
            }
        }

        assets = removeDuplicates(assets);

        Collections.sort(assets, new Comparator<Asset>() {
            @Override
            public int compare(Asset a1, Asset a2) {
                return -a1.getCreateDate().compareTo(a2.getCreateDate());
            }
        });


        if(lastEvent != null) {
            List<Asset> finalList = new ArrayList<>();
            for (Asset a : assets) {
                if (a.getCreateDate().after(lastEvent)) {
                    finalList.add(a);
                }
            }
            return finalList;
        } else {
            return assets;
        }
    }

    private ArrayList<Asset> removeDuplicates(ArrayList<Asset> assets){
        Set<Asset> s = new LinkedHashSet<>(assets);
        ArrayList<Asset> list = new ArrayList<>();
        list.addAll(s);
        return list;
    }
}
