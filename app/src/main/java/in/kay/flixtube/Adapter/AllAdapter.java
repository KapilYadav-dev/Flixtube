package in.kay.flixtube.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import in.kay.flixtube.Model.MovieModel;
import in.kay.flixtube.R;
import in.kay.flixtube.UI.HomeUI.DetailActivity;

public class AllAdapter extends FirebaseRecyclerAdapter<MovieModel, AllAdapter.AllAdapterViewHolder> {

    Context context;
    String type;

    public AllAdapter(@NonNull FirebaseRecyclerOptions<MovieModel> options, Context context, String type) {
        super(options);
        this.context = context;
        this.type = type;
    }


    @Override
    protected void onBindViewHolder(@NonNull final AllAdapterViewHolder holder, final int position, @NonNull final MovieModel model) {
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
        if (type.equalsIgnoreCase("Movies")) {

            if (model.getType().equalsIgnoreCase("free")) {
                holder.vip.setVisibility(View.GONE);
            } else {
                holder.vip.setVisibility(View.VISIBLE);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DetailActivity.class);
                intent.putExtra("imdb", model.getImdb());
                intent.putExtra("type", type);
                intent.putExtra("movieType", model.getType());
                intent.putExtra("trailer", model.getTrailer());
                intent.putExtra("url", model.getUrl());
                intent.putExtra("key", getRef(position).getKey());
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                view.getContext().startActivity(intent);
                Animatoo.animateSlideLeft(context);
            }
        });
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        if (getItemCount()==0)
        {
            TastyToast.makeText(context,"No Result found",TastyToast.LENGTH_LONG,TastyToast.INFO);
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @NonNull
    @Override
    public AllAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_view, parent, false);
        return new AllAdapterViewHolder(view);
    }

    class AllAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView title, genre;
        ImageView img, vip;

        public AllAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_movie_name);
            genre = itemView.findViewById(R.id.tv_genre);
            img = itemView.findViewById(R.id.iv_cover_img);
            vip = itemView.findViewById(R.id.iv_vip);
            vip.setVisibility(View.GONE);
        }
    }
}
