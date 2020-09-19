package in.kay.flixtube.UI;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.kay.flixtube.Adapter.FeatureAdapter;
import in.kay.flixtube.Adapter.MovieAdapter;
import in.kay.flixtube.Adapter.SeriesAdapter;
import in.kay.flixtube.Model.MovieModel;
import in.kay.flixtube.Model.SeriesModel;
import in.kay.flixtube.R;

public class MainActivity extends AppCompatActivity {
    DatabaseReference rootRef;
    TextView tvName, tvFeatured, tvMovies, tvSeries;
    RecyclerView rvFeatured, rvMovies, rvSeries;
    MovieAdapter movieAdapter;
    FeatureAdapter featureAdapter;
    SeriesAdapter seriesAdapter;
    int size;

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
        GetSizeRV();
    }

    private void GetSizeRV() {
        FnSeries();
        FnMovie();
    }

    private void FnSeries() {
        rootRef.child("Webseries").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                size = (int) snapshot.getChildrenCount();
                rvSeries.smoothScrollToPosition(size);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void FnMovie() {
        rootRef.child("Movies").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                size = (int) snapshot.getChildrenCount();
                rvMovies.smoothScrollToPosition(size);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }



    private void LoadViews() {
        rootRef = FirebaseDatabase.getInstance().getReference();
        /////
        Typeface font = Typeface.createFromAsset(this.getAssets(), "Gilroy-ExtraBold.ttf");
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
        rvMovies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        rvFeatured.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        SnapHelper snapHelpernew = new PagerSnapHelper();
        snapHelpernew.attachToRecyclerView(rvFeatured);
        rvSeries.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        /////
        tvName.setTypeface(font);
        tvFeatured.setTypeface(font);
        tvMovies.setTypeface(font);
        tvSeries.setTypeface(font);
    }

    private void LoadMovies() {
        FirebaseRecyclerOptions<MovieModel> options = new FirebaseRecyclerOptions.Builder<MovieModel>()
                .setQuery(rootRef.child("Movies"), MovieModel.class)
                .build();
        movieAdapter = new MovieAdapter(options, this);
        rvMovies.setAdapter(movieAdapter);

    }

    private void LoadFeatured() {
        FirebaseRecyclerOptions<MovieModel> options = new FirebaseRecyclerOptions.Builder<MovieModel>()
                .setQuery(rootRef.child("Movies").orderByChild("featured").equalTo("Yes"), MovieModel.class)
                .build();
        featureAdapter = new FeatureAdapter(options, this);
        rvFeatured.setAdapter(featureAdapter);


    }

    private void LoadSeries() {
        FirebaseRecyclerOptions<SeriesModel> options = new FirebaseRecyclerOptions.Builder<SeriesModel>()
                .setQuery(rootRef.child("Webseries"), SeriesModel.class)
                .build();
        seriesAdapter = new SeriesAdapter(options, this);
        rvSeries.setAdapter(seriesAdapter);

    }


    public void ViewAllSeries(View view) {
        Intent intent = new Intent(this, ViewAllActivity.class);
        intent.putExtra("type", "Webseries");
        startActivity(intent);
        Animatoo.animateFade(this);
    }

    public void ViewAllMovies(View view) {
        Intent intent = new Intent(this, ViewAllActivity.class);
        intent.putExtra("type", "Movies");
        startActivity(intent);
        Animatoo.animateFade(this);
    }

    @Override
    public void onBackPressed() {
        CloseApp();
    }

    private void CloseApp() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }

    @Override
    protected void onStart() {
        super.onStart();
        movieAdapter.startListening();
        featureAdapter.startListening();
        seriesAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        movieAdapter.stopListening();
        featureAdapter.stopListening();
        seriesAdapter.stopListening();
    }
}