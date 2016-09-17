package io.memorylane.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import io.memorylane.R;
import timber.log.Timber;

/**
 * Created by abertschi on 17/09/16.
 */
public class PictureInPictureView extends RelativeLayout implements View.OnTouchListener {

    private FrameLayout mFrontendView;
    private FrameLayout mBackgroundView;

    private ViewGroup mViewGroup;
    private static String TAG = "test";
    private int mDeltaX;
    private int mDeltaY;
    private float mScaleFactor = 1.f;
    private ScaleGestureDetector mScaleDetector;
    private boolean mMovingDirty;
    private boolean mScaleDirty;

    private int mLastMovableX;
    private int mLastMovableY;

    private ExitCameraGestureListener mExitCameraGestureListener;

    private GestureDetector mGestureDector;

    private AutoFitTextureView mTextureView1;
    private AutoFitTextureView mTextureView2;

    private boolean _switchView;

    private SwitchPictureInPictureListener mSwitchPaneListener;

    public PictureInPictureView(Context context) {
        super(context);
        initView();
    }

    public void setOnExitCameraGestureListener(ExitCameraGestureListener l) {
        mExitCameraGestureListener = l;
    }

    public PictureInPictureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PictureInPictureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        inflate(getContext(), R.layout.picture_in_picture_view, this);

        mFrontendView = (FrameLayout) findViewById(R.id.frontendView);
        mBackgroundView = (FrameLayout) findViewById(R.id.backgroundView);

        mTextureView1 = (AutoFitTextureView) findViewById(R.id.camera1);
        mTextureView2 = (AutoFitTextureView) findViewById(R.id.camera2);

        mViewGroup = this;

        mGestureDector = new GestureDetector(super.getContext(), new GestureListener());

        mScaleDetector = new ScaleGestureDetector(super.getContext(), new ScaleListener());
        mFrontendView.setOnTouchListener(this);
        mBackgroundView.setOnTouchListener(new ExitCameraGestureDetection());
    }

    public AutoFitTextureView[] getTextureViews() {
        return new AutoFitTextureView[]{mTextureView1, mTextureView2};
    }

    public void setSwitchPictureInPictureListener(SwitchPictureInPictureListener listener) {
        mSwitchPaneListener = listener;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();

            Log.d("Double Tap", "Tapped at: (" + x + "," + y + ")");

            if (mSwitchPaneListener != null) {
                mSwitchPaneListener.onSwitchPane();
            }

            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return mGestureDector.onTouchEvent(e);
    }

    @Override
    public boolean onTouch(final View view, MotionEvent motionEvent) {
        mScaleDetector.onTouchEvent(motionEvent);

        final int viewWidth = (int) (view.getWidth() * mScaleFactor);
        final int viewHeight = (int) (view.getHeight() * mScaleFactor);

        final int containerWidth = mViewGroup.getWidth();
        final int containerHeight = mViewGroup.getHeight();

        final int touchX = (int) motionEvent.getRawX();
        final int touchY = (int) motionEvent.getRawY();

        final int padding = 0;

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                RelativeLayout.LayoutParams lParms = (RelativeLayout.LayoutParams) view.getLayoutParams();
                mDeltaX = touchX - lParms.leftMargin;
                mDeltaY = touchY - lParms.topMargin;
                break;

            case MotionEvent.ACTION_UP:
                if (mMovingDirty) {
                    mMovingDirty = false;

                    final RelativeLayout.LayoutParams viewLayoutParams = (RelativeLayout.LayoutParams) view
                            .getLayoutParams();

                    int newYPos = 0;
                    int newXPos = 0;
                    double spacing = 0.3;

                    int spaceBelow = containerHeight - (viewLayoutParams.topMargin + viewHeight);
                    spaceBelow = spaceBelow < 0 ? 0 : spaceBelow;

                    if (viewLayoutParams.topMargin < spaceBelow * spacing) {
                        Log.i(TAG, "UP: " + viewLayoutParams.topMargin + " < " + spaceBelow * spacing);
                        newYPos = padding;
                    } else if (viewLayoutParams.topMargin * spacing > spaceBelow) {
                        Log.i(TAG, "BOT: " + viewLayoutParams.topMargin * spacing + " > " + spaceBelow);
                        newYPos = containerHeight - viewHeight - padding;
                    } else {
                        // center
                        newYPos = containerHeight / 2 - viewHeight / 2;
                    }

                    int spaceRight = containerWidth - (viewLayoutParams.leftMargin + viewWidth);
                    spaceRight = spaceRight < 0 ? 0 : spaceRight;

                    if (viewLayoutParams.leftMargin < spaceRight * spacing) {
                        newXPos = padding;
                    } else if (viewLayoutParams.leftMargin * spacing > spaceRight) {
                        newXPos = containerWidth - viewWidth - padding;
                    } else {
                        // center
                        newXPos = containerWidth / 2 - viewWidth / 2;
                    }

                    if (mScaleDirty) {
                        newYPos = mLastMovableY;
                        newXPos = mLastMovableX;
                        mScaleDirty = false;
                    }

                    view.animate().y(newYPos).x(newXPos).setDuration(300).start(); // TODO: should we really auto position at all?

                    if (!mScaleDirty) {
                        mLastMovableX = newXPos;
                        mLastMovableY = newYPos;
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mScaleDirty) break; // don't move while scale

                mMovingDirty = true;

                RelativeLayout.LayoutParams viewLayoutParams = (RelativeLayout.LayoutParams) view
                        .getLayoutParams();

                int xPos = touchX - mDeltaX;
                if (xPos < 0) { // dont move over left boundary
                    xPos = 0;
                } else if (viewWidth + xPos >= containerWidth) // dont move over right boundary
                {
                    xPos = containerWidth - viewWidth;
                }
                viewLayoutParams.leftMargin = xPos;

                int yPos = touchY - mDeltaY;
                if (yPos <= 0) {
                    yPos = 0;
                } else if (viewHeight + yPos >= containerHeight) {
                    yPos = containerHeight - viewHeight;
                }
                viewLayoutParams.topMargin = yPos;

                view.setLayoutParams(viewLayoutParams);
                break;
        }
        mViewGroup.invalidate();
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(0.5f, Math.min(mScaleFactor, 2.5f));
            mFrontendView.setScaleX(mScaleFactor);
            mFrontendView.setScaleY(mScaleFactor);
            mFrontendView.setPivotX(0);
            mFrontendView.setPivotY(0);

            Log.i(TAG, "Scaling view by " + String.valueOf(mScaleFactor));
            return true;
        }

        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mScaleDirty = true;
            return true;
        }
    }

    public interface SwitchPictureInPictureListener {
        void onSwitchPane();
    }

    public interface ExitCameraGestureListener {
        class GestureEvent {
            public GestureEvent(Direction direction) {
                this.direction = direction;
            }

            public enum Direction {
                UP,
                DOWN
            }

            private Direction direction;

            public Direction getDirection() {
                return this.direction;
            }
        }

        void onChange(GestureEvent e);
    }


    private class ExitCameraGestureDetection implements OnTouchListener {

        private int mLastTouchX;
        private int mLastTouchY;

        private int OFFSET = 400;

        private boolean isTouching = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            final int touchX = (int) event.getRawX();
            final int touchY = (int) event.getRawY();

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mLastTouchX = touchX;
                    mLastTouchY = touchY;
                    break;

                case MotionEvent.ACTION_UP:
                    // moving down
                    int move = touchY - mLastTouchY;

                    if (move > OFFSET) {
                        Log.e(TAG, "MOVING DOWN");
                        if (mExitCameraGestureListener != null) {
                            ExitCameraGestureListener.GestureEvent e =
                                    new ExitCameraGestureListener.GestureEvent(ExitCameraGestureListener.GestureEvent.Direction.DOWN);
                            mExitCameraGestureListener.onChange(e);
                        }
                    }
                    // moving up
                    else if (-move > OFFSET) {
                        Log.e(TAG, "MOVING UP");
                        if (mExitCameraGestureListener != null) {
                            ExitCameraGestureListener.GestureEvent e =
                                    new ExitCameraGestureListener.GestureEvent(ExitCameraGestureListener.GestureEvent.Direction.UP);
                            mExitCameraGestureListener.onChange(e);
                        }
                    }
                    break;

                case MotionEvent.ACTION_MOVE:

                    return false;
                default:
                    break;
            }
            return true;
        }
    }
}
