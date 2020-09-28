package in.kay.flixtube.UI.HomeUI;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import in.kay.flixtube.R;
import in.kay.flixtube.UI.HomeUI.Fragments.Download;
import in.kay.flixtube.UI.HomeUI.Fragments.Help;
import in.kay.flixtube.UI.HomeUI.Fragments.Home;
import in.kay.flixtube.UI.HomeUI.Fragments.Watchlist;
import in.kay.flixtube.UI.IntroUI.LandingActivity;
import in.kay.flixtube.Utils.Helper;

public class MainActivity extends AppCompatActivity implements PaymentResultListener {
    DatabaseReference rootRef;
    Helper helper;
    NavigationView nav;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    TextView username, useremail;
    String name, email,membership;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootRef = FirebaseDatabase.getInstance().getReference();
        helper = new Helper();
        dialog=new Dialog(this);
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
                membership=snapshot.child("Membership").getValue(String.class);
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
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_hamburger);
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
                               selectedFragment=new Download();
                        }
                        break;
                    case R.id.watchlist:
                        selectedFragment = new Watchlist();
                        break;
                    case R.id.help:
                        selectedFragment = new Help();
                        break;
                    case R.id.logout:
                        selectedFragment = null;
                        LogoutPop();
                        break;
                    case R.id.membership:
                        if (membership.equalsIgnoreCase("vip"))
                        {
                            TastyToast.makeText(getApplicationContext(),"You are already a premium member..",TastyToast.LENGTH_LONG,TastyToast.INFO);
                        }
                        else {
                            selectedFragment =null;
                            ShowPopup();
                        }
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment, null).addToBackStack(null).commit();
                }
                return true;
            }
        });
    }

    private void ShowPopup() {
        Button pay;
        TextView close;
        dialog.setContentView(R.layout.payement_pop_up);
        pay=dialog.findViewById(R.id.pay);
        close=dialog.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                DoPayment();
            }
        });

    }

    private void DoPayment() {
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                TastyToast.makeText(this, "Permission successfully granted...", Toast.LENGTH_SHORT, TastyToast.SUCCESS);

            } else {
                TastyToast.makeText(this, "Please allow us permission..", Toast.LENGTH_SHORT, TastyToast.CONFUSING);
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
        if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
            CloseApp();
        } else if (getSupportFragmentManager().getBackStackEntryCount()==0)
        {
            //Toast.makeText(this, "Only 1 left", Toast.LENGTH_SHORT).show();
            ShowDiag();
        }
        else {
            super.onBackPressed();

        }
    }

    private void ShowDiag() {
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "Gilroy-ExtraBold.ttf");
        new iOSDialogBuilder(this)
                .setTitle("Exit")
                .setSubtitle("Ohh no! You're leaving...\nAre you sure?")
                .setCancelable(false)
                .setFont(font)
                .setPositiveListener(getString(R.string.ok), new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        dialog.dismiss();
                        CloseApp();
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

    private void CloseApp() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
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
    }
}