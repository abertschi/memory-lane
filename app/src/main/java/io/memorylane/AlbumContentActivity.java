package io.memorylane;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

import io.memorylane.view.CustomScrollView;
import io.memorylane.view.PictureInPictureView;

/**
 * Created by abertschi on 17/09/16.
 */
public class AlbumContentActivity extends AppCompatActivity implements PictureInPictureView.ExitCameraGestureListener {

    private AlbumBrowseFragment mBrowseFragment;
    private CameraFragment mCameraFragment;
    private CustomScrollView mScrollView;

    static AlbumContentActivity _instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        _instance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_content_activity);

        mScrollView = (CustomScrollView) findViewById(R.id.scroll1);
        mScrollView.setEnableScrolling(false);

        mBrowseFragment = AlbumBrowseFragment.newInstance();
        mCameraFragment = CameraFragment.newInstance();

        FragmentTransaction t = getFragmentManager().beginTransaction();
        t.add(R.id.container_1, mCameraFragment, "camera");
        t.add(R.id.container_2, mBrowseFragment, "browse");

        t.commit();
    }

    private void _hackReset() {
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(mBrowseFragment);
        ft.attach(mBrowseFragment);
        ft.commit();
    }

    public static void hackReset() {
        _instance._hackReset();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e("TAG", mCameraFragment.getPictureInPictureView().toString());
        mCameraFragment.getPictureInPictureView().setOnExitCameraGestureListener(this);
    }

    @Override
    public void onChange(GestureEvent e) {
        if (e.getDirection() == GestureEvent.Direction.UP) {
            mCameraFragment.closeCameras();
            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            mScrollView.scrollTo(0, this.getResources().getDisplayMetrics().heightPixels);
            finish();
        }
    }
}
