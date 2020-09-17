package in.kay.flixtube.UI;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import in.kay.flixtube.Adapter.AllAdapter;
import in.kay.flixtube.Model.MovieModel;
import in.kay.flixtube.R;

public class ViewAllActivity extends AppCompatActivity {
    RecyclerView rvAll;
    AllAdapter allAdapter;
    DatabaseReference rootRef;
    EditText etQuery;
    ImageView ivSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String type=getIntent().getStringExtra("type");
        setContentView(R.layout.activity_view_all);
        initz();
        LoadData("", type);
        SearchEvent(type);
    }

    private void SearchEvent(final String type) {
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strQuery=etQuery.getText().toString();
                LoadData(strQuery, type);
            }
        });
    }

    private void LoadData(String strQuery, String type) {
        FirebaseRecyclerOptions<MovieModel> options = new FirebaseRecyclerOptions.Builder<MovieModel>()
                .setQuery(rootRef.child(type).orderByChild("title").startAt(strQuery).endAt(strQuery +"\uf8ff"), MovieModel.class)
                .build();
        allAdapter = new AllAdapter(options, this, type);
        rvAll.setAdapter(allAdapter);
        allAdapter.startListening();
    }

    private void initz() {
        rootRef = FirebaseDatabase.getInstance().getReference();
        rvAll = findViewById(R.id.rv_all);
        rvAll.setLayoutManager(new GridLayoutManager(this, 2));
        etQuery=findViewById(R.id.et_query);
        ivSearch=findViewById(R.id.iv_search);
    }
}