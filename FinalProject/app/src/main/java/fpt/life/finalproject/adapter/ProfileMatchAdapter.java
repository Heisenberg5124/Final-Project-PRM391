package fpt.life.finalproject.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import fpt.life.finalproject.R;
import fpt.life.finalproject.dto.MatchedProfile;

public class ProfileMatchAdapter extends RecyclerView.Adapter<ProfileMatchAdapter.ViewHolder>{
    private ProfileMatchAdapter.OnItemListener onItemListener;
    private List<MatchedProfile> listMatchedProfile;

    public ProfileMatchAdapter( List<MatchedProfile> listMatchedProfile, OnItemListener onItemListener) {
        this.onItemListener = onItemListener;
        this.listMatchedProfile = listMatchedProfile;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View matchedView = inflater.inflate(R.layout.recycler_view_item_show_profile_match, parent, false);
        ViewHolder viewHolder = new ViewHolder(matchedView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileMatchAdapter.ViewHolder holder, int position) {
        MatchedProfile matchedProfile = listMatchedProfile.get(position);
        ImageView avt_matched_view = holder.avt_matched;
        Picasso.get().load(matchedProfile.getPhotoImageUrl()).into(avt_matched_view);
        TextView text_view_name_matched_view = holder.text_view_name_matched;
        text_view_name_matched_view.setText(matchedProfile.getOtherUserName());
        ImageView isOnline = holder.imageViewIsOnline;
        String colorStatus = matchedProfile.getOnlineStatus() ? "#99ffbb" : "#cccccc";
        isOnline.setColorFilter(Color.parseColor(colorStatus));
    }
    public void filterProfileList(ArrayList<MatchedProfile> filteredList) {
        listMatchedProfile = filteredList;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return listMatchedProfile.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView avt_matched;
        public TextView text_view_name_matched;
        public ImageView imageViewIsOnline;
        public ViewHolder(View itemView) {
            super(itemView);
            avt_matched = itemView.findViewById(R.id.image_view_avt_match_profile);
            imageViewIsOnline = itemView.findViewById(R.id.isOnline_show_profile_match);
            text_view_name_matched =itemView.findViewById(R.id.text_view_name_profile_match);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(listMatchedProfile.get(getAdapterPosition()).getOtherUid());
        }
    }
    public interface OnItemListener{
        void onItemClick(String uid);
    }
}
