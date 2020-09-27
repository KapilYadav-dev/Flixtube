package in.kay.flixtube.UI.HomeUI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.tastytoast.TastyToast;

import in.kay.flixtube.R;
import in.kay.flixtube.UI.HomeUI.Fragments.Download;
import in.kay.flixtube.UI.HomeUI.Fragments.Help;
import in.kay.flixtube.UI.HomeUI.Fragments.Home;
import in.kay.flixtube.UI.IntroUI.LandingActivity;
import in.kay.flixtube.Utils.Helper;

public class MainActivity extends AppCompatActivity {
    DatabaseReference rootRef;
    Helper helper;
    NavigationView nav;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    TextView username, useremail;
    String name, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootRef = FirebaseDatabase.getInstance().getReference();
        helper = new Helper();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new Home()).commit();
        }
        ReadValueFromDatabase();
    }

    private void ReadValueFromDatabase() {
        rootRef.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name = snapshot.child("Name").getValue(String.class);
                email = snapshot.child("Email").getValue(String.class);
                NavigationInitz();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void NavigationInitz() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        nav = (NavigationView) findViewById(R.id.navbar);
        nav.setItemIconTintList(null);
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        useremail = findViewById(R.id.textemail);
        useremail.setText(helper.decryptedMsg(name, email));
        username = findViewById(R.id.textname);
        username.setText(name);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        selectedFragment = new Home();
                        break;
                    case R.id.downloads:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
                        } else {
                            selectedFragment = new Download();
                        }
                        break;
                    case R.id.watchlist:
                        Toast.makeText(MainActivity.this, "Watchlist section open", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.help:
                        selectedFragment = new Help();
                        break;
                    case R.id.logout:
                        selectedFragment = null;
                        drawer.closeDrawer(GravityCompat.START);
                        LogoutPop();
                        break;
                    case R.id.membership:
                        selectedFragment = null;
                        //  BuyPopUp();
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                }
                return true;
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                TastyToast.makeText(this, "Permission successfully granted...", Toast.LENGTH_SHORT, TastyToast.SUCCESS);

            } else {
                TastyToast.makeText(this, "Please allow us permission..", Toast.LENGTH_SHORT, TastyToast.CONFUSING);
                finish();
            }
        }
    }

    private void LogoutPop() {
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "Gilroy-ExtraBold.ttf");
        new iOSDialogBuilder(this)
                .setTitle("See you soon,")
                .setSubtitle("Ohh no! You're leaving...\nAre you sure?")
                .setCancelable(false)
                .setFont(font)
                .setPositiveListener(getString(R.string.ok), new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        dialog.dismiss();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplication(), LandingActivity.class));
                    }
                })
                .setNegativeListener(getString(R.string.dismiss), new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        nav.setCheckedItem(R.id.home);
                        dialog.dismiss();
                    }
                })
                .build().show();
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
}