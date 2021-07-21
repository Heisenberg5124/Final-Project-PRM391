package fpt.life.finalproject.screen.matched;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import fpt.life.finalproject.R;
import fpt.life.finalproject.dto.MatchedProfile;

public class MatchedAdapter extends RecyclerView.Adapter<MatchedAdapter.ViewHolder> {
    private List<MatchedProfile> listMatched;
    private OnItemListener onItemListener;
    public MatchedAdapter(List<MatchedProfile> listMatched, OnItemListener onItemListener) {
        this.onItemListener = onItemListener;
        this.listMatched = listMatched;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View matchedView = inflater.inflate(R.layout.recycler_view_item_showmatched, parent, false);
        ViewHolder viewHolder = new ViewHolder(matchedView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MatchedProfile matchedProfile = listMatched.get(position);
        ImageView avt_matched_view = holder.avt_matched;
        Picasso.get().load(matchedProfile.getPhotoImageUrl()).into(avt_matched_view);
        TextView text_view_name_matched_view = holder.text_view_name_matched;
        text_view_name_matched_view.setText(matchedProfile.getName());
    }

    @Override
    public int getItemCount() {
        return listMatched.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView avt_matched;
        public TextView text_view_name_matched;
        public ViewHolder(View itemView) {
            super(itemView);
            avt_matched = itemView.findViewById(R.id.image_view_avt_chatted);
            text_view_name_matched =itemView.findViewById(R.id.text_view_name_matched);
            itemView.setOnClickListener(this);
        }
        public String a(){
            return avt_matched.toString() + " " + text_view_name_matched.toString() + " "+ this.toString();
        }
        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(listMatched.get(getAdapterPosition()).getOtherUid());
        }
    }
    public interface OnItemListener{
        void onItemClick(String uid);
    }
}
