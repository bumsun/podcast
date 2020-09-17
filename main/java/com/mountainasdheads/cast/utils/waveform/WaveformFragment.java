package com.mountainasdheads.cast.utils.waveform;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.mountainasdheads.cast.R;
import com.mountainasdheads.cast.ui.fragments.BaseNavigationFragment;
import com.mountainasdheads.cast.ui.fragments.EditPodcastFragment;
import com.mountainasdheads.cast.utils.PathUtils;
import com.mountainasdheads.cast.utils.PodcastHeap;
import com.mountainasdheads.cast.utils.waveform.soundfile.CheapSoundFile;
import com.mountainasdheads.cast.utils.waveform.view.MarkerView;
import com.mountainasdheads.cast.utils.waveform.view.WaveformView;

import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public abstract class WaveformFragment extends BaseNavigationFragment implements MarkerView.MarkerListener, WaveformView.WaveformListener {

    public static final String TAG = "WaveformFragment";
    protected View myView;
    protected long mLoadingLastUpdateTime;
    protected boolean mLoadingKeepGoing;
    protected ProgressDialog mProgressDialog;
    protected CheapSoundFile mSoundFile;
    protected File mFile;
    protected String mFilename;
    protected WaveformView mWaveformView;
    protected MarkerView mStartMarker;
    protected MarkerView mEndMarker;



    protected boolean mKeyDown;
    protected String mCaption = "";
    protected int mWidth;
    protected int mMaxPos;
    protected int mStartPos;
    protected int mEndPos;
    protected boolean mStartVisible;
    protected boolean mEndVisible;
    protected int mLastDisplayedStartPos;
    protected int mLastDisplayedEndPos;
    protected int mOffset;
    protected int mOffsetGoal;
    protected int mFlingVelocity;
    protected int mPlayStartMsec;
    protected int mPlayStartOffset;
    protected int mPlayEndMsec;
    protected Handler mHandler;
    protected boolean mIsPlaying;
    protected MediaPlayer mPlayer;
    protected boolean mTouchDragging;
    protected float mTouchStart;
    protected int mTouchInitialOffset;
    protected int mTouchInitialStartPos;
    protected int mTouchInitialEndPos;
    protected long mWaveformTouchStartMsec;
    protected float mDensity;
    protected int mMarkerLeftInset;
    protected int mMarkerRightInset;
    protected int mMarkerTopOffset;
    protected int mMarkerBottomOffset;
    private MediaPlayer backgroundMusic;

    Boolean enableMusic = false;
    Boolean enableFadeIn = false;
    Boolean enableFadeOut = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_edit_podcast, container, false);
        myView = view;
        loadGui(view);
        if (mSoundFile == null) {
            loadFromFile();
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    WaveformFragment.this.finishOpeningSoundFile();
                }
            });
        }
        return view;
    }


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);

        mPlayer = null;
        mIsPlaying = false;

        try {
            mFilename = PathUtils.getPath(getContext(), Uri.parse(PodcastHeap.INSTANCE.getAudioUri().getPath()));
            Log.d("mFilename", "mFilename = " + mFilename);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mSoundFile = null;
        mKeyDown = false;

        mHandler = new Handler();

        mHandler.postDelayed(mTimerRunnable, 100);
    }

    @Override
    public void onDestroy() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }

        mSoundFile = null;
        mWaveformView = null;
        super.onDestroy();
    }

    //
    // WaveformListener
    //

    /**
     * Every time we get a message that our waveform drew, see if we need to
     * animate and trigger another redraw.
     */
    public void waveformDraw() {
        mWidth = mWaveformView.getMeasuredWidth();
        if (mOffsetGoal != mOffset && !mKeyDown)
            updateDisplay();
        else if (mIsPlaying) {
            updateDisplay();
        } else if (mFlingVelocity != 0) {
            updateDisplay();
        }
    }

    public void waveformTouchStart(float x) {
        mTouchDragging = true;
        mTouchStart = x;
        mTouchInitialOffset = mOffset;
        mFlingVelocity = 0;
        mWaveformTouchStartMsec = System.currentTimeMillis();
    }

    public void waveformTouchMove(float x) {
        mOffset = trap((int) (mTouchInitialOffset + (mTouchStart - x)));
        updateDisplay();
    }
    protected void onTuchedWaveForm(){

    }
    public void waveformTouchEnd() {
        mTouchDragging = false;
        mOffsetGoal = mOffset;

        long elapsedMsec = System.currentTimeMillis() - mWaveformTouchStartMsec;
        if (elapsedMsec < 300) {
            if (mIsPlaying) {
                int seekMsec = mWaveformView.pixelsToMillisecs((int) (mTouchStart + mOffset));
                if (seekMsec >= mPlayStartMsec && seekMsec < mPlayEndMsec) {
                    mPlayer.seekTo(seekMsec - mPlayStartOffset);
                } else {
                    handlePause();
                }
            } else {
                onPlay((int) (mTouchStart + mOffset));
            }
        }
        onTuchedWaveForm();
    }

    public void waveformFling(float vx) {
        mTouchDragging = false;
        mOffsetGoal = mOffset;
        mFlingVelocity = (int) (-vx);
        updateDisplay();
    }

    public void waveformZoomIn() {
        mWaveformView.zoomIn();
        mStartPos = mWaveformView.getStart();
        mEndPos = mWaveformView.getEnd();
        mMaxPos = mWaveformView.maxPos();
        mOffset = mWaveformView.getOffset();
        mOffsetGoal = mOffset;
        enableZoomButtons();
        updateDisplay();
    }

    public void waveformZoomOut() {
        mWaveformView.zoomOut();
        mStartPos = mWaveformView.getStart();
        mEndPos = mWaveformView.getEnd();
        mMaxPos = mWaveformView.maxPos();
        mOffset = mWaveformView.getOffset();
        mOffsetGoal = mOffset;
        enableZoomButtons();
        updateDisplay();
    }

    //
    // MarkerListener
    //

    public void markerDraw() {
    }

    public void markerTouchStart(MarkerView marker, float x) {
        mTouchDragging = true;
        mTouchStart = x;
        mTouchInitialStartPos = mStartPos;
        mTouchInitialEndPos = mEndPos;
    }

    public void markerTouchMove(MarkerView marker, float x) {
        float delta = x - mTouchStart;

        if (marker == mStartMarker) {
            mStartPos = trap((int) (mTouchInitialStartPos + delta));
            mEndPos = trap((int) (mTouchInitialEndPos + delta));
        } else {
            mEndPos = trap((int) (mTouchInitialEndPos + delta));
            if (mEndPos < mStartPos)
                mEndPos = mStartPos;
        }

        updateDisplay();
    }

    public void markerTouchEnd(MarkerView marker) {
        mTouchDragging = false;
        if (marker == mStartMarker) {
            setOffsetGoalStart();
        } else {
            setOffsetGoalEnd();
        }
    }

    public void markerLeft(MarkerView marker, int velocity) {
        mKeyDown = true;

        if (marker == mStartMarker) {
            int saveStart = mStartPos;
            mStartPos = trap(mStartPos - velocity);
            mEndPos = trap(mEndPos - (saveStart - mStartPos));
            setOffsetGoalStart();
        }

        if (marker == mEndMarker) {
            if (mEndPos == mStartPos) {
                mStartPos = trap(mStartPos - velocity);
                mEndPos = mStartPos;
            } else {
                mEndPos = trap(mEndPos - velocity);
            }

            setOffsetGoalEnd();
        }

        updateDisplay();
    }

    public void markerRight(MarkerView marker, int velocity) {
        mKeyDown = true;

        if (marker == mStartMarker) {
            int saveStart = mStartPos;
            mStartPos += velocity;
            if (mStartPos > mMaxPos)
                mStartPos = mMaxPos;
            mEndPos += (mStartPos - saveStart);
            if (mEndPos > mMaxPos)
                mEndPos = mMaxPos;

            setOffsetGoalStart();
        }

        if (marker == mEndMarker) {
            mEndPos += velocity;
            if (mEndPos > mMaxPos)
                mEndPos = mMaxPos;

            setOffsetGoalEnd();
        }

        updateDisplay();
    }

    public void markerEnter(MarkerView marker) {
    }

    public void markerKeyUp() {
        mKeyDown = false;
        updateDisplay();
    }

    public void markerFocus(MarkerView marker) {
        mKeyDown = false;
        if (marker == mStartMarker) {
            setOffsetGoalStartNoUpdate();
        } else {
            setOffsetGoalEndNoUpdate();
        }

        // Delay updaing the display because if this focus was in
        // response to a touch event, we want to receive the touch
        // event too before updating the display.
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                WaveformFragment.this.updateDisplay();
            }
        }, 100);
    }

    //
    // Internal methods
    //

    protected void loadGui(View view) {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mDensity = metrics.density;

        mMarkerLeftInset = (int) (48 * mDensity);
        mMarkerRightInset = (int) (48 * mDensity);
        mMarkerTopOffset = (int) (24 * mDensity);
        mMarkerBottomOffset = (int) (10 * mDensity);





        enableDisableButtons();

        mWaveformView = (WaveformView) view.findViewById(R.id.waveform);
        mWaveformView.setListener(this);
        mWaveformView.setSegments(getSegments());


        mMaxPos = 0;
        mLastDisplayedStartPos = -1;
        mLastDisplayedEndPos = -1;

        if (mSoundFile != null && !mWaveformView.hasSoundFile()) {
            mWaveformView.setSoundFile(mSoundFile);
            mWaveformView.recomputeHeights(mDensity);
            mMaxPos = mWaveformView.maxPos();
        }

        mStartMarker = (MarkerView) view.findViewById(R.id.startmarker);
        mStartMarker.setListener(this);
        mStartMarker.setImageAlpha(255);
        mStartMarker.setFocusable(true);
        mStartMarker.setFocusableInTouchMode(true);
        mStartVisible = true;

        mEndMarker = (MarkerView) view.findViewById(R.id.endmarker);
        mEndMarker.setListener(this);
        mEndMarker.setImageAlpha(255);
        mEndMarker.setFocusable(true);
        mEndMarker.setFocusableInTouchMode(true);
        mEndVisible = true;

        updateDisplay();
    }


    protected void resetCut() {
        mStartPos = 0;
        mEndPos = mMaxPos;
        updateDisplay();
        setOffsetGoalStart();
    }

    protected void resetEffects() {
        enableFadeOut = false;
        enableFadeIn = false;
        enableMusic = false;
        resetCut();
    }

    protected void loadFromFile() {
        try {
            Log.d("mFile", "mFile = " + PodcastHeap.INSTANCE.getAudioUri().getPath());
            Log.d("mFile", "mFile 2 = " + PathUtils.getPath(getContext(), PodcastHeap.INSTANCE.getAudioUri()));
            mFile = new File(PathUtils.getPath(getContext(), PodcastHeap.INSTANCE.getAudioUri()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mLoadingLastUpdateTime = System.currentTimeMillis();
        mLoadingKeepGoing = true;
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
       // mProgressDialog.setTitle(R.string.progress_dialog_loading);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mLoadingKeepGoing = false;
            }
        });
        mProgressDialog.show();

        final CheapSoundFile.ProgressListener listener = new CheapSoundFile.ProgressListener() {
            @Override
            public boolean reportProgress(double fractionComplete) {
                long now = System.currentTimeMillis();
                if (now - mLoadingLastUpdateTime > 100) {
                    mProgressDialog.setProgress(
                            (int) (mProgressDialog.getMax() * fractionComplete));
                    mLoadingLastUpdateTime = now;
                }
                return mLoadingKeepGoing;
            }
        };

        // Create the MediaPlayer in a background thread
        new Thread() {
            public void run() {
                try {
                    MediaPlayer player = new MediaPlayer();

                    player.setDataSource(mFile.getAbsolutePath().replace("/document/raw:",""));
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    player.prepare();
                    mPlayer = player;
                } catch (final java.io.IOException e) {
                    Log.e(TAG, "Error while creating media player", e);
                }
            }
        }.start();

        // Load the sound file in a background thread
        new Thread() {
            public void run() {
                try {

                    mSoundFile = CheapSoundFile.create(mFile.getAbsolutePath().replace("/document/raw:",""), listener);
                } catch (final Exception e) {
                    Log.e(TAG, "Error while loading sound file", e);
                    mProgressDialog.dismiss();
                    return;
                }
                if (mLoadingKeepGoing) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            finishOpeningSoundFile();
                        }
                    });
                }
            }
        }.start();
    }

    protected void finishOpeningSoundFile() {
        mWaveformView.setSoundFile(mSoundFile);
        mWaveformView.recomputeHeights(mDensity);

        mMaxPos = mWaveformView.maxPos();
        mLastDisplayedStartPos = -1;
        mLastDisplayedEndPos = -1;

        mTouchDragging = false;

        mOffset = 0;
        mOffsetGoal = 0;
        mFlingVelocity = 0;
        resetPositions();

        mCaption = mSoundFile.getFiletype() + ", " +
                mSoundFile.getSampleRate() + " Hz, " +
                mSoundFile.getAvgBitrateKbps() + " kbps, " +
                formatTime(mMaxPos) + " " + getResources().getString(R.string.time_seconds);
        mProgressDialog.dismiss();
        updateDisplay();
    }

    protected synchronized void updateDisplay() {
        if (mIsPlaying) {
            int now = mPlayer.getCurrentPosition() + mPlayStartOffset;
            int frames = mWaveformView.millisecsToPixels(now);
            mWaveformView.setPlayback(frames);
            setOffsetGoalNoUpdate(frames - mWidth / 2);
            if (now >= mPlayEndMsec) {
                handlePause();
            }
        }

        if (!mTouchDragging) {
            int offsetDelta;

            if (mFlingVelocity != 0) {
                float saveVel = mFlingVelocity;

                offsetDelta = mFlingVelocity / 30;
                if (mFlingVelocity > 80) {
                    mFlingVelocity -= 80;
                } else if (mFlingVelocity < -80) {
                    mFlingVelocity += 80;
                } else {
                    mFlingVelocity = 0;
                }

                mOffset += offsetDelta;

                if (mOffset + mWidth / 2 > mMaxPos) {
                    mOffset = mMaxPos - mWidth / 2;
                    mFlingVelocity = 0;
                }
                if (mOffset < 0) {
                    mOffset = 0;
                    mFlingVelocity = 0;
                }
                mOffsetGoal = mOffset;
            } else {
                offsetDelta = mOffsetGoal - mOffset;

                if (offsetDelta > 10)
                    offsetDelta = offsetDelta / 10;
                else if (offsetDelta > 0)
                    offsetDelta = 1;
                else if (offsetDelta < -10)
                    offsetDelta = offsetDelta / 10;
                else if (offsetDelta < 0)
                    offsetDelta = -1;
                else
                    offsetDelta = 0;

                mOffset += offsetDelta;
            }
        }

        mWaveformView.setParameters(mStartPos, mEndPos, mOffset);
        mWaveformView.invalidate();

        mStartMarker.setContentDescription(getResources().getText(R.string.start_marker) + " " + formatTime(mStartPos));
        mEndMarker.setContentDescription(getResources().getText(R.string.end_marker) + " " + formatTime(mEndPos));

        int startX = mStartPos - mOffset - mMarkerLeftInset;
        if (startX + mStartMarker.getWidth() >= 0) {
            if (!mStartVisible) {
                // Delay this to avoid flicker
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mStartVisible = true;
                        mStartMarker.setImageAlpha(255);
                    }
                }, 0);
            }
        } else {
            if (mStartVisible) {
                mStartMarker.setImageAlpha(0);
                mStartVisible = false;
            }
            startX = 0;
        }

        int endX = mEndPos - mOffset - mEndMarker.getWidth() + mMarkerRightInset;
        if (endX + mEndMarker.getWidth() >= 0) {
            if (!mEndVisible) {
                // Delay this to avoid flicker
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mEndVisible = true;
                        mEndMarker.setImageAlpha(255);
                    }
                }, 0);
            }
        } else {
            if (mEndVisible) {
                mEndMarker.setImageAlpha(0);
                mEndVisible = false;
            }
            endX = 0;
        }

        mStartMarker.setLayoutParams(
                new AbsoluteLayout.LayoutParams(
                        AbsoluteLayout.LayoutParams.WRAP_CONTENT,
                        AbsoluteLayout.LayoutParams.WRAP_CONTENT,
                        startX, mMarkerTopOffset));

        mEndMarker.setLayoutParams(
                new AbsoluteLayout.LayoutParams(
                        AbsoluteLayout.LayoutParams.WRAP_CONTENT,
                        AbsoluteLayout.LayoutParams.WRAP_CONTENT,
                        endX, mMarkerTopOffset));
    }

    protected Runnable mTimerRunnable = new Runnable() {
        public void run() {
            // Updating an EditText is slow on Android.  Make sure
            // we only do the update if the text has actually changed.
            if (mStartPos != mLastDisplayedStartPos) {
                mLastDisplayedStartPos = mStartPos;
            }

            if (mEndPos != mLastDisplayedEndPos) {
                mLastDisplayedEndPos = mEndPos;
            }

            mHandler.postDelayed(mTimerRunnable, 100);
        }
    };

    protected void enableDisableButtons() {
        if (mIsPlaying) {
            Log.d("myLogs","onPause2();");
            onPause2();
        } else {
            onPause2();
        }
    }

    protected void resetPositions() {
        mStartPos = 0;
        mEndPos = mMaxPos;
    }

    protected int trap(int pos) {
        if (pos < 0)
            return 0;
        if (pos > mMaxPos)
            return mMaxPos;
        return pos;
    }

    protected void setOffsetGoalStart() {
        setOffsetGoal(mStartPos - mWidth / 2);
    }

    protected void setOffsetGoalStartNoUpdate() {
        setOffsetGoalNoUpdate(mStartPos - mWidth / 2);
    }

    protected void setOffsetGoalEnd() {
        setOffsetGoal(mEndPos - mWidth / 2);
    }

    protected void setOffsetGoalEndNoUpdate() {
        setOffsetGoalNoUpdate(mEndPos - mWidth / 2);
    }

    protected void setOffsetGoal(int offset) {
        setOffsetGoalNoUpdate(offset);
        updateDisplay();
    }

    protected void setOffsetGoalNoUpdate(int offset) {
        if (mTouchDragging) {
            return;
        }

        mOffsetGoal = offset;
        if (mOffsetGoal + mWidth / 2 > mMaxPos)
            mOffsetGoal = mMaxPos - mWidth / 2;
        if (mOffsetGoal < 0)
            mOffsetGoal = 0;
    }

    protected String formatTime(int pixels) {
        if (mWaveformView != null && mWaveformView.isInitialized()) {
            return formatDecimal(mWaveformView.pixelsToSeconds(pixels));
        } else {
            return "";
        }
    }

    protected String formatDecimal(double x) {
        int xWhole = (int) x;
        int xFrac = (int) (100 * (x - xWhole) + 0.5);

        if (xFrac >= 100) {
            xWhole++; //Round up
            xFrac -= 100; //Now we need the remainder after the round up
            if (xFrac < 10) {
                xFrac *= 10; //we need a fraction that is 2 digits long
            }
        }

        if (xFrac < 10)
            return xWhole + ".0" + xFrac;
        else
            return xWhole + "." + xFrac;
    }

    protected synchronized void handlePause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }
        if(backgroundMusic != null && backgroundMusic.isPlaying()){
            backgroundMusic.pause();
            backgroundMusic.seekTo(0);
        }
        mWaveformView.setPlayback(-1);
        mIsPlaying = false;
        enableDisableButtons();
        onPause2();
    }

    abstract public void onPause2();

    protected synchronized void onPlay(int startPosition) {

        if (mIsPlaying) {
            handlePause();
            return;
        }

        if (mPlayer == null) {
            // Not initialized yet
            return;
        }

        try {
            mPlayStartMsec = mWaveformView.pixelsToMillisecs(startPosition);
            if (startPosition < mStartPos) {
                mPlayEndMsec = mWaveformView.pixelsToMillisecs(mStartPos);
            } else if (startPosition > mEndPos) {
                mPlayEndMsec = mWaveformView.pixelsToMillisecs(mMaxPos);
            } else {
                mPlayEndMsec = mWaveformView.pixelsToMillisecs(mEndPos);
            }

            mPlayStartOffset = 0;

            int startFrame = mWaveformView.secondsToFrames(mPlayStartMsec * 0.001);
            int endFrame = mWaveformView.secondsToFrames(mPlayEndMsec * 0.001);
            int startByte = mSoundFile.getSeekableFrameOffset(startFrame);
            int endByte = mSoundFile.getSeekableFrameOffset(endFrame);
            if (startByte >= 0 && endByte >= 0) {
                try {
                    mPlayer.reset();
                    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    FileInputStream subsetInputStream = new FileInputStream(mFile.getAbsolutePath());
                    mPlayer.setDataSource(subsetInputStream.getFD(),startByte, endByte - startByte);
                    mPlayer.prepare();
                    mPlayStartOffset = mPlayStartMsec;
                } catch (Exception e) {
                    Log.e(TAG, "Exception trying to play file subset", e);
                    mPlayer.reset();
                    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mPlayer.setDataSource(mFile.getAbsolutePath());
                    mPlayer.prepare();
                    mPlayStartOffset = 0;
                }
            }

            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    WaveformFragment.this.handlePause();
                }
            });
            mIsPlaying = true;

            if (mPlayStartOffset == 0) {
                mPlayer.seekTo(mPlayStartMsec);
            }
            mPlayer.start();
            if(backgroundMusic != null){
                backgroundMusic.start();
                backgroundMusic.setVolume(0.5f,0.5f);
            }
            mPlayer.setVolume(1, 1);

            startFadeIn();
            updateDisplay();

            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startFadeOut();
                }
            },mWaveformView.pixelsToMillisecs(mEndPos) - mWaveformView.pixelsToMillisecs(mStartPos) - 3300);
            enableDisableButtons();
        } catch (Exception e) {
            Log.e(TAG, "Exception while playing file", e);
        }
    }

    protected void turnBackgroundMusic(int id){
        enableMusic = !enableMusic;
        if(enableMusic){
            if (id != 0){
                backgroundMusic = MediaPlayer.create(getActivity(), id);
            } else if (backgroundMusic != null){
                backgroundMusic.stop();
                backgroundMusic.release();
                backgroundMusic = null;
            }

        }else{
            if (backgroundMusic != null){
                backgroundMusic.stop();
                backgroundMusic.release();
                backgroundMusic = null;
            }

        }
    }
    protected void turnFadeIn(){
        enableFadeIn = !enableFadeIn;
    }
    protected void turnFadeOut(){
        enableFadeOut = !enableFadeOut;
    }


    float volume = 0;
    float volumeBackgroundMusic = 0;
    private void startFadeIn(){
        if(!enableFadeIn){
            return;
        }
        volume = 0;
        volumeBackgroundMusic = 0;
        final int FADE_DURATION = 3000; //The duration of the fade
        //The amount of time between volume changes. The smaller this is, the smoother the fade
        final int FADE_INTERVAL = 250;
        final int MAX_VOLUME = 1; //The volume will increase from 0 to 1
        int numberOfSteps = FADE_DURATION/FADE_INTERVAL; //Calculate the number of fade steps
        //Calculate by how much the volume changes each step
        final float deltaVolume = MAX_VOLUME / (float)numberOfSteps;
        final float deltaVolumeBackgroundMusic = 0.5f / (float)numberOfSteps;

        //Create a new Timer and Timer task to run the fading outside the main UI thread
        fadeInStep(deltaVolume,deltaVolumeBackgroundMusic);
        final Timer timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                fadeInStep(deltaVolume,deltaVolumeBackgroundMusic); //Do a fade step
                //Cancel and Purge the Timer if the desired volume has been reached
                if(volume>=1f){
                    timer.cancel();
                    timer.purge();
                }
            }
        };

        timer.schedule(timerTask,FADE_INTERVAL,FADE_INTERVAL);
    }

    private void startFadeOut(){
        if(!enableFadeOut){
            return;
        }
        volume = 1;
        volumeBackgroundMusic = 0.5f;
        Log.d("myLogs","startFadeOut volume = " + volume);
        // The duration of the fade
        final int FADE_DURATION = 3000;

        // The amount of time between volume changes. The smaller this is, the smoother the fade
        final int FADE_INTERVAL = 250;

        // Calculate the number of fade steps
        int numberOfSteps = FADE_DURATION / FADE_INTERVAL;

        // Calculate by how much the volume changes each step
        final float deltaVolume = 1 / (float)numberOfSteps;
        final float deltaVolumeBackgroundMusic = 0.5f / (float)numberOfSteps;

        // Create a new Timer and Timer task to run the fading outside the main UI thread
        fadeOutStep(deltaVolume,deltaVolumeBackgroundMusic);
        final Timer timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                //Do a fade step
                fadeOutStep(deltaVolume,deltaVolumeBackgroundMusic);

                //Cancel and Purge the Timer if the desired volume has been reached
                if(volume <= 0){
                    timer.cancel();
                    timer.purge();
                }

                if(mPlayer.isPlaying() == false){
                    timer.cancel();
                    timer.purge();
                }
            }
        };

        timer.schedule(timerTask,FADE_INTERVAL,FADE_INTERVAL);
    }

    private void fadeOutStep(float deltaVolume, float deltaVolumeBackgroundMusic){
        Log.d("myLogs","fadeOutStep volume = " + volume);
        mPlayer.setVolume(volume, volume);
        volume -= deltaVolume;

        if(backgroundMusic != null && backgroundMusic.isPlaying()){
            volumeBackgroundMusic -= deltaVolumeBackgroundMusic;
            if(volumeBackgroundMusic < 0){
                volumeBackgroundMusic = 0;
            }
            backgroundMusic.setVolume(volumeBackgroundMusic, volumeBackgroundMusic);
        }
    }


    private void fadeInStep(float deltaVolume, float deltaVolumeBackgroundMusic){
        Log.d("myLogs","fadeInStep volume = " + volume);
        mPlayer.setVolume(volume, volume);
        volume += deltaVolume;
        if(backgroundMusic != null && backgroundMusic.isPlaying()){
            volumeBackgroundMusic += deltaVolumeBackgroundMusic;
            if(volumeBackgroundMusic > 0.5f){
                volumeBackgroundMusic = 0.5f;
            }
            backgroundMusic.setVolume(volumeBackgroundMusic, volumeBackgroundMusic);
        }
    }
    protected void enableZoomButtons() {
    }

    protected OnClickListener mPlayListener = new OnClickListener() {
        public void onClick(View sender) {
            onPlay(mStartPos);
        }
    };

    protected OnClickListener mRewindListener = new OnClickListener() {
        public void onClick(View sender) {
            if (mIsPlaying) {
                int newPos = mPlayer.getCurrentPosition() - 5000;
                if (newPos < mPlayStartMsec)
                    newPos = mPlayStartMsec;
                mPlayer.seekTo(newPos);
            } else {
                mStartPos = trap(mStartPos - mWaveformView.secondsToPixels(getStep()));
                updateDisplay();
                mStartMarker.requestFocus();
                markerFocus(mStartMarker);
            }
        }
    };

    protected OnClickListener mFfwdListener = new OnClickListener() {
        public void onClick(View sender) {
            if (mIsPlaying) {
                int newPos = 5000 + mPlayer.getCurrentPosition();
                if (newPos > mPlayEndMsec)
                    newPos = mPlayEndMsec;
                mPlayer.seekTo(newPos);
            } else {
                mStartPos = trap(mStartPos + mWaveformView.secondsToPixels(getStep()));
                updateDisplay();
                mStartMarker.requestFocus();
                markerFocus(mStartMarker);
            }
        }
    };

    protected OnClickListener mMarkStartListener = new OnClickListener() {
        public void onClick(View sender) {
            if (mIsPlaying) {
                mStartPos = mWaveformView.millisecsToPixels(mPlayer.getCurrentPosition() + mPlayStartOffset);
                updateDisplay();
            }
        }
    };

    protected OnClickListener mMarkEndListener = new OnClickListener() {
        public void onClick(View sender) {
            if (mIsPlaying) {
                mEndPos = mWaveformView.millisecsToPixels(mPlayer.getCurrentPosition() + mPlayStartOffset);
                updateDisplay();
                handlePause();
            }
        }
    };

    protected TextWatcher mTextWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {

        }
    };


    protected List<Segment> getSegments() {
        return null;
    }

    protected OnClickListener getFwdListener() {
        return mFfwdListener;
    }

    protected OnClickListener getRewindListener() {
        return mRewindListener;
    }

    protected int getStep() {
        int maxSeconds = (int) mWaveformView.pixelsToSeconds(mWaveformView.maxPos());
        if (maxSeconds / 3600 > 0) {
            return 600;
        } else if (maxSeconds / 1800 > 0) {
            return 300;
        } else if (maxSeconds / 300 > 0) {
            return 60;
        }
        return 5;
    }
}