package io.memorylane;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import io.memorylane.R;
import io.memorylane.view.PictureInPictureView;

/**
 * Created by abertschi on 17/09/16.
 */
public class CameraFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PictureInPictureView pictureInPictureView = (PictureInPictureView) inflater.inflate(R.layout.picture_in_picture_view, container, false);

        return pictureInPictureView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public static CameraFragment newInstance() {
        return new CameraFragment();
    }
}
