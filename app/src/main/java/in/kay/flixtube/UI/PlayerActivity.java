package in.kay.flixtube.UI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.pixplicity.easyprefs.library.Prefs;
import com.sdsmdg.tastytoast.TastyToast;

import in.kay.flixtube.R;

public class PlayerActivity extends AppCompatActivity {
    SimpleExoPlayerView exoPlayerView;
    SimpleExoPlayer exoPlayer;
    String videoURL,title ;
    TextView Name;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        videoURL=getIntent().getStringExtra("url");
        title=getIntent().getStringExtra("title");
        Name=findViewById(R.id.title);
        Name.setText(title);
        exoPlayerView = (SimpleExoPlayerView) findViewById(R.id.exo_player_view);
        back=findViewById(R.id.iv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContinueWatching();
                startActivity(new Intent(PlayerActivity.this,MainActivity.class));
            }
        });
        try {
            ExoPlayerLogic();
        } catch (Exception e) {
            Log.e("Error", " exoplayer error " + e.toString());
        }

    }
    private void ExoPlayerLogic() {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        Uri videoURI = Uri.parse(videoURL);
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource mediaSource = new ExtractorMediaSource(videoURI, dataSourceFactory, extractorsFactory, null, null);
        exoPlayerView.setPlayer(exoPlayer);
        exoPlayerView.setKeepScreenOn(true);
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);
        //Used to know user is playing that video and if true then continue to last location
        String pref_url= Prefs.getString("url","null");
        String pref_title= Prefs.getString("title","null");
        if (pref_url.equalsIgnoreCase(videoURL) && pref_title.equalsIgnoreCase(title))
        {
            Long time= Prefs.getLong("time",0);
            Log.d("Last_Time", "Video time is "+time);
            exoPlayer.seekTo(time);
        }
        exoPlayer.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == ExoPlayer.STATE_ENDED)
                {
                    TastyToast.makeText(PlayerActivity.this,"Show has been ended",TastyToast.LENGTH_LONG,TastyToast.SUCCESS);
                    Prefs.putLong("time", 0);
                    startActivity(new Intent(PlayerActivity.this,MainActivity.class));

                }
                else if (playbackState==ExoPlayer.STATE_BUFFERING)
                {

                }
                else if (!(playbackState==ExoPlayer.STATE_BUFFERING))
                {

                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                TastyToast.makeText(PlayerActivity.this,"Error Occured "+error,TastyToast.LENGTH_SHORT,TastyToast.ERROR);
                startActivity(new Intent(PlayerActivity.this,MainActivity.class));

            }

            @Override
            public void onPositionDiscontinuity() {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        releasePlayer();
        ContinueWatching();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
        ContinueWatching();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
        ContinueWatching();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        releasePlayer();
        ContinueWatching();
    }
    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.release();
        }
    }

    private void ContinueWatching() {
        Prefs.putString("url", videoURL);
        Prefs.putString("title", title);
        Prefs.putLong("time", exoPlayer.getCurrentPosition());
    }
}