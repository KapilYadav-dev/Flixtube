package in.kay.flixtube.Adapter;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import in.kay.flixtube.Model.CastModel;
import in.kay.flixtube.R;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.Viewholder> {
    List<CastModel> castModels;

    public CastAdapter(List<CastModel> castModels) {
        this.castModels = castModels;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.castview, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        holder.name.setText(castModels.get(position).getName());
        if (castModels.get(position).getProfile_path().equalsIgnoreCase("") || TextUtils.isEmpty(castModels.get(position).getProfile_path())|| TextUtils.isEmpty(castModels.get(position).getProfile_path()))
        {
            Picasso.get().load(R.drawable.ts).into(holder.image);
        }
        else
        {
            Picasso.get().load(castModels.get(position).getProfile_path()).error(R.drawable.ts).into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return castModels.size();
    }

    public class Viewholder  extends RecyclerView.ViewHolder{
        CircleImageView image;
        TextView name;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.profile_image);
            name=itemView.findViewById(R.id.profile_name);
        }
    }
}
