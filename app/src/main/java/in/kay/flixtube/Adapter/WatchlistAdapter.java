package in.kay.flixtube.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import in.kay.flixtube.Model.WatchlistModel;
import in.kay.flixtube.R;

public class WatchlistAdapter extends FirebaseRecyclerAdapter<WatchlistModel, WatchlistAdapter.Viewholder> {
    Context context;
    String membership;
    DatabaseReference rootRef;

    public WatchlistAdapter(@NonNull FirebaseRecyclerOptions<WatchlistModel> options, Context context) {
        super(options);
        this.context = context;
    }


    @Override
    protected void onBindViewHolder(@NonNull final Viewholder holder, int position, @NonNull final WatchlistModel model) {
        GetData(model,holder);


    }

    private void GetData(final WatchlistModel model, final Viewholder holder) {
        rootRef=FirebaseDatabase.getInstance().getReference();
        rootRef.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                membership = snapshot.child("Membership").getValue(String.class);
                LoadData(model,holder);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void LoadData(final WatchlistModel model, final Viewholder holder) {
        Typeface medium = Typeface.createFromAsset(context.getAssets(), "Gilroy-ExtraBold.ttf");
        holder.title.setText(model.getTitle());
        holder.title.setTypeface(medium);
        Picasso.get()
                .load(model.getImage())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.img, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                        Picasso.get()
                                .load(model.getImage())
                                .into(holder.img);
                    }
                });
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.watch_list, parent, false);
        return new Viewholder(view);
    }

    class Viewholder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView img;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_movie_name);
            img = itemView.findViewById(R.id.iv_cover_img);
        }
    }
}
