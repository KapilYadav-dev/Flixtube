package in.kay.flixtube.UI;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import in.kay.flixtube.R;

public class DetailActivity extends AppCompatActivity {
    String imdb, trailer, url, type, title;
    TextView tvTitle, tvTime, tvPlot, tvCasting, tvGenre, tvAbout, tvAward, tvAwards, tvCastName, tvImdb;
    RequestQueue requestQueue;
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        GetValues();
        initz();
        GetDatafromURL();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void GetDatafromURL() {
        String URL = "http://www.omdbapi.com/?apikey=a7008f3&i=" + imdb + "&plot=long";
        requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String movieName = title = jsonObject.getString("Title");
                    String movieGenre = jsonObject.getString("Genre");
                    String movieImdb = jsonObject.getString("imdbRating");
                    String movieDate = jsonObject.getString("Released");
                    String movieTime = jsonObject.getString("Runtime");
                    String moviePoster = jsonObject.getString("Poster");
                    String moviePlot = jsonObject.getString("Plot");
                    String movieCast = jsonObject.getString("Actors");
                    String movieAward = jsonObject.getString("Awards");
                    tvTitle.setText(movieName);
                    tvAbout.setText(moviePlot);
                    tvGenre.setText(movieGenre + "  |  " + movieDate);
                    tvImdb.setText(movieImdb + "/10");
                    tvCastName.setText(movieCast);
                    tvAwards.setText(movieAward);
                    tvTime.setText(movieTime);
                    Picasso.get()
                            .load(moviePoster)
                            .into(iv);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(DetailActivity.this, "Error occured " + e, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DetailActivity.this, "Error occured " + error, Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(request);
    }

    private void GetValues() {
        imdb = getIntent().getStringExtra("imdb");
        trailer = getIntent().getStringExtra("trailer");
        type = getIntent().getStringExtra("type");
        url = getIntent().getStringExtra("url");
    }

    private void initz() {
        Typeface font = Typeface.createFromAsset(this.getAssets(), "Gilroy-ExtraBold.ttf");
        Typeface medium = Typeface.createFromAsset(this.getAssets(), "Gilroy-Medium.ttf");
        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "Gilroy-Light.ttf");
        /////////////////////////////////
        tvTitle = findViewById(R.id.tv_title);
        tvCasting = findViewById(R.id.tv_casting);
        tvImdb = findViewById(R.id.tv_imdb);
        tvCastName = findViewById(R.id.tv_cast_name);
        tvPlot = findViewById(R.id.tv_plot);
        tvTime = findViewById(R.id.tv_time);
        tvGenre = findViewById(R.id.tv_genre);
        tvAbout = findViewById(R.id.tv_about);
        tvAward = findViewById(R.id.tv_award);
        tvAwards = findViewById(R.id.tv_awards);
        /////////////////////////////////
        iv = findViewById(R.id.iv_cover_img);
        /////////////////////////////////
        tvAbout.setTypeface(typeface);
        tvGenre.setTypeface(typeface);
        tvCastName.setTypeface(typeface);
        tvAwards.setTypeface(typeface);
        tvImdb.setTypeface(typeface);
        tvTitle.setTypeface(font);
        tvPlot.setTypeface(medium);
        tvCasting.setTypeface(medium);
        tvAward.setTypeface(medium);

    }

    public void PlayMovie(View view) {

    }

    public void Download(View view) {

    }

}