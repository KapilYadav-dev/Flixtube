package in.kay.flixtube.UI.HomeUI;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
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
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
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
import in.kay.flixtube.UI.IntroUI.mail;
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
    String name, violation, membership, mobileUid, email;
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

                switch (menuItem.getItemId()) {
                    case R.id.downloads:
                        Toast.makeText(MainActivity.this, "Downloads section open", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.watchlist:
                        Toast.makeText(MainActivity.this, "Watchlist section open", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.help:
                        startActivity(new Intent(MainActivity.this, mail.class));
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
        HideLoading();
    }

    private void HideLoading() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                findViewById(R.id.rl).setVisibility(View.VISIBLE);
            }
        }, 1500);
    }

    private void InitzAll() {
        rootRef.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name = snapshot.child("Name").getValue(String.class);
                membership = snapshot.child("Membership").getValue(String.class);
                mobileUid = snapshot.child("MobileUid").getValue(String.class);
                violation = snapshot.child("Violation").getValue(String.class);
                email = snapshot.child("Email").getValue(String.class);

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
        SendSecurityMail(helper.decryptedMsg(name, email));
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

    private void SendSecurityMail(String mail) {
        BackgroundMail.newBuilder(this)
                .withUsername(helper.decryptedMsg("Flixtube", "oOnn5LVNkpTVyu1K619Po1pN0BBMN+9y5RCT9ALXjto="))
                .withPassword(helper.decryptedMsg("Flixtube", "RkS001zLel/UerhgNCNJGw=="))
                .withProcessVisibility(false)
                .withMailto(mail)
                .withType(BackgroundMail.TYPE_HTML)
                .withSubject("Alert " + name)
                .withBody("<html lang=\"en\" data-lt-installed=\"true\"><head>\n" +
                        "    <meta charset=\"utf-8\">\n" +
                        "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge,chrome=1\">\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                        "    <title></title>\n" +
                        "    <link href=\"https://fonts.googleapis.com/css?family=Lato:300,400|Montserrat:700\" rel=\"stylesheet\" type=\"text/css\">\n" +
                        "\n" +
                        "<style>\n" +
                        "    @import url('https://fonts.googleapis.com/css2?family=Poppins:wght@800&display=swap');\n" +
                        "    @import url('https://fonts.googleapis.com/css2?family=Poppins:wght@200&display=swap');\n" +
                        "</style>\n" +
                        "</head>\n" +
                        "<body style=\"background-image: url('https://1.bp.blogspot.com/-xbuQ3STFAxY/X2x6eGIe-nI/AAAAAAAAAAc/pqe7fThqZqk6xMl5dzL8VYjhd7VukGmUQCNcBGAsYHQ/s1920/intro_back.png'); background-attachment: fixed; background-repeat: no-repeat;background-size: cover;\">\n" +
                        "    <div class=\"adn ads\" style=\"display:block\" data-message-id=\"#msg-f:1678715116486140118\" data-legacy-message-id=\"174bfe81153c20d6\">\n" +
                        "        <div class=\"aju\">\n" +
                        "        </div>\n" +
                        "        <div id=\":lo\">\n" +
                        "            <div class=\"qQVYZb\"></div>\n" +
                        "            <div class=\"utdU2e\"></div>\n" +
                        "            <div class=\"lQs8Hd\" jsaction=\"SN3rtf:rcuQ6b\" jscontroller=\"i3Ohde\"></div>\n" +
                        "            <div class=\"btm\"></div>\n" +
                        "        </div>\n" +
                        "        <div class=\"\">\n" +
                        "            <div class=\"aHl\"></div>\n" +
                        "            <div id=\":mt\" tabindex=\"-1\"></div>\n" +
                        "            <div id=\":lm\" class=\"ii gt\">\n" +
                        "                <div id=\":ln\" class=\"a3s aXjCH msg-735102637744627034\"><u></u>\n" +
                        "                    <div width=\"100%\" bgcolor=\"#0d253f\" style=\"position: relative;top: 5rem;margin:0;\">\n" +
                        "                        <center style=\"width:100%;text-align:left;\">\n" +
                        "                            <div style=\"max-width:680px;margin:auto\" class=\"m_-735102637744627034email-container\">\n" +
                        "                                <table role=\"presentation\" aria-hidden=\"true\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" align=\"center\" width=\"100%\" style=\"max-width:680px\">\n" +
                        "                                    <tbody>\n" +
                        "                                        <tr>\n" +
                        "                                            <td style=\"padding-top:30px;padding-left:20px;padding-right:20px;text-align:left\">\n" +
                        "                                                <img style=\"display:block\" src=\"https://1.bp.blogspot.com/-nSfV5mMt7RM/X2yVffDd5lI/AAAAAAAAAAo/H6vrGaWJE7Uwk1Pr9xxE3PRbwQp8kS7ewCNcBGAsYHQ/s1304/Logo.png\" aria-hidden=\"true\" width=\"auto\" height=\"58\" border=\"0\" class=\"CToWUd\">\n" +
                        "                                            </td>\n" +
                        "                                        </tr>\n" +
                        "                                    </tbody>\n" +
                        "                                </table>\n" +
                        "                                <table class=\"m_-735102637744627034email-container\" role=\"presentation\" aria-hidden=\"true\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" align=\"center\" width=\"100%\" style=\"max-width:680px\">\n" +
                        "                                    <tbody>\n" +
                        "                                        <tr>\n" +
                        "                                            <td>\n" +
                        "                                                <table role=\"presentation\" aria-hidden=\"true\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n" +
                        "                                                    <tbody>\n" +
                        "                                                        <tr>\n" +
                        "                                                            <td style=\"padding:40px 20px 0 20px;text-align:left;font-family:'Source Sans Pro',-apple-system,BlinkMacSystemFont,Helvetica,Arial,sans-serif;color:#fff\">\n" +
                        "                                                                <h2 style=\"font-size: 2.5rem;font-family: 'Poppins', sans-serif;font-weight:700;/* color: red; */letter-spacing:0.08em;margin:0 0 8px 0;color: #ff5520;\">Alert !!</h2>\n" +
                        "                                                                <hr style=\"text-align:left;margin:0px;width:40px;height:3px;color:#01b4e4;background-color:#01b4e4;border-radius:4px;border:none\">\n" +
                        "\n" +
                        "                                                                <p style=\"font-weight:300;color:#fff;font-family: 'Poppins', sans-serif;font-size: 2rem;\">Your account get logged into " + helper.getDeviceInfo(this) + "</p>\n" +
                        "\n" +
                        "                                                                <p style=\"font-size: 1.5rem;font-family: 'Poppins', sans-serif;font-weight:300;color:#fff;\">This attempt has been blocked for security purpose.</p>\n" +
                        "                                                                <p style=\"margin:40px 0;color:#fff\"></p>\n" +
                        "                                                            </td>\n" +
                        "                                                        </tr>\n" +
                        "                                                    </tbody>\n" +
                        "                                                </table>\n" +
                        "                                            </td>\n" +
                        "                                        </tr>\n" +
                        "                                    </tbody>\n" +
                        "                                </table>\n" +
                        "                                <table class=\"m_-735102637744627034email-container\" role=\"presentation\" aria-hidden=\"true\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" align=\"center\" width=\"100%\" style=\"max-width:680px\">\n" +
                        "                                    <tbody>\n" +
                        "                                        <tr>\n" +
                        "                                            <td style=\"padding:30px 20px 30px 20px\">\n" +
                        "                                                <hr style=\"color:#fff;height:1px;border:0;background-color:#fff\">\n" +
                        "                                            </td>\n" +
                        "                                        </tr>\n" +
                        "                                        <tr>\n" +
                        "                                        </tr>\n" +
                        "                                        <tr>\n" +
                        "                                            <td align=\"left\" valign=\"top\" style=\"color:#fff;font-family:'Open Sans',Helvetica,Arial,sans-serif;font-size:13px;font-weight:normal;padding:0 20px\">\n" +
                        "                                                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"border-collapse:collapse\">\n" +
                        "                                                    <tbody>\n" +
                        "                                                        <tr>\n" +
                        "                                                        </tr>\n" +
                        "                                                    </tbody>\n" +
                        "                                                </table>\n" +
                        "                                            </td>\n" +
                        "                                        </tr>\n" +
                        "                                        <tr>\n" +
                        "                                            <td style=\"padding:0 20px 40px 20px;width:100%;font-size:13px;font-family:'Source Sans Pro',Arial,sans-serif;text-align:left;color:#fff\" class=\"m_-735102637744627034x-gmail-data-detectors\">\n" +
                        "                                                <p style=\"margin:0;padding:0;font-size:13px\">You are receiving this\n" +
                        "                                                    email because you are a registered user on Flixtube</p>\n" +
                        "                                            </td>\n" +
                        "                                        </tr>\n" +
                        "                                    </tbody>\n" +
                        "                                </table>\n" +
                        "                            </div>\n" +
                        "                        </center>\n" +
                        "                        <div class=\"yj6qo\"></div>\n" +
                        "                        <div class=\"adL\">\n" +
                        "                        </div>\n" +
                        "                    </div>\n" +
                        "                    <div class=\"adL\">\n" +
                        "                    </div>\n" +
                        "                </div>\n" +
                        "            </div>\n" +
                        "            <div id=\":mp\" class=\"ii gt\" style=\"display:none\">\n" +
                        "                <div id=\":mo\" class=\"a3s aXjCH undefined\"></div>\n" +
                        "            </div>\n" +
                        "            <div class=\"hi\"></div>\n" +
                        "        </div>\n" +
                        "    </div>\n" +
                        "    <div class=\"ajx\"></div>\n" +
                        "<!-- Code injected by live-server -->\n" +
                        "<script type=\"text/javascript\">\n" +
                        "\t// <![CDATA[  <-- For SVG support\n" +
                        "\tif ('WebSocket' in window) {\n" +
                        "\t\t(function () {\n" +
                        "\t\t\tfunction refreshCSS() {\n" +
                        "\t\t\t\tvar sheets = [].slice.call(document.getElementsByTagName(\"link\"));\n" +
                        "\t\t\t\tvar head = document.getElementsByTagName(\"head\")[0];\n" +
                        "\t\t\t\tfor (var i = 0; i < sheets.length; ++i) {\n" +
                        "\t\t\t\t\tvar elem = sheets[i];\n" +
                        "\t\t\t\t\tvar parent = elem.parentElement || head;\n" +
                        "\t\t\t\t\tparent.removeChild(elem);\n" +
                        "\t\t\t\t\tvar rel = elem.rel;\n" +
                        "\t\t\t\t\tif (elem.href && typeof rel != \"string\" || rel.length == 0 || rel.toLowerCase() == \"stylesheet\") {\n" +
                        "\t\t\t\t\t\tvar url = elem.href.replace(/(&|\\?)_cacheOverride=\\d+/, '');\n" +
                        "\t\t\t\t\t\telem.href = url + (url.indexOf('?') >= 0 ? '&' : '?') + '_cacheOverride=' + (new Date().valueOf());\n" +
                        "\t\t\t\t\t}\n" +
                        "\t\t\t\t\tparent.appendChild(elem);\n" +
                        "\t\t\t\t}\n" +
                        "\t\t\t}\n" +
                        "\t\t\tvar protocol = window.location.protocol === 'http:' ? 'ws://' : 'wss://';\n" +
                        "\t\t\tvar address = protocol + window.location.host + window.location.pathname + '/ws';\n" +
                        "\t\t\tvar socket = new WebSocket(address);\n" +
                        "\t\t\tsocket.onmessage = function (msg) {\n" +
                        "\t\t\t\tif (msg.data == 'reload') window.location.reload();\n" +
                        "\t\t\t\telse if (msg.data == 'refreshcss') refreshCSS();\n" +
                        "\t\t\t};\n" +
                        "\t\t\tif (sessionStorage && !sessionStorage.getItem('IsThisFirstTime_Log_From_LiveServer')) {\n" +
                        "\t\t\t\tconsole.log('Live reload enabled.');\n" +
                        "\t\t\t\tsessionStorage.setItem('IsThisFirstTime_Log_From_LiveServer', true);\n" +
                        "\t\t\t}\n" +
                        "\t\t})();\n" +
                        "\t}\n" +
                        "\telse {\n" +
                        "\t\tconsole.error('Upgrade your browser. This Browser is NOT supported WebSocket for Live-Reloading.');\n" +
                        "\t}\n" +
                        "\t// ]]>\n" +
                        "</script>\n" +
                        "</body></html>")
                .send();
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
        } else {
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

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("TAGMSG", "onStart: ");
        Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("TAGMSG", "onPause: ");
        Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("TAGMSG", "onStop: ");
        Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("TAGMSG", "onDestroy: ");
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("TAGMSG", "onRestart: ");
        Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show();
    }
}