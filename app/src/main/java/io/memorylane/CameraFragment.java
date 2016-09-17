package io.memorylane;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import io.memorylane.R;
import io.memorylane.view.PictureInPictureView;

/**
 * Created by abertschi on 17/09/16.
 */
public class CameraFragment extends Fragment {

    private PictureInPictureView mPictureInPictureView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public static CameraFragment newInstance() {
        return new CameraFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.camera_fragment, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mPictureInPictureView = (PictureInPictureView) view.findViewById(R.id.picture_in_picture);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)
                mPictureInPictureView.getLayoutParams();


        Point displaySize = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getRealSize(displaySize);

        Rect windowSize = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(windowSize);


        int width = getActivity().getResources().getDisplayMetrics().widthPixels;
        int height = getActivity().getResources().getDisplayMetrics().heightPixels;


        //params.width = width;
        params.height = height;

        mPictureInPictureView.setLayoutParams(params);
    }

    public PictureInPictureView getPictureInPictureView() {
        return mPictureInPictureView;
    }


    @Override
    public void onResume() {
        super.onResume();
    }


}


