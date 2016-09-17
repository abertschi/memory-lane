package io.memorylane;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import io.memorylane.view.PictureInPictureView;

/**
 * Created by abertschi on 17/09/16.
 */
public class AlbumContentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_content_activity);

        AlbumBrowseFragment browseFragment = AlbumBrowseFragment.newInstance();
        CameraFragment cameraFragment = CameraFragment.newInstance();
//
//        FragmentTransaction t = getFragmentManager().beginTransaction();
//        t.add(R.id.album_browse_fragment, browseFragment, "browse");
//        t.add(R.id.camera_fragment, cameraFragment, "browse");
//
//        t.commit();
    }

}
