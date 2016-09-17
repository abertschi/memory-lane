package io.memorylane;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import io.memorylane.view.AutoFitTextureView;
import io.memorylane.view.PictureInPictureView;

/**
 * Created by abertschi on 17/09/16.
 */
public class CameraFragment extends Fragment implements PictureInPictureView.SwitchPictureInPictureListener {

    private PictureInPictureView mPictureInPictureView;

    private static final int REQUEST_VIDEO_PERMISSIONS = 1;

    private CameraDevice mCameraDevice1;
    private CameraDevice mCameraDevice2;

    private CameraCaptureSession mPreviewSession1;

    private AutoFitTextureView mTextureView1;
    private AutoFitTextureView mTextureView2;


    private MediaRecorder mMediaRecorder1;
    private MediaRecorder mMediaRecorder2;

    private static String TAG = "VIDEO_FRAGMENT";

    private static final String[] VIDEO_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private Size mPreviewSize1;
    private Size mPreviewSize2;

    private Handler mBackgroundHandler1;
    private HandlerThread mBackgroundThread1;
    //
    private Handler mBackgroundHandler2;
    private HandlerThread mBackgroundThread2;

    private CaptureRequest.Builder mPreviewBuilder1;
    private Size mVideoSize1;
    private Size mVideoSize2;
    private Integer mSensorOrientation;
    private CameraCaptureSession mPreviewSession2;
    private CaptureRequest.Builder mPreviewBuilder2;
    private ImageButton mRecordButton;

    private boolean _compatibleMode = false;

    private final String RECORDING_TAG_ON = "on";
    private final String RECORDING_TAG_OFF = "off";
    private String mNextVideoAbsolutePath;
    private Surface mRecorderSurface1;

    private Semaphore mCameraOpenCloseLock1 = new Semaphore(1);


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
        mPictureInPictureView.setSwitchPictureInPictureListener(this);

        mRecordButton = (ImageButton) view.findViewById(R.id.record_button);
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecordButton.getTag() != RECORDING_TAG_ON) {

                    mRecordButton.setImageResource(R.mipmap.button_rec);
                    mRecordButton.setTag(RECORDING_TAG_ON);
                    startRecordingVideo();
                    //mRecordButton.animate().alpha()
                    //mAddAlbumButton.animate().translationY(mAddAlbumButton.getHeight() * 2);

                } else {
                    stopRecordingVideo1();
                    mRecordButton.setImageResource(R.mipmap.button);
                    mRecordButton.setTag(RECORDING_TAG_OFF);
                }
            }
        });

        AutoFitTextureView[] textures = mPictureInPictureView.getTextureViews();
        mTextureView1 = textures[1];
        mTextureView2 = textures[0];

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)
                mPictureInPictureView.getLayoutParams();

        int height = getActivity().getResources().getDisplayMetrics().heightPixels;
        params.height = height;
        mPictureInPictureView.setLayoutParams(params);
    }

    public PictureInPictureView getPictureInPictureView() {
        return mPictureInPictureView;
    }

    // todo: switch views on double press
    @Override
    public void onPictureInPictureSwitched() {
        Log.i(TAG, "onPictureInPictureSwitched from fragment");
        _mCameraScreenConfiguration = !_mCameraScreenConfiguration;

//        closeCameras();
//        startCameras();
    }

    public void startCameras() {
        openCamera1(mTextureView1.getWidth(), mTextureView1.getWidth());
        openCamera2(mTextureView2.getHeight(), mTextureView2.getWidth());
    }

    private void resetCameras() {
        AlbumContentActivity.hackReset();
    }

    private enum Camera {
        CAMERA_1,
        CAMERA_2;
    }

    private enum CameraType {
        FRONT_CAMERA,
        BACK_CAMERA
    }

    private boolean _mCameraScreenConfiguration = true;


    private CameraDevice.StateCallback mStateCallBackCamera1 = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice camera) {
            Log.i(TAG, "StateCallback 1 camera opened");
            mCameraDevice1 = camera;
            startPreview(Camera.CAMERA_1);
            mCameraOpenCloseLock1.release();
            if (mTextureView1 != null) {
                //configureTransform(mTextureView1.getWidth(), mTextureView1.getHeight(), mTextureView1, mPreviewSize1);
            }
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            mCameraOpenCloseLock1.release();
            if (mCameraDevice1 != null) {
                mCameraDevice1.close();
                mCameraDevice1 = null;
            }
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            mCameraOpenCloseLock1.release();
            onDisconnected(camera);
            Log.e("LOG", "error camera 1");
            Activity activity = getActivity();
            if (activity != null) {
                activity.finish();
            }
        }
    };

    private CameraDevice.StateCallback mStateCallBackCamera2 = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            Log.i(TAG, "StateCallback 2 camera opened");
            mCameraDevice2 = camera;
            startPreview(Camera.CAMERA_2);

            if (mTextureView2 != null) {
                //configureTransform(mTextureView2.getWidth(), mTextureView2.getHeight(), mTextureView2, mPreviewSize2);
            }
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            mCameraDevice2.close();
            mCameraDevice2 = null;
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            onDisconnected(camera);
            Log.e("LOG", "error camera 2");
            Activity activity = getActivity();
            if (activity != null) {
                activity.finish();
            }

        }
    };


    private TextureView.SurfaceTextureListener mSurfaceTexture1Listener = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera1(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            configureTransform(width, height, mTextureView1, mPreviewSize1);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private TextureView.SurfaceTextureListener mSurfaceTexture2Listener = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera2(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            configureTransform(width, height, mTextureView2, mPreviewSize2);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };


    @Override
    public void onResume() {
        if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), VIDEO_PERMISSIONS, REQUEST_VIDEO_PERMISSIONS);
        }

        super.onResume();
        startBackgroundThread();
        mTextureView1.setSurfaceTextureListener(mSurfaceTexture1Listener);
        mTextureView2.setSurfaceTextureListener(mSurfaceTexture2Listener);
    }

    @Override
    public void onPause() {
        stopBackgroundThread();
        closeCameras();
        super.onPause();
    }

    private CameraType getConfiguredBackCamera() {
        return _mCameraScreenConfiguration ? CameraType.FRONT_CAMERA : CameraType.BACK_CAMERA;
    }

    private CameraType getConfiguredFrontCamera() {
        return _mCameraScreenConfiguration ? CameraType.BACK_CAMERA : CameraType.FRONT_CAMERA;
    }

    private void openCamera1(final int width, final int height) {
        if (Build.MODEL.equals("LG-D855")) {
            _openCamera(Camera.CAMERA_1, getConfiguredBackCamera(), width, height);
        }
    }

    private void openCamera2(int width, int height) {
        Log.i("TAG", "RUN CAMERA2 now");
        _openCamera(Camera.CAMERA_2, getConfiguredFrontCamera(), width, height);
    }

    private void _openCamera(Camera camera, CameraType cameraType, int width, int height) {
        try {
            CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);

            Log.d(TAG, "tryAcquire");
            try {
                if (!mCameraOpenCloseLock1.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                    //throw new RuntimeException("Time out waiting to lock camera opening.");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            String[] cameraIdList = manager.getCameraIdList();

            if (cameraIdList.length == 0) {
                throw new RuntimeException("Device has no camera");
            }
            for (String s : cameraIdList) {
                Log.i(TAG, "Camera found: " + s);
            }


            String cameraId;
            if (cameraType == CameraType.BACK_CAMERA) {
                cameraId = cameraIdList[0];
            } else if (cameraIdList.length > 1) {
                cameraId = cameraIdList[1];
            } else {
                Log.e(TAG, "No second camera found");
                return;
            }

            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            switch (camera) {
                case CAMERA_1: // front camera
                    mVideoSize1 = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
                    mPreviewSize1 = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), height, width, mVideoSize1);
                    mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                    //mTextureView1.setAspectRatio(mPreviewSize1.getHeight(), mPreviewSize1.getWidth());
                    mTextureView1.setAspectRatio(mTextureView1.getWidth(), mTextureView1.getHeight());
                    configureTransform(mTextureView1.getWidth(), mTextureView1.getHeight(), mTextureView1, mPreviewSize1);
                    mMediaRecorder1 = new MediaRecorder();
                    manager.openCamera(cameraId, mStateCallBackCamera1, null);

                    break;
                case CAMERA_2:
                    mVideoSize2 = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
                    mPreviewSize2 = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), height, width, mVideoSize2);
                    mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                    //mTextureView2.setAspectRatio(mPreviewSize2.getHeight(), mPreviewSize2.getWidth());
                    mTextureView2.setAspectRatio(mTextureView2.getWidth(), mTextureView2.getHeight());
                    configureTransform(mTextureView2.getWidth(), mTextureView2.getHeight(), mTextureView2, mPreviewSize2);
                    mMediaRecorder2 = new MediaRecorder();
                    manager.openCamera(cameraId, mStateCallBackCamera2, null);
                    break;
                default:
                    throw new RuntimeException("No matching camera");
            }

        } catch (CameraAccessException e) {
            Log.e(TAG, "Problems opening camera " + cameraType, e);
        }
    }

    /**
     * Configures the necessary {@link android.graphics.Matrix} transformation to `mTextureView`.
     * This method should not to be called until the camera preview size is determined in
     * openCamera, or until the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private void configureTransform(int viewWidth, int viewHeight, AutoFitTextureView textureView, Size previewSize) {
        textureView.updateViewSize(viewWidth, viewHeight);
        Activity activity = getActivity();
        if (null == textureView || null == previewSize || null == activity) {
            return;
        }
    }

    private void startPreview(final Camera camera) {
        try {
            switch (camera) {
                case CAMERA_1:
                    if (mCameraDevice1 == null || !mTextureView1.isAvailable()) {
                        return;
                    }
                    closePreviewSession1();
                    SurfaceTexture textureCamera1 = mTextureView1.getSurfaceTexture();
                    textureCamera1.setDefaultBufferSize(mTextureView1.getHeight(), mTextureView1.getWidth());
                    mPreviewBuilder1 = mCameraDevice1.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                    mTextureView1.setAspectRatio(mPreviewSize1.getHeight(), mPreviewSize1.getWidth());
                    mTextureView1.updateViewSize(mTextureView1.getHeight(), mTextureView1.getWidth());

                    Surface surfaceCamera1 = new Surface(textureCamera1);
                    mPreviewBuilder1.addTarget(surfaceCamera1);
                    mCameraDevice1.createCaptureSession(Arrays.asList(surfaceCamera1), new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(CameraCaptureSession session) {
                            mPreviewSession1 = session;
                            updatePreview(camera);
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession session) {
                            onPreviewUpdateFailed(session, camera);
                        }
                    }, mBackgroundHandler1);

                    break;
                case CAMERA_2:
                    if (mCameraDevice2 == null || !mTextureView2.isAvailable()) {
                        return;
                    }
                    closePreviewSession2();

                    SurfaceTexture textureCamera2 = mTextureView2.getSurfaceTexture();
                    textureCamera2.setDefaultBufferSize(mTextureView2.getWidth(), mTextureView2.getHeight());
                    mPreviewBuilder2 = mCameraDevice2.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
                    //mTextureView2.setAspectRatio(mPreviewSize2.getHeight(), mPreviewSize2.getWidth());
                    //mTextureView2.updateViewSize(mTextureView2.getHeight(), mTextureView2.getWidth());

                    Surface surfaceCamera2 = new Surface(textureCamera2);
                    mPreviewBuilder2.addTarget(surfaceCamera2);
                    mCameraDevice2.createCaptureSession(Arrays.asList(surfaceCamera2), new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(CameraCaptureSession session) {
                            mPreviewSession2 = session;
                            updatePreview(camera);
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession session) {
                            onPreviewUpdateFailed(session, camera);
                        }
                    }, mBackgroundHandler1);
                    break;
                default:
                    throw new RuntimeException("Unknown camera");
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void onPreviewUpdateFailed(CameraCaptureSession session, Camera camera) {
        Activity activity = getActivity();
        if (null != activity) {
            Toast.makeText(activity, "Failed starting preview for " + camera, Toast.LENGTH_SHORT).show();
        }
    }


    private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
    }

    private void updatePreview(Camera camera) {
        CaptureRequest.Builder previewBuilder;
        CameraCaptureSession previewSession;
        Handler backgroundHandler;

        if (camera == Camera.CAMERA_1) {
            if (mCameraDevice1 == null) {
                return;
            }
            previewBuilder = mPreviewBuilder1;
            previewSession = mPreviewSession1;
            backgroundHandler = mBackgroundHandler1;
        } else {
            if (mCameraDevice2 == null) {
                return;
            }
            previewBuilder = mPreviewBuilder2;
            previewSession = mPreviewSession2;
            backgroundHandler = mBackgroundHandler2;
        }

        setUpCaptureRequestBuilder(previewBuilder);
        HandlerThread thread = new HandlerThread(camera.toString());
        thread.start();
        try {
            previewSession.setRepeatingRequest(previewBuilder.build(), null, backgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG, "Problems in updating preview for camera " + camera.toString(), e);
        }
    }

    private void closePreviewSession1() {
        if (mPreviewSession1 != null) {
            mPreviewSession1.close();
            mPreviewSession1 = null;
        }
    }

    private void closePreviewSession2() {
        if (mPreviewSession2 != null) {
            mPreviewSession2.close();
            mPreviewSession2 = null;
        }
    }

    private void requestPermissions(String[] videoPermissions) {
        FragmentCompat.requestPermissions(this, videoPermissions, REQUEST_VIDEO_PERMISSIONS);
    }

    private boolean hasPermissionGranted(String[] permissions) {
        for (String p : permissions) {
            if (ActivityCompat.checkSelfPermission(getActivity(), p) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    /**
     * In this sample, we choose a video size with 3x4 aspect ratio. Also, we don't use sizes
     * larger than 1080p, since MediaRecorder cannot handle such a high-resolution video.
     *
     * @param choices The list of available sizes
     * @return The video size
     */
    private static Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
                return size;
            }
        }
        Log.e(TAG, "Couldn't find any suitable video size");
        return choices[choices.length - 1];
    }

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, chooses the smallest one whose
     * width and height are at least as large as the respective requested values, and whose aspect
     * ratio matches with the specified value.
     *
     * @param choices     The list of sizes that the camera supports for the intended output class
     * @param width       The minimum desired width
     * @param height      The minimum desired height
     * @param aspectRatio The aspect ratio
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    private static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<Size>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }


    private static class CompareSizesByArea implements java.util.Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }

    private void startBackgroundThread() {
        mBackgroundThread1 = new HandlerThread("CameraBackground1");
        mBackgroundThread1.start();
        mBackgroundHandler1 = new Handler(mBackgroundThread1.getLooper());

        mBackgroundThread2 = new HandlerThread("CameraBackground2");
        mBackgroundThread2.start();
        mBackgroundHandler2 = new Handler(mBackgroundThread2.getLooper());

    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread() {
        if (mBackgroundThread1 != null) {
            mBackgroundThread1.quitSafely();
            try {
                mBackgroundThread1.join();
                mBackgroundThread1 = null;
                mBackgroundHandler1 = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (mBackgroundThread2 != null) {
            mBackgroundThread2.quitSafely();
            try {
                mBackgroundThread2.join();
                mBackgroundThread2 = null;
                mBackgroundHandler2 = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void closeCameras() {
        closeCamera1();
        closeCamera2();
    }

    private void closeCamera1() {
        try {
            mCameraOpenCloseLock1.acquire();

            closePreviewSession1();
            if (null != mCameraDevice1) {
                mCameraDevice1.close();
                mCameraDevice1 = null;
            }

            if (null != mMediaRecorder1) {
                mMediaRecorder1.release();
                mMediaRecorder1 = null;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mCameraOpenCloseLock1.release();
        }
    }

    private void closeCamera2() {
        closePreviewSession2();
        if (null != mCameraDevice2) {
            mCameraDevice2.close();
            mCameraDevice2 = null;
        }

        if (null != mMediaRecorder2) {
            mMediaRecorder2.release();
            mMediaRecorder2 = null;
        }
    }

    private void setUpMediaRecorder1() {
        final Activity activity = getActivity();
        if (null == activity) {
            return;
        }
        //mMediaRecorder1.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder1.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder1.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        if (mNextVideoAbsolutePath == null || mNextVideoAbsolutePath.isEmpty()) {
            mNextVideoAbsolutePath = getVideoFilePath(getActivity());
        }
        mMediaRecorder1.setOutputFile(mNextVideoAbsolutePath);
        mMediaRecorder1.setVideoEncodingBitRate(1000000);
        mMediaRecorder1.setVideoFrameRate(30);
        mMediaRecorder1.setVideoSize(mVideoSize1.getWidth(), mVideoSize1.getHeight());
        mMediaRecorder1.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        //mMediaRecorder1.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        mMediaRecorder1.setOrientationHint(rotation);
        try {
            mMediaRecorder1.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Problems recording video with mediarecorder 1");
        }
    }

    private String getVideoFilePath(Context context) {
        File cameraDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString());
        cameraDirectory = new File(cameraDirectory, "memorylane");
        cameraDirectory.mkdirs();
        return cameraDirectory.toString() + "/memorylane_"
                + System.currentTimeMillis() + ".mp4";
    }

    private void startRecordingVideo() {
        if (null == mCameraDevice1 || !mTextureView1.isAvailable() || null == mPreviewSize1) {
            return;
        }
        try {
            closePreviewSession1();
            setUpMediaRecorder1();
            SurfaceTexture texture = mTextureView1.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize1.getWidth(), mPreviewSize1.getHeight());
            mPreviewBuilder1 = mCameraDevice1.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            List<Surface> surfaces = new ArrayList<>();

            // Set up Surface for the camera preview
            Surface previewSurface = new Surface(texture);
            surfaces.add(previewSurface);
            mPreviewBuilder1.addTarget(previewSurface);

            // Set up Surface for the MediaRecorder
            mRecorderSurface1 = mMediaRecorder1.getSurface();
            surfaces.add(mRecorderSurface1);
            mPreviewBuilder1.addTarget(mRecorderSurface1);

            // Start a capture session
            // Once the session starts, we can update the UI and start recording
            mCameraDevice1.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    mPreviewSession1 = cameraCaptureSession;
                    updatePreview(Camera.CAMERA_1);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            // UI
//                            mButtonVideo.setText(R.string.stop);
//                            mIsRecordingVideo = true;

                            // Start recording
                            mMediaRecorder1.start();
                        }
                    });
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Activity activity = getActivity();
                    if (null != activity) {
                        Toast.makeText(activity, "Failed", Toast.LENGTH_LONG).show();
                    }
                }
            }, mBackgroundHandler1);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void stopRecordingVideo1() {
        // UI
//        mIsRecordingVideo = false;
//        mButtonVideo.setText(R.string.record);
        // Stop recording
        mMediaRecorder1.stop();
        mMediaRecorder1.reset();

        Activity activity = getActivity();
        if (null != activity) {
            Toast.makeText(activity, "Video saved: " + mNextVideoAbsolutePath,
                    Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Video saved: " + mNextVideoAbsolutePath);
        }
        mNextVideoAbsolutePath = null;
        startPreview(Camera.CAMERA_1);
    }
}


