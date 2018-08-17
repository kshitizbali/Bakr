package com.kshitizbali.bakr;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.kshitizbali.bakr.utilities.ConstantUtilities;
import com.kshitizbali.bakr.utilities.NetworkUtilities;

public class RecipeStepDetailFragment extends android.support.v4.app.Fragment implements ExoPlayer.EventListener {

    private SimpleExoPlayerView simpleExoPlayerView;
    private static final String TAG = "RecipeStepDetailFragment";
    private SimpleExoPlayer mExoPlayer;
    private TextView tvStepsDesc, tvError;
    private FloatingActionButton fab_Prev_Step, fab_Next_Step;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private NotificationManager mNotificationManager;

    static final String RECIPE_STEP_ID = "step_id";
    static final String RECIPE_STEP_VIDEO_LINK = "video_link";
    static final String RECIPE_STEP_DESC = "desc";
    static final String RECIPE_STEP_SHORT_DESC = "short_desc";
    static final String RECIPE_ID = "recipe_id";
    static final String RECIPE_STEPS_COUNT = "recipe_steps_count";
    static final String RECIPE_STEP_POSITION = "recipe_step_pos";


    private int /*recipeStepId, */mainRecipeId, recipeStepsCount, recipePos;
    private String videoLink, desc, shortDesc;
    private RecipeStepDetailFragment.onFabRecipeStepChangeListener onFabRecipeStepChangeListenerCallback;

    private int mResumeWindow;
    private long mResumePosition;
    private final String STATE_RESUME_WINDOW = "resumeWindow";
    private final String STATE_RESUME_POSITION = "resumePosition";
    private final String STATE_PLAYER_FULLSCREEN = "playerFullscreen";

    private boolean mExoPlayerFullscreen = false;
    private FrameLayout mFullScreenButton;
    private ImageView mFullScreenIcon, iv_no_video;
    private Dialog mFullScreenDialog;
    private static final String RECIPE_POS = "recipe_pos";
    static final String RECIPE_STEPS_COUNT_ROT = "recipe_steps_count_rot";
    private static final String VIDEO_LINK = "video_link";

    private View mainView;

    public RecipeStepDetailFragment() {

    }


    public interface onFabRecipeStepChangeListener {

        void onFabRecipeStepClicked(int nextOrPrev);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_recipe_step_detail, container, false);

        setMainView(rootView);
        instantiateViews(rootView);


        return rootView;
    }


    private void instantiateViews(View view) {
        simpleExoPlayerView = (SimpleExoPlayerView) view.findViewById(R.id.simpleExoPlayerView);
        initFullscreenDialog();
        initFullscreenButton();

        tvStepsDesc = (TextView) view.findViewById(R.id.tvStepsDesc);
        iv_no_video = (ImageView) view.findViewById(R.id.iv_no_video);
        fab_Prev_Step = (FloatingActionButton) view.findViewById(R.id.fab_Prev_Step);
        fab_Next_Step = (FloatingActionButton) view.findViewById(R.id.fab_Next_Step);
        tvError = (TextView) view.findViewById(R.id.tvError);


        if (this.getArguments() != null && this.getArguments().getInt(RECIPE_ID) != 0) {

            mainRecipeId = this.getArguments().getInt(RECIPE_ID);

        }

        if (this.getArguments() != null && this.getArguments().getInt(RECIPE_STEP_POSITION) != 0) {

            recipePos = this.getArguments().getInt(RECIPE_STEP_POSITION);

        }

        if (this.getArguments() != null && !this.getArguments().getString(RECIPE_STEP_VIDEO_LINK).isEmpty()) {

            videoLink = this.getArguments().getString(RECIPE_STEP_VIDEO_LINK);

        }

        if (this.getArguments() != null && !this.getArguments().getString(RECIPE_STEP_DESC).isEmpty()) {

            desc = this.getArguments().getString(RECIPE_STEP_DESC);
            tvStepsDesc.setText(desc);

        }

        if (this.getArguments() != null && !this.getArguments().getString(RECIPE_STEP_SHORT_DESC).isEmpty()) {

            shortDesc = this.getArguments().getString(RECIPE_STEP_SHORT_DESC);

        }


        if (this.getArguments() != null && this.getArguments().getInt(RECIPE_STEPS_COUNT) != 0) {

            recipeStepsCount = this.getArguments().getInt(RECIPE_STEPS_COUNT);

        }


        if (recipePos == 0) {
            fab_Prev_Step.setVisibility(View.INVISIBLE);
        } else if ((recipePos + 1) == recipeStepsCount) {
            fab_Next_Step.setVisibility(View.INVISIBLE);
        }


        fab_Next_Step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onFabRecipeStepChangeListenerCallback.onFabRecipeStepClicked(recipePos + 1);

            }
        });

        fab_Prev_Step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFabRecipeStepChangeListenerCallback.onFabRecipeStepClicked(recipePos - 1);
            }
        });

    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            onFabRecipeStepChangeListenerCallback = (RecipeStepDetailFragment.onFabRecipeStepChangeListener) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnRecipeClickListener");
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        if (savedInstanceState != null) {
            mResumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW);
            mResumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION);
            mExoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN);
            recipeStepsCount = savedInstanceState.getInt(RECIPE_STEPS_COUNT_ROT);
            recipePos = savedInstanceState.getInt(RECIPE_POS);
            videoLink = savedInstanceState.getString(VIDEO_LINK);
        }

        if (videoLink == null || videoLink.isEmpty()) {
            simpleExoPlayerView.setVisibility(View.INVISIBLE);
            iv_no_video.setVisibility(View.VISIBLE);


            return;
        } else {
            simpleExoPlayerView.setVisibility(View.VISIBLE);
            iv_no_video.setVisibility(View.GONE);
            simpleExoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource
                    (getResources(), R.drawable.video_player));
            if (NetworkUtilities.isInternetAvailable(getActivity())) {
                initializeMediaSession();
                initializePlayer(Uri.parse(videoLink));
            } else {

            }

        }

    }


    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getActivity(), TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    /**
     * Method that is called when the ExoPlayer state changes. Used to update the MediaSession
     * PlayBackState to keep in sync, and post the media notification.
     *
     * @param playWhenReady true if ExoPlayer is playing, false if it's paused.
     * @param playbackState int describing the state of ExoPlayer. Can be STATE_READY, STATE_IDLE,
     *                      STATE_BUFFERING, or STATE_ENDED.
     */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
        //showNotification(mStateBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }


    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

    /**
     * Broadcast Receiver registered to receive the MEDIA_BUTTON intent coming from clients.
     */
    public static class MediaReceiver extends BroadcastReceiver {

        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }


    /**
     * Initialize ExoPlayer.
     *
     * @param mediaUri The URI of the sample to play.
     */
    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);

            /*if (getActivity().getResources().getConfiguration().orientation ==
                    Configuration.ORIENTATION_LANDSCAPE) {

                ConstraintLayout.LayoutParams params =
                        params = (ConstraintLayout.LayoutParams)
                                simpleExoPlayerView.getLayoutParams();
                params.width = params.MATCH_PARENT;
                params.height = params.MATCH_PARENT;
                simpleExoPlayerView.setLayoutParams(params);
                simpleExoPlayerView.setPlayer(mExoPlayer);
            } else {
                simpleExoPlayerView.setPlayer(mExoPlayer);
            }*/

            simpleExoPlayerView.setPlayer(mExoPlayer);


            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getActivity(), getString(R.string.app_name));
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getActivity(), userAgent), new DefaultExtractorsFactory(), null, null);


            boolean haveResumePosition = mResumeWindow != C.INDEX_UNSET;

            if (haveResumePosition) {
                mExoPlayer.seekTo(mResumeWindow, mResumePosition);
            }

            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }


    //    /**
//     * Shows Media Style notification, with actions that depend on the current MediaSession
//     * PlaybackState.
//     *
//     * @param state The PlaybackState of the MediaSession.
//     */
/*
    private void showNotification(PlaybackStateCompat state) {


        int icon;
        String play_pause;
        if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
            icon = R.drawable.exo_controls_pause;
            play_pause = getString(R.string.pause);
        } else {
            icon = R.drawable.exo_controls_play;
            play_pause = getString(R.string.play);
        }

        mNotificationManager = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), ConstantUtilities.NOTI_CHANNEL_ID);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {


            String CHANNEL_ID = ConstantUtilities.NOTI_CHANNEL_ID;
            CharSequence name = ConstantUtilities.NOTI_CHANNEL_NAME;
            String Description = ConstantUtilities.NOTI_CHANNEL_DESC;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            mNotificationManager.createNotificationChannel(mChannel);
        }


        NotificationCompat.Action playPauseAction = new NotificationCompat.Action(
                icon, play_pause,
                MediaButtonReceiver.buildMediaButtonPendingIntent(getActivity(),
                        PlaybackStateCompat.ACTION_PLAY_PAUSE));

        NotificationCompat.Action restartAction = new android.support.v4.app.NotificationCompat
                .Action(R.drawable.exo_controls_previous, getString(R.string.restart),
                MediaButtonReceiver.buildMediaButtonPendingIntent
                        (getActivity(), PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));

        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (getActivity(), 0, new Intent(getActivity(), MainActivity.class), 0);

        builder.setContentTitle(shortDesc)
                .setContentText(getString(R.string.notification_text))
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.video)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(restartAction)
                .addAction(playPauseAction)
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mMediaSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1));


        mNotificationManager.notify(0, builder.build());
    }
*/
    private void releasePlayer() {
        //mNotificationManager.cancelAll();
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releasePlayer();
        if (mMediaSession != null) {
            mMediaSession.setActive(false);
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putInt(STATE_RESUME_WINDOW, mResumeWindow);
        outState.putLong(STATE_RESUME_POSITION, mResumePosition);
        outState.putBoolean(STATE_PLAYER_FULLSCREEN, mExoPlayerFullscreen);
        outState.putInt(RECIPE_POS, recipePos);
        outState.putInt(RECIPE_STEPS_COUNT_ROT, recipeStepsCount);
        outState.putString(VIDEO_LINK, videoLink);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (simpleExoPlayerView != null && simpleExoPlayerView.getPlayer() != null) {
            mResumeWindow = simpleExoPlayerView.getPlayer().getCurrentWindowIndex();
            mResumePosition = Math.max(0, simpleExoPlayerView.getPlayer().getCurrentPosition());
            releasePlayer();
            if (mMediaSession != null) {
                mMediaSession.setActive(false);
            }
        }

        if (mFullScreenDialog != null)
            mFullScreenDialog.dismiss();
    }


    @Override
    public void onResume() {
        super.onResume();


        if (simpleExoPlayerView == null) {
            initFullscreenDialog();
            initFullscreenButton();
        }

        if (videoLink == null || videoLink.isEmpty()) {
            simpleExoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource
                    (getResources(), R.drawable.question_mark));

            return;
        } else {
            initializeMediaSession();
            initializePlayer(Uri.parse(videoLink));
        }


        if (mExoPlayerFullscreen) {
            ((ViewGroup) simpleExoPlayerView.getParent()).removeView(simpleExoPlayerView);
            mFullScreenDialog.addContentView(simpleExoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_fullscreen_skrink));
            mFullScreenDialog.show();
        } /*else {
            if (tvStepsDesc.getVisibility() == View.GONE) {
                Log.i("Land", "True");
                demoFullscreen();
            }
        }*/

    }


    private void initFullscreenDialog() {

        mFullScreenDialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (mExoPlayerFullscreen)
                    closeFullscreenDialog();
                super.onBackPressed();
            }
        };
    }


    private void openFullscreenDialog() {

        ((ViewGroup) simpleExoPlayerView.getParent()).removeView(simpleExoPlayerView);
        mFullScreenDialog.addContentView(simpleExoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_fullscreen_skrink));
        mExoPlayerFullscreen = true;
        mFullScreenDialog.show();
    }


    private void closeFullscreenDialog() {

        ((ViewGroup) simpleExoPlayerView.getParent()).removeView(simpleExoPlayerView);
        ((FrameLayout) getMainView().findViewById(R.id.main_media_frame)).addView(simpleExoPlayerView);
        mExoPlayerFullscreen = false;
        mFullScreenDialog.dismiss();
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_fullscreen_expand));
    }


    private void initFullscreenButton() {

        PlaybackControlView controlView = simpleExoPlayerView.findViewById(R.id.exo_controller);
        mFullScreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
        mFullScreenButton = controlView.findViewById(R.id.exo_fullscreen_button);
        mFullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mExoPlayerFullscreen)
                    openFullscreenDialog();
                else
                    closeFullscreenDialog();
            }
        });
    }

    public View getMainView() {
        return mainView;
    }

    public void setMainView(View mainView) {
        this.mainView = mainView;
    }


}
