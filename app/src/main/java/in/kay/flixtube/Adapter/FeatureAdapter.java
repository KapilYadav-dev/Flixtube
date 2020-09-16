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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import in.kay.flixtube.Model.MovieModel;
import in.kay.flixtube.R;

public class FeatureAdapter extends FirebaseRecyclerAdapter<MovieModel, FeatureAdapter.FeatureAdapterViewHolder> {
Context context;

    public FeatureAdapter(@NonNull FirebaseRecyclerOptions<MovieModel> options, Context context) {
        super(options);
        this.context = context;
    }

    public FeatureAdapter(@NonNull FirebaseRecyclerOptions<MovieModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final FeatureAdapterViewHolder holder, int position, @NonNull final MovieModel model) {
        Typeface medium = Typeface.createFromAsset(context.getAssets(), "Gilroy-Medium.ttf");
        Typeface regular = Typeface.createFromAsset(context.getAssets(), "Gilroy-Regular.ttf");
        holder.title.setText(model.getTitle());
        holder.title.setTypeface(medium);
        holder.genre.setText(model.getCategory());
        holder.genre.setTypeface(regular);
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
        if (model.getType().equalsIgnoreCase("free"))
        {
            holder.vip.setVisibility(View.GONE);
        }

    }

    @NonNull
    @Override
    public FeatureAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feature_view, parent, false);
        return new FeatureAdapterViewHolder(view);
    }

    public class FeatureAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView title, genre;
        ImageView img,vip;

        public FeatureAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_movie_name);
            genre = itemView.findViewById(R.id.tv_genre);
            img = itemView.findViewById(R.id.iv_cover_img);
            vip=itemView.findViewById(R.id.iv_vip);
        }
    }
}
