package in.kay.flixtube.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import in.kay.flixtube.Adapter.FeatureAdapter;
import in.kay.flixtube.Adapter.MovieAdapter;
import in.kay.flixtube.Adapter.SeriesAdapter;
import in.kay.flixtube.Model.MovieModel;
import in.kay.flixtube.Model.SeriesModel;
import in.kay.flixtube.R;

public class MainActivity extends AppCompatActivity {
    DatabaseReference rootRef;
    TextView tvName, tvFeatured, tvMovies ,tvSeries;
    RecyclerView rvFeatured, rvMovies, rvSeries;
    MovieAdapter movieAdapter;
    FeatureAdapter featureAdapter;
    SeriesAdapter seriesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initz();
    }
    private void initz() {
        LoadViews();
        LoadMovies();
        LoadFeatured();
        LoadSeries();
    }


    private void LoadViews() {
        rootRef = FirebaseDatabase.getInstance().getReference();
        /////
        Typeface font = Typeface.createFromAsset(this.getAssets(), "Gilroy-ExtraBold.ttf");
        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "Montserrat_bold.ttf");
        /////
        tvName = findViewById(R.id.tv_name);
        tvFeatured = findViewById(R.id.tv_featured);
        tvSeries = findViewById(R.id.tv_series);
        tvMovies = findViewById(R.id.tv_movies);
        /////
        rvFeatured = findViewById(R.id.rv_featured);
        rvSeries = findViewById(R.id.rv_series);
        rvMovies = findViewById(R.id.rv_movies);
        /////
        rvMovies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvFeatured.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        SnapHelper snapHelpernew = new PagerSnapHelper();
        snapHelpernew.attachToRecyclerView(rvFeatured);
        rvSeries.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        /////
        tvName.setTypeface(font);
        tvFeatured.setTypeface(typeface);
        tvMovies.setTypeface(typeface);
        tvSeries.setTypeface(typeface);
    }
    private void LoadMovies() {
        FirebaseRecyclerOptions<MovieModel> options = new FirebaseRecyclerOptions.Builder<MovieModel>()
                .setQuery(rootRef.child("Movies"), MovieModel.class)
                .build();
        movieAdapter = new MovieAdapter(options,this);
        rvMovies.setAdapter(movieAdapter);
        movieAdapter.startListening();
    }
    private void LoadFeatured() {
        FirebaseRecyclerOptions<MovieModel> options = new FirebaseRecyclerOptions.Builder<MovieModel>()
                .setQuery(rootRef.child("Movies").orderByChild("featured").equalTo("Yes"), MovieModel.class)
                .build();
        featureAdapter = new FeatureAdapter(options ,this);
        rvFeatured.setAdapter(featureAdapter);
        featureAdapter.startListening();

    }
    private void LoadSeries() {
        FirebaseRecyclerOptions<SeriesModel> options = new FirebaseRecyclerOptions.Builder<SeriesModel>()
                .setQuery(rootRef.child("Webseries"), SeriesModel.class)
                .build();
        seriesAdapter = new SeriesAdapter(options,this);
        rvSeries.setAdapter(seriesAdapter);
        seriesAdapter.startListening();
    }


    public void ViewAllSeries(View view) {
        Intent intent=new Intent(this,ViewAllActivity.class);
        intent.putExtra("type","Webseries");
        startActivity(intent);
    }

    public void ViewAllMovies(View view) {
        Intent intent=new Intent(this,ViewAllActivity.class);
        intent.putExtra("type","Movies");
        startActivity(intent);
    }
}