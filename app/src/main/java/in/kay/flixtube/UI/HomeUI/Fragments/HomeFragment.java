package in.kay.flixtube.UI.HomeUI.Fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
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
import com.razorpay.PaymentResultListener;
import com.sdsmdg.tastytoast.TastyToast;

import in.kay.flixtube.Adapter.FeatureAdapter;
import in.kay.flixtube.Adapter.MovieAdapter;
import in.kay.flixtube.Adapter.SeriesAdapter;
import in.kay.flixtube.Model.MovieModel;
import in.kay.flixtube.Model.SeriesModel;
import in.kay.flixtube.R;
import in.kay.flixtube.UI.HomeUI.ViewAllActivity;
import in.kay.flixtube.UI.IntroUI.LandingActivity;
import in.kay.flixtube.Utils.Helper;

public class HomeFragment extends Fragment implements PaymentResultListener {

    View view;
    DatabaseReference rootRef;
    TextView tvName, tvFeatured, tvMovies, tvSeries;
    RecyclerView rvFeatured, rvMovies, rvSeries;
    MovieAdapter movieAdapter;
    FeatureAdapter featureAdapter;
    SeriesAdapter seriesAdapter;
    int size;
    Helper helper;
    String name, violation, membership, mobileUid, email;
    FirebaseAuth mAuth;


    private void INITZ() {
        rootRef = FirebaseDatabase.getInstance().getReference();
        helper = new Helper();
        mAuth = FirebaseAuth.getInstance();
        InitzAll();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        CheckInternet();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    private void initz() {
        LoadViews();
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
                view.findViewById(R.id.progressBar).setVisibility(View.GONE);
                view.findViewById(R.id.rr).setVisibility(View.VISIBLE);
            }
        }, 3000);
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
                if (strmobileUid.equalsIgnoreCase(helper.deviceId(getContext()))) {
                    view.findViewById(R.id.nsv_main).setVisibility(View.VISIBLE);
                    initz();
                } else {
                    PopUp();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void PopUp() {
        SendSecurityMail(helper.decryptedMsg(name, email));
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "Gilroy-ExtraBold.ttf");
        new iOSDialogBuilder(getContext())
                .setTitle("Privacy Issue")
                .setSubtitle("This isn't your device. Due to security permission, we can't let you to use this account.")
                .setCancelable(false)
                .setFont(font)
                .setPositiveListener(getString(R.string.ok), new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        dialog.dismiss();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getContext(), LandingActivity.class));
                    }
                })
                .build().show();
    }

    private void SendSecurityMail(String mail) {
        BackgroundMail.newBuilder(getContext())
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
                        "                                                                <p style=\"font-weight:300;color:#fff;font-family: 'Poppins', sans-serif;font-size: 2rem;\">Your account get logged into " + helper.getDeviceInfo(getContext()) + "</p>\n" +
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

    private void CheckInternet() {
        helper = new Helper();
        if (helper.isNetwork(getContext())) {
            INITZ();
        } else {
            Typeface font = Typeface.createFromAsset(getContext().getAssets(), "Gilroy-ExtraBold.ttf");
            new iOSDialogBuilder(getContext())
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

    private void LoadViews() {
        /////
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "Gilroy-ExtraBold.ttf");
        /////
        tvName = view.findViewById(R.id.tv_name);
        tvFeatured = view.findViewById(R.id.tv_featured);
        tvSeries = view.findViewById(R.id.tv_series);
        tvMovies = view.findViewById(R.id.tv_movies);
        /////
        rvFeatured = view.findViewById(R.id.rv_featured);
        rvSeries = view.findViewById(R.id.rv_series);
        rvMovies = view.findViewById(R.id.rv_movies);
        /////
        rvMovies.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true));
        rvFeatured.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvFeatured.setOnFlingListener(null);
        SnapHelper snapHelpernew = new PagerSnapHelper();
        snapHelpernew.attachToRecyclerView(rvFeatured);
        rvSeries.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true));
        /////
        tvName.setTypeface(font);
        tvName.setText("Hey, " + name);
        tvFeatured.setTypeface(font);
        tvMovies.setTypeface(font);
        tvSeries.setTypeface(font);
        if (membership.equalsIgnoreCase("VIP")) {
            view.findViewById(R.id.crown).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.crown).setVisibility(View.GONE);
        }
        view.findViewById(R.id.tv_movie_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewAllMovies(view);
            }
        });
        view.findViewById(R.id.tv_series_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewAllSeries(view);
            }
        });
    }

    private void LoadMovies() {
        FirebaseRecyclerOptions<MovieModel> options = new FirebaseRecyclerOptions.Builder<MovieModel>()
                .setQuery(rootRef.child("Movies").limitToLast(5), MovieModel.class)
                .build();
        movieAdapter = new MovieAdapter(options, getContext());
        rvMovies.setAdapter(movieAdapter);
        movieAdapter.startListening();
    }

    private void LoadFeatured() {
        FirebaseRecyclerOptions<MovieModel> options = new FirebaseRecyclerOptions.Builder<MovieModel>()
                .setQuery(rootRef.child("Movies").orderByChild("featured").equalTo("Yes"), MovieModel.class)
                .build();
        featureAdapter = new FeatureAdapter(options, getContext());
        rvFeatured.setAdapter(featureAdapter);
        featureAdapter.startListening();
    }

    private void LoadSeries() {
        FirebaseRecyclerOptions<SeriesModel> options = new FirebaseRecyclerOptions.Builder<SeriesModel>()
                .setQuery(rootRef.child("Webseries").limitToLast(5), SeriesModel.class)
                .build();
        seriesAdapter = new SeriesAdapter(options, getContext());
        rvSeries.setAdapter(seriesAdapter);
        seriesAdapter.startListening();
    }


    public void ViewAllSeries(View view) {
        Intent intent = new Intent(getContext(), ViewAllActivity.class);
        intent.putExtra("type", "Webseries");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        Animatoo.animateFade(getContext());
    }

    public void ViewAllMovies(View view) {
        Intent intent = new Intent(getContext(), ViewAllActivity.class);
        intent.putExtra("type", "Movies");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        Animatoo.animateFade(getContext());
    }


    @Override
    public void onPaymentSuccess(String s) {
        rootRef.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Membership").setValue("VIP").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                TastyToast.makeText(getContext(), "Welcome to Flixtube VIP club...", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                TastyToast.makeText(getContext(), "Server down. Error : " + e, TastyToast.LENGTH_LONG, TastyToast.ERROR);
            }
        });
    }


    @Override
    public void onPaymentError(int i, String s) {
        TastyToast.makeText(getContext(), "Payment cancelled.", TastyToast.LENGTH_LONG, TastyToast.ERROR);
    }


}