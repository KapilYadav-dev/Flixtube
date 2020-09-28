package in.kay.flixtube.UI.HomeUI;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import in.kay.flixtube.Adapter.SeriesPlayAdapter;
import in.kay.flixtube.Model.SeriesModel;
import in.kay.flixtube.R;
import in.kay.flixtube.Utils.Helper;

public class DetailActivity extends AppCompatActivity implements PaymentResultListener {

    String imdb, trailer, url, type, title, image, contentType, key;
    TextView tvTitle, tvTime, tvPlot, tvCasting, tvGenre, tvAbout, tvAward, tvAwards, tvCastName, tvImdb, tvSeasons, tvWatch;
    ImageView iv;
    Helper helper;
    RecyclerView rvSeries;
    DatabaseReference rootRef;
    SeriesPlayAdapter seriesPlayAdapter;
    LikeButton likeButton;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        helper = new Helper();
        rootRef = FirebaseDatabase.getInstance().getReference();
        //CheckInternet();
    }

    private void CheckInternet() {
        if (helper.isNetwork(this)) {
            InitAll();
        } else {
            Typeface font = Typeface.createFromAsset(this.getAssets(), "Gilroy-ExtraBold.ttf");
            new iOSDialogBuilder(this)
                    .setTitle("Oh shucks!")
                    .setSubtitle("Slow or no internet connection.\nPlease check your internet settings")
                    .setCancelable(false)
                    .setFont(font)
                    .setPositiveListener(getString(R.string.ok), new iOSDialogClickListener() {
                        @Override
                        public void onClick(iOSDialog dialog) {
                            CheckInternet();
                            dialog.dismiss();
                        }
                    })
                    .build().show();
        }
    }

    private void InitAll() {
        rootRef.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String membership = snapshot.child("Membership").getValue(String.class);
                Initz(membership);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void Initz(final String membership) {
        GetValues();
        InitzViews();
        GetData getData = new GetData();
        getData.execute();
        findViewById(R.id.btn_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayMovie(membership);
            }
        });
        findViewById(R.id.btn_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Download();
            }
        });
    }

    private void LoadSeries(JSONObject jsonObject) {
        if (type.equalsIgnoreCase("Series") || type.equalsIgnoreCase("Webseries")) {
            String movieSeason = null;
            try {
                movieSeason = jsonObject.getString("number_of_seasons");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //tvCasting.setVisibility(View.GONE);
            //tvCastName.setVisibility(View.GONE);
            tvSeasons.setText("Total Seasons " + movieSeason);
            tvSeasons.setVisibility(View.VISIBLE);
            findViewById(R.id.ll).setVisibility(View.GONE);
            rvSeries.setVisibility(View.VISIBLE);
            findViewById(R.id.tv_watch).setVisibility(View.VISIBLE);
            findViewById(R.id.ll).setVisibility(View.GONE);
            String key = getIntent().getStringExtra("key");
            FirebaseRecyclerOptions<SeriesModel> options = new FirebaseRecyclerOptions.Builder<SeriesModel>()
                    .setQuery(rootRef.child("Webseries").child(key).child("Source"), SeriesModel.class)
                    .build();
            seriesPlayAdapter = new SeriesPlayAdapter(options, this, image);
            rvSeries.setAdapter(seriesPlayAdapter);
            seriesPlayAdapter.startListening();
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        rootRef.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Membership").setValue("VIP").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                TastyToast.makeText(DetailActivity.this, "Welcome to Flixtube VIP club...", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                TastyToast.makeText(DetailActivity.this, "Server down. Error : " + e, TastyToast.LENGTH_LONG, TastyToast.ERROR);
            }
        });
    }

    @Override
    public void onPaymentError(int i, String s) {
        TastyToast.makeText(this, "Payment cancelled.", TastyToast.LENGTH_LONG, TastyToast.ERROR);
    }

    public void back(View view) {
        onBackPressed();
    }

    private class GetData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            findViewById(R.id.nsv_detail).setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("LOGMSG", "ASYNC CALLED");
            try {
                GetDatafromURL();
            } catch (JSONException | IOException e) {
                Log.d("ErrorIS", "Error is " + e);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        TastyToast.makeText(DetailActivity.this, "Something went wrong.", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                    }
                });

            }
            return null;
        }
    }

    private void GetDatafromURL() throws IOException, JSONException {

        String movieGenre = "",strUrl="",runtime="",date="",mname="";

        if (type.equalsIgnoreCase("Series") || type.equalsIgnoreCase("Webseries")) {
            strUrl = "https://api.themoviedb.org/3/tv/" + imdb + "?api_key=78f8e2ad04a35e7d8a8117dfef2de601&language=en-US";
        }
        else
        {
            strUrl= "https://api.themoviedb.org/3/movie/" + imdb + "?api_key=78f8e2ad04a35e7d8a8117dfef2de601&language=en-US";
        }
            URL url = new URL(strUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();
            if (inputStream == null) {
                TastyToast.makeText(DetailActivity.this, "Something went wrong.", TastyToast.LENGTH_LONG, TastyToast.ERROR);
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }

            final JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            final JSONArray genrearray = jsonObject.getJSONArray("genres");
            final String genrename[] = new String[genrearray.length()];
            for (int i = 0; i < genrearray.length(); i++)
            {
                //Log.d("DETAILARRAY", "Genre " + genrearray);
                JSONObject genreobject = genrearray.getJSONObject(i);
                genrename[i] = genreobject.getString("name");
                if (i < genrearray.length() - 1)
                    movieGenre = movieGenre + genrename[i] + ", ";
                else
                    movieGenre = movieGenre + genrename[i];
            }


            final double movieImdb = jsonObject.getDouble("vote_average");
            final String moviePoster = image = "https://image.tmdb.org/t/p/w500" + jsonObject.getString("poster_path");
            final String moviePlot = jsonObject.getString("overview");
            final int id = jsonObject.getInt("id");
            uid = Integer.toString(id);

            if (type.equalsIgnoreCase("Series") || type.equalsIgnoreCase("Webseries"))
            {
                mname = title = jsonObject.getString("name");
                date = jsonObject.getString("first_air_date");
                final JSONArray timearray = jsonObject.getJSONArray("episode_run_time");
                runtime = timearray.getString(0);
          }
          else
              {
               mname = title = jsonObject.getString("title");
               date = jsonObject.getString("release_date");
               runtime = jsonObject.getString("runtime");
             }
        final String movieName= mname;
        final String movieDate= date;
            final String movieTime= runtime;
            final String movieCast = CastDataFetch();
            final String finalMovieGenre = movieGenre;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    UpdateUI(jsonObject, movieName, movieImdb, movieDate, moviePoster, moviePlot, movieCast, finalMovieGenre, movieTime);
                }
            });
        //Log.d("DETAILARRAY", "Reached end of fn");
    }


    private String CastDataFetch() {
        String castnamefinal="";
        String caststrUrl="";
        if (type.equalsIgnoreCase("Series") || type.equalsIgnoreCase("Webseries")) {
            caststrUrl = "https://api.themoviedb.org/3/tv/" + uid + "/credits?api_key=78f8e2ad04a35e7d8a8117dfef2de601";
        }
        else
        {
            caststrUrl = "https://api.themoviedb.org/3/movie/" + uid + "/credits?api_key=78f8e2ad04a35e7d8a8117dfef2de601";
        }
        try {
            URL casturl = new URL(caststrUrl);
            HttpURLConnection new_connection = (HttpURLConnection) casturl.openConnection();
            InputStream new_inputStream = new_connection.getInputStream();
            if (new_inputStream == null) {
                TastyToast.makeText(DetailActivity.this, "Something went wrong.", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
            }
            BufferedReader new_br = new BufferedReader(new InputStreamReader(new_inputStream));
            String str_line;
            StringBuilder cast_stringBuilder = new StringBuilder();
            while ((str_line = new_br.readLine()) != null) {
                cast_stringBuilder.append(str_line);
            }
            JSONObject castparentObject = new JSONObject(cast_stringBuilder.toString());
            JSONArray parentarray = castparentObject.getJSONArray("cast");
            final String castname[] = new String[10];

            for (int i = 0; i < 10; i++) {
                JSONObject castobject = parentarray.getJSONObject(i);
                castname[i] = castobject.getString("name");

                if(i!=9)
                    castnamefinal= castnamefinal+castname[i]+", ";
                else
                    castnamefinal=castnamefinal+castname[i];

            }
           // Log.d("DETAILARRAY", "CastDataFetch: " + castnamefinal);

        }catch (IOException e) {
            Log.d("DETAILARRAY", "Error: " + e);
            e.printStackTrace();
        } catch (JSONException e) {
            Log.d("DETAILARRAY", "Error: " + e);
            e.printStackTrace();
        }
        return castnamefinal;
    }

    private void UpdateUI(JSONObject jsonObject, String movieName, double movieImdb, String movieDate, String moviePoster, String moviePlot, String movieCast, String movieGenre, String movieTime) {
        tvTitle.setText(movieName);
        tvAbout.setText(moviePlot);
        tvGenre.setText(movieGenre + "  |  " + movieDate);
        tvImdb.setText(movieImdb + "/10");
        tvCastName.setText(movieCast);
        //tvAwards.setText(movieAward);
        tvTime.setText(movieTime+ " min");
        Picasso.get()
                .load(moviePoster)
                .into(iv);
        LoadSeries(jsonObject);
    }


    private void GetValues() {
        key = getIntent().getStringExtra("key");
        imdb = getIntent().getStringExtra("imdb");
        trailer = getIntent().getStringExtra("trailer");
        type = getIntent().getStringExtra("type");
        url = getIntent().getStringExtra("url");
        contentType = getIntent().getStringExtra("movieType");
    }

    private void InitzViews() {
        Typeface font = Typeface.createFromAsset(this.getAssets(), "Gilroy-ExtraBold.ttf");
        Typeface brandon = Typeface.createFromAsset(this.getAssets(), "Brandon.ttf");
        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "Gilroy-Light.ttf");
        /////////////////////////////////
        likeButton = findViewById(R.id.star_button);
        tvTitle = findViewById(R.id.tv_title);
        tvCasting = findViewById(R.id.tv_casting);
        tvImdb = findViewById(R.id.tv_imdb);
        tvCastName = findViewById(R.id.tv_cast_name);
        tvPlot = findViewById(R.id.tv_plot);
        tvTime = findViewById(R.id.tv_time);
        tvGenre = findViewById(R.id.tv_genre);
        tvWatch = findViewById(R.id.tv_watch);
        tvAbout = findViewById(R.id.tv_about);
        tvSeasons = findViewById(R.id.tv_seasons);
        tvAward = findViewById(R.id.tv_award);
        tvAwards = findViewById(R.id.tv_awards);
        /////////////////////////////////
        iv = findViewById(R.id.iv_cover_img);
        /////////////////////////////////
        rvSeries = findViewById(R.id.rv_series);
        rvSeries.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        /////////////////////////////////
        tvAbout.setTypeface(typeface);
        tvGenre.setTypeface(typeface);
        tvCastName.setTypeface(typeface);
        tvAwards.setTypeface(typeface);
        tvImdb.setTypeface(typeface);
        tvTitle.setTypeface(font);
        tvPlot.setTypeface(brandon);
        tvCasting.setTypeface(brandon);
        tvAward.setTypeface(brandon);
        tvWatch.setTypeface(brandon);
        CheckLike();
        LikeClick();

    }

    private void CheckLike() {
        rootRef.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Watchlist").child(type).child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    likeButton.setLiked(true);
                } else {
                    likeButton.setLiked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void LikeClick() {
        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(final LikeButton likeButton) {
                likeButton.setLiked(true);
                HashMap<String, Object> map = new HashMap<>();
                map.put("image", image);
                map.put("title", title);
                if (TextUtils.isEmpty(contentType))
                {
                    map.put("contentType", "free");
                }
                else {
                    map.put("contentType", contentType);
                }
                rootRef.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Watchlist").child(type).child(key).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        TastyToast.makeText(getApplicationContext(), "Added to watchlist successfully", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        TastyToast.makeText(getApplicationContext(), "Something went wrong.", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                    }
                });
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                likeButton.setLiked(false);
                rootRef.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Watchlist").child(type).child(key).setValue(null);
                TastyToast.makeText(getApplicationContext(), "Removed from watchlist", TastyToast.LENGTH_LONG, TastyToast.INFO);
            }
        });
    }

    public void PlayMovie(String membership) {
        if (contentType.equalsIgnoreCase("Premium")) {
            if (membership.equalsIgnoreCase("VIP")) {
                Intent intent = new Intent(this, PlayerActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("title", title);
                startActivity(intent);
            } else {
                ShowPopup();
            }
        } else {
            Intent intent = new Intent(this, PlayerActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            startActivity(intent);
        }
    }

    private void ShowPopup() {
        Typeface font = Typeface.createFromAsset(this.getAssets(), "Gilroy-ExtraBold.ttf");
        new iOSDialogBuilder(DetailActivity.this)
                .setTitle("Buy Premium")
                .setSubtitle("You discovered a premium feature. Streaming a premium content requires VIP account. Press buy to continue")
                .setBoldPositiveLabel(true)
                .setFont(font)
                .setCancelable(false)
                .setPositiveListener(getString(R.string.buy), new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        BuyAccount();
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

    private void BuyAccount() {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_iSJv7N9Z4dJo63");
        final Activity activity = this;
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Flixtube");
            options.put("description", "Purchase premium Flixtube account");
            options.put("currency", "INR");
            String paisee = Integer.toString(Integer.parseInt("199") * 100);
            options.put("amount", paisee);
            checkout.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(this, "Payment error please try again" + e, Toast.LENGTH_SHORT).show();
        }
    }

    public void Download() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        } else {
            File myDirectory = new File("/Flixtube");
            if(!myDirectory.exists()) {
                myDirectory.mkdirs();
            }
            TastyToast.makeText(this, "Downloading " + title, TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
            helper.DownloadFile(this, title, "Movie", helper.decryptedMsg("Flixtube", url),myDirectory.getAbsolutePath());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                TastyToast.makeText(DetailActivity.this, "Permission successfully granted...", Toast.LENGTH_SHORT, TastyToast.SUCCESS);

            } else {
                TastyToast.makeText(DetailActivity.this, "Please allow us to download..", Toast.LENGTH_SHORT, TastyToast.CONFUSING);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideRight(this);
    }

    public void TrailerPlay(View view) {
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("url", trailer);
        intent.putExtra("title", title + " trailer");
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckInternet();
    }
}
