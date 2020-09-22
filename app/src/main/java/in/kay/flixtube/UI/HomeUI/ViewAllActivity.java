package in.kay.flixtube.UI.HomeUI;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import in.kay.flixtube.Adapter.AllAdapter;
import in.kay.flixtube.Model.MovieModel;
import in.kay.flixtube.R;
import in.kay.flixtube.Utils.Helper;

public class ViewAllActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView rvAll;
    AllAdapter allAdapter;
    DatabaseReference rootRef;
    EditText etQuery;
    ImageView ivSearch;
    String  type;
    Helper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        type = getIntent().getStringExtra("type");
        helper=new Helper();
        CheckInternet();
    }

    private void InitzAll() {
        initz();
        LoadData("", type);
        SearchEvent(type);
        ClickEvents();
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


    private void ClickEvents() {
        findViewById(R.id.tv_adventure).setOnClickListener(this);
        findViewById(R.id.tv_horror).setOnClickListener(this);
        findViewById(R.id.tv_romance).setOnClickListener(this);
        findViewById(R.id.tv_Documentary).setOnClickListener(this);
        findViewById(R.id.tv_thriller).setOnClickListener(this);
        findViewById(R.id.tv_action).setOnClickListener(this);
        findViewById(R.id.tv_comedy).setOnClickListener(this);
        findViewById(R.id.tv_kids).setOnClickListener(this);
        findViewById(R.id.tv_sci_fi).setOnClickListener(this);
    }


    private void SearchEvent(final String type) {
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strQuery = etQuery.getText().toString();
                LoadData(strQuery, type);
            }
        });
    }

    private void LoadData(String strQuery, String type) {
        FirebaseRecyclerOptions<MovieModel> options = new FirebaseRecyclerOptions.Builder<MovieModel>()
                .setQuery(rootRef.child(type).orderByChild("title").startAt(strQuery).endAt(strQuery + "\uf8ff"), MovieModel.class)
                .build();
        allAdapter = new AllAdapter(options, this, type);
        rvAll.setAdapter(allAdapter);
        allAdapter.startListening();
    }

    private void initz() {
        rootRef = FirebaseDatabase.getInstance().getReference();
        rvAll = findViewById(R.id.rv_all);
        rvAll.setLayoutManager(new GridLayoutManager(this, 2));
        etQuery = findViewById(R.id.et_query);
        ivSearch = findViewById(R.id.iv_search);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateFade(this); //fire the slide left animation
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_horror:
                searchData("Horror");
                break;
            case R.id.tv_adventure:
                searchData("Adventure");
                break;
            case R.id.tv_action:
                searchData("Action");
                break;
            case R.id.tv_comedy:
                searchData("Comedy");
                break;
            case R.id.tv_thriller:
                searchData("Thriller");
                break;
            case R.id.tv_romance:
                searchData("Romance");
                break;
            case R.id.tv_sci_fi:
                searchData("Sci-fi");
                break;
            case R.id.tv_Documentary:
                searchData("Documentary");
                break;
            case R.id.tv_kids:
                searchData("Kids");
                break;
        }
    }

    private void searchData(String string) {
        FirebaseRecyclerOptions<MovieModel> options = new FirebaseRecyclerOptions.Builder<MovieModel>()
                .setQuery(rootRef.child(type).orderByChild("category").startAt(string).endAt(string + "\uf8ff"), MovieModel.class)
                .build();
        allAdapter = new AllAdapter(options, this, type);
        rvAll.setAdapter(allAdapter);
        allAdapter.startListening();
    }
}