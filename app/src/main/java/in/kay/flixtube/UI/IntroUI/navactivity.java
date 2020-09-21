package in.kay.flixtube;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class navactivity extends AppCompatActivity {

    NavigationView nav;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    TextView username;
    TextView  useremail;
    FirebaseAuth mAuth;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navactivity);
        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        nav=(NavigationView)findViewById(R.id.navbar);
        drawer =(DrawerLayout)findViewById(R.id.drawer);
        useremail=findViewById(R.id.textemail);
        //useremail.setText(mAuth.getCurrentUser().getEmail());

        toggle= new ActionBarDrawerToggle(this,drawer,toolbar,R.string.open,R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch(menuItem.getItemId())
                {
                    case R.id.home:
                        Toast.makeText(navactivity.this, "Home section open",Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.downloads:
                        Toast.makeText(navactivity.this, "Downloads section open",Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.watchlist:
                        Toast.makeText(navactivity.this, "Watchlist section open",Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.membership:
                        Toast.makeText(navactivity.this, "Membership section open",Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.settings:
                        Toast.makeText(navactivity.this, "Settings section open",Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.help:
                        Toast.makeText(navactivity.this, "Help section open",Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.logout:
                        drawer.closeDrawer(GravityCompat.START);
                        final AlertDialog.Builder logoutdialog= new AlertDialog.Builder(navactivity.this);
                        logoutdialog.setTitle("Logout Box");
                        logoutdialog.setMessage("Are you sure to logout?");
                        logoutdialog.setCancelable(false);

                        logoutdialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                Toast.makeText(navactivity.this,"Signed-out",Toast.LENGTH_SHORT).show();
                                Intent intent= new Intent(navactivity.this,landingactivity.class);
                                startActivity(intent);
                            }
                        });
                        logoutdialog.create().show();
                        break;
                                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }


}