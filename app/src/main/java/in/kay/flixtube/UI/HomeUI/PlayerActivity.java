package in.kay.flixtube.UI.HomeUI;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.github.nisrulz.sensey.FlipDetector;
import com.github.nisrulz.sensey.Sensey;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pixplicity.easyprefs.library.Prefs;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.HashMap;

import in.kay.flixtube.R;
import in.kay.flixtube.Utils.Helper;

public class PlayerActivity extends AppCompatActivity {
    SimpleExoPlayerView exoPlayerView;
    SimpleExoPlayer exoPlayer;
    String videoURL, title;
    TextView Name;
    ImageView back;
    FlipDetector.FlipListener flipListener;
    DatabaseReference rootRef;
    Helper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getValue();
        initz();
        SensorFn();
        try {
            ExoPlayerLogic();
        } catch (Exception e) {
            Log.e("Error", " exoplayer error " + e.toString());
        }

    }

    private void SensorFn() {
        FlipFN();
    }


    private void FlipFN() {
        flipListener = new FlipDetector.FlipListener() {
            @Override
            public void onFaceUp() {
                Log.d("LOGMSG", "onFaceUp: ");
                playPlayer();
            }

            @Override
            public void onFaceDown() {
                // Device Facing down
                Log.d("LOGMSG", "onFaceDown: ");
                pausePlayer();
            }
        };
        Sensey.getInstance().startFlipDetection(flipListener);
    }

    private void getValue() {
        videoURL = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
    }

    private void initz() {
        rootRef= FirebaseDatabase.getInstance().getReference();
        Sensey.getInstance().init(this);
        Name = findViewById(R.id.title);
        Name.setText(title);
        exoPlayerView = (SimpleExoPlayerView) findViewById(R.id.exo_player_view);
        back = findViewById(R.id.iv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContinueWatching();
                onBackPressed();
            }
        });
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
        String pref_url = Prefs.getString("url", "null");
        String pref_title = Prefs.getString("title", "null");
        if (pref_url.equalsIgnoreCase(videoURL) && pref_title.equalsIgnoreCase(title)) {
            Long time = Prefs.getLong("time", 0);
            Log.d("Last_Time", "Video time is " + time);
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
                if (playbackState == ExoPlayer.STATE_ENDED) {
                    TastyToast.makeText(PlayerActivity.this, "Content has been ended", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                    Prefs.putLong("time", 0);
                    onBackPressed();

                } else if (playbackState == ExoPlayer.STATE_BUFFERING) {

                } else if (!(playbackState == ExoPlayer.STATE_BUFFERING)) {

                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                TastyToast.makeText(PlayerActivity.this, "Error Occured " + error, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                onBackPressed();

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
        pausePlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
        ContinueWatching();
        Sensey.getInstance().stopFlipDetection(flipListener);
        Sensey.getInstance().stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
        ContinueWatching();
        Sensey.getInstance().stopFlipDetection(flipListener);
        Sensey.getInstance().stop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        releasePlayer();
        ContinueWatching();
        Sensey.getInstance().stopFlipDetection(flipListener);
        Sensey.getInstance().stop();
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

    private void pausePlayer() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
        }
    }

    private void playPlayer() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(true);
        }
    }

    public void Report(View view) {
        Typeface font = Typeface.createFromAsset(this.getAssets(), "Gilroy-ExtraBold.ttf");
        new iOSDialogBuilder(PlayerActivity.this)
                .setTitle("Report this Media")
                .setSubtitle("You are going to report this media if case this isn't working or found some infringing content.")
                .setBoldPositiveLabel(true)
                .setCancelable(false)
                .setFont(font)
                .setPositiveListener(getString(R.string.ok),new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        ReportDB();
                        dialog.dismiss();

                    }
                })
                .setNegativeListener(getString(R.string.dismiss), new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .build().show();
    }

    private void ReportDB() {
        helper=new Helper();
        String date=helper.Date(this);
        String time=helper.Time(this);
        String movieName=title;
        HashMap<String,Object>map =new HashMap<>();
        map.put("Date",date);
        map.put("Time",time);
        map.put("MovieName",movieName);
        rootRef.child("Reports").push().setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                TastyToast.makeText(PlayerActivity.this,"Successfully reported Media",TastyToast.LENGTH_LONG,TastyToast.INFO);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                TastyToast.makeText(PlayerActivity.this,"Error while reporting media, "+e,TastyToast.LENGTH_LONG,TastyToast.ERROR);
            }
        });

    }
}