package in.kay.flixtube.UI.HomeUI.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.sdsmdg.tastytoast.TastyToast;

import in.kay.flixtube.Adapter.WatchlistAdapter;
import in.kay.flixtube.Model.MovieModel;
import in.kay.flixtube.Model.WatchlistModel;
import in.kay.flixtube.R;

public class Watchlist extends Fragment {
    View view;
    Dialog dialog;
    RecyclerView recyclerView;
    WatchlistAdapter adapter;
    Context mcontext;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        dialog = new Dialog(mcontext);
        recyclerView=view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(mcontext, LinearLayoutManager.VERTICAL, false));
        ShowPopUp();
    }

    private void ShowPopUp() {
        Button movie,series;
        dialog.setContentView(R.layout.pop_up);
        movie=dialog.findViewById(R.id.btn_movie);
        series=dialog.findViewById(R.id.btn_series);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        series.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                DoWork("Webseries");
            }
        });
        movie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                DoWork("Movies");
            }
        });
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    TastyToast.makeText(getActivity(), "Please choose a value to continue..", TastyToast.LENGTH_SHORT,TastyToast.INFO);
                    return true;
                }
                return false;
            }
        });

    }

    private void DoWork(String string) {
        FirebaseRecyclerOptions<WatchlistModel> options = new FirebaseRecyclerOptions.Builder<WatchlistModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Watchlist").child(string), WatchlistModel.class)
                .build();
        adapter=new WatchlistAdapter(options,mcontext);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_watchlist, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mcontext=context;
    }
}