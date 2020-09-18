package in.kay.flixtube.Adapter;

import android.content.Context;
import android.content.Intent;
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

import in.kay.flixtube.Model.SeriesModel;
import in.kay.flixtube.R;
import in.kay.flixtube.UI.PlayerActivity;

public class SeriesPlayAdapter extends FirebaseRecyclerAdapter<SeriesModel, SeriesPlayAdapter.SeriesPlayAdapterViewModel> {
    Context context;
    String image;

    public SeriesPlayAdapter(@NonNull FirebaseRecyclerOptions<SeriesModel> options, Context context, String image) {
        super(options);
        this.context = context;
        this.image = image;
    }

    @Override
    protected void onBindViewHolder(@NonNull final SeriesPlayAdapterViewModel holder, int position, @NonNull final SeriesModel model) {
        holder.episode.setText("Episode " + model.getEpisode());
        holder.season.setText("Season " + model.getSeason());
        Picasso.get()
                .load(image)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                        Picasso.get()
                                .load(image)
                                .into(holder.imageView);
                    }
                });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PlayerActivity.class);
                intent.putExtra("url", model.getUrl());
                intent.putExtra("title", "Episode " + model.getEpisode());
                view.getContext().startActivity(intent);
            }
        });

    }

    @NonNull
    @Override
    public SeriesPlayAdapterViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.series_view, parent, false);
        return new SeriesPlayAdapterViewModel(view);
    }

    class SeriesPlayAdapterViewModel extends RecyclerView.ViewHolder {
        TextView season, episode;
        ImageView imageView;

        public SeriesPlayAdapterViewModel(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_cover_img);
            season = itemView.findViewById(R.id.tv_season);
            episode = itemView.findViewById(R.id.tv_episode);
        }
    }
}
