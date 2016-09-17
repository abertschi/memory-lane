package io.memorylane;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import io.memorylane.view.PictureInPictureView;

/**
 * Created by abertschi on 17/09/16.
 */
public class CameraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);
        PictureInPictureView pictureInPictureView = (PictureInPictureView) findViewById(R.id.picture_in_picture);


    }

}
