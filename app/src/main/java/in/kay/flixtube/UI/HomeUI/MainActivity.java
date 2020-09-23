package in.kay.flixtube.UI.HomeUI;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialog;
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialogListener;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONObject;

import in.kay.flixtube.Adapter.FeatureAdapter;
import in.kay.flixtube.Adapter.MovieAdapter;
import in.kay.flixtube.Adapter.SeriesAdapter;
import in.kay.flixtube.Model.MovieModel;
import in.kay.flixtube.Model.SeriesModel;
import in.kay.flixtube.R;
import in.kay.flixtube.UI.IntroUI.LandingActivity;
import in.kay.flixtube.Utils.Helper;

public class MainActivity extends AppCompatActivity implements PaymentResultListener {
    DatabaseReference rootRef;
    TextView tvName, tvFeatured, tvMovies, tvSeries;
    RecyclerView rvFeatured, rvMovies, rvSeries;
    MovieAdapter movieAdapter;
    FeatureAdapter featureAdapter;
    SeriesAdapter seriesAdapter;
    int size;
    Helper helper;
    String name, violation, membership, mobileUid;
    NavigationView nav;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    TextView username;
    TextView useremail;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootRef = FirebaseDatabase.getInstance().getReference();
        helper = new Helper();
        CheckInternet();
        mAuth = FirebaseAuth.getInstance();
    }

    private void NavigationInitz() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        nav = (NavigationView) findViewById(R.id.navbar);
        nav.setItemIconTintList(null);
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        useremail = findViewById(R.id.textemail);
        //useremail.setText(mAuth.getCurrentUser().getEmail());

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.downloads:
                        Toast.makeText(MainActivity.this, "Downloads section open", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.watchlist:
                        Toast.makeText(MainActivity.this, "Watchlist section open", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.help:
                        Toast.makeText(MainActivity.this, "Help section open", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.logout:
                        drawer.closeDrawer(GravityCompat.START);
                        LogoutPop();
                        break;

                    case R.id.membership:
                        BuyPopUp();
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void BuyPopUp() {
        if (membership.equalsIgnoreCase("VIP")) {
            TastyToast.makeText(getApplicationContext(), "You are already a VIP user...", TastyToast.LENGTH_LONG, TastyToast.INFO);
        } else {
            new TTFancyGifDialog.Builder(MainActivity.this)
                    .setTitle("Membership")
                    .setMessage("Get access to all of Flixtube's amazing content at just Rs. 200. What are you waiting for? Get set subscribe !!")
                    .setPositiveBtnText("Subscribe")
                    .setPositiveBtnBackground("#22b573")
                    .setNegativeBtnText("Not now")
                    .setNegativeBtnBackground("#c1272d")
                    .setGifResource(R.drawable.subscription)      //pass your gif, png or jpg
                    .isCancellable(true)
                    .OnPositiveClicked(new TTFancyGifDialogListener() {
                        @Override
                        public void OnClick() {
                            BuyAccount();
                        }
                    })
                    .OnNegativeClicked(new TTFancyGifDialogListener() {
                        @Override
                        public void OnClick() {
                            TastyToast.makeText(MainActivity.this, "Cancel", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                        }
                    })
                    .build();
        }

    }

    private void BuyAccount() {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_iSJv7N9Z4dJo63");
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Flixtube");
            options.put("description", "Purchase premium Flixtube account");
            options.put("currency", "INR");
            String paisee = Integer.toString(Integer.parseInt("200") * 100);
            options.put("amount", paisee);
            checkout.open(MainActivity.this, options);
        } catch (Exception e) {
            TastyToast.makeText(getApplicationContext(), "Server error " + e, TastyToast.LENGTH_LONG, TastyToast.ERROR);
        }
    }

    private void LogoutPop() {
        Typeface font = Typeface.createFromAsset(this.getAssets(), "Gilroy-ExtraBold.ttf");
        new iOSDialogBuilder(MainActivity.this)
                .setTitle("See you soon,")
                .setSubtitle("Ohh no! You're leaving...\nAre you sure?")
                .setCancelable(false)
                .setFont(font)
                .setPositiveListener(getString(R.string.ok), new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        dialog.dismiss();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(MainActivity.this, LandingActivity.class));
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

    private void CheckInternet() {
        if (helper.isNetwork(this)) {
            InitzAll();
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

    private void initz() {
        LoadViews();
        NavigationInitz();
        LoadMovies();
        LoadFeatured();
        LoadSeries();
        GetSizeRV();
    }

    private void InitzAll() {
        rootRef.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name = snapshot.child("Name").getValue(String.class);
                membership = snapshot.child("Membership").getValue(String.class);
                mobileUid = snapshot.child("MobileUid").getValue(String.class);
                violation = snapshot.child("Violation").getValue(String.class);
                String strmobileUid = helper.decryptedMsg(name, mobileUid);
                if (strmobileUid.equalsIgnoreCase(helper.deviceId(MainActivity.this))) {
                    findViewById(R.id.nsv_main).setVisibility(View.VISIBLE);
                    initz();
                } else {
                    PopUp();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Log.d("UserValue", "Outside: " + name);
    }

    private void PopUp() {
        Typeface font = Typeface.createFromAsset(this.getAssets(), "Gilroy-ExtraBold.ttf");
        new iOSDialogBuilder(MainActivity.this)
                .setTitle("Privacy Issue")
                .setSubtitle("This isn't your device. Due to security permission, we can't let you to use this account.")
                .setCancelable(false)
                .setFont(font)
                .setPositiveListener(getString(R.string.ok), new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        dialog.dismiss();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(MainActivity.this, LandingActivity.class));
                    }
                })
                .build().show();
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
        rvFeatured.setOnFlingListener(null);
        SnapHelper snapHelpernew = new PagerSnapHelper();
        snapHelpernew.attachToRecyclerView(rvFeatured);
        rvSeries.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        /////
        tvName.setTypeface(font);
        tvName.setText("Hey, " + name);
        tvFeatured.setTypeface(font);
        tvMovies.setTypeface(font);
        tvSeries.setTypeface(font);
        if (membership.equalsIgnoreCase("VIP")) {
            findViewById(R.id.crown).setVisibility(View.VISIBLE);
        }
        else
        {
            findViewById(R.id.crown).setVisibility(View.GONE);
        }
    }

    private void LoadMovies() {
        FirebaseRecyclerOptions<MovieModel> options = new FirebaseRecyclerOptions.Builder<MovieModel>()
                .setQuery(rootRef.child("Movies").limitToLast(5), MovieModel.class)
                .build();
        movieAdapter = new MovieAdapter(options, this);
        rvMovies.setAdapter(movieAdapter);
        movieAdapter.startListening();
    }

    private void LoadFeatured() {
        FirebaseRecyclerOptions<MovieModel> options = new FirebaseRecyclerOptions.Builder<MovieModel>()
                .setQuery(rootRef.child("Movies").orderByChild("featured").equalTo("Yes"), MovieModel.class)
                .build();
        featureAdapter = new FeatureAdapter(options, this);
        rvFeatured.setAdapter(featureAdapter);
        featureAdapter.startListening();
    }

    private void LoadSeries() {
        FirebaseRecyclerOptions<SeriesModel> options = new FirebaseRecyclerOptions.Builder<SeriesModel>()
                .setQuery(rootRef.child("Webseries").limitToLast(5), SeriesModel.class)
                .build();
        seriesAdapter = new SeriesAdapter(options, this);
        rvSeries.setAdapter(seriesAdapter);
        seriesAdapter.startListening();
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
    protected void onResume() {
        super.onResume();
        CheckInternet();
    }


    @Override
    public void onPaymentSuccess(String s) {
        rootRef.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Membership").setValue("VIP").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                TastyToast.makeText(MainActivity.this, "Welcome to Flixtube VIP club...", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                TastyToast.makeText(MainActivity.this, "Server down. Error : " + e, TastyToast.LENGTH_LONG, TastyToast.ERROR);
            }
        });
    }


    @Override
    public void onPaymentError(int i, String s) {
        TastyToast.makeText(this, "Payment cancelled.", TastyToast.LENGTH_LONG, TastyToast.ERROR);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}