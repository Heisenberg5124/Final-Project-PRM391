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

import org.w3c.dom.Text;

import java.util.List;

import fpt.life.finalproject.R;
import fpt.life.finalproject.dto.MatchedProfile;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
    private List<MatchedProfile> listChatted;
    private OnItemListener onItemListener;
    public ChatAdapter(List<MatchedProfile> listChatted, OnItemListener onItemListener) {
        this.listChatted = listChatted;
        this.onItemListener = onItemListener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View chatView = inflater.inflate(R.layout.recycler_view_item_showchated, parent, false);
        ViewHolder viewHolder = new ViewHolder(chatView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MatchedProfile matchedProfile = listChatted.get(position);
        ImageView avtChatted = holder.avtChatted;
        Picasso.get().load(matchedProfile.getPhotoImageUrl()).into(avtChatted);
        TextView name = holder.textViewNameChatted;
        name.setText(matchedProfile.getName());
        TextView lastMessage = holder.textViewLastMessage;
        lastMessage.setText(String.format("%s: %s • %s",matchedProfile.getSender(), matchedProfile.getLastMessage(), matchedProfile.getTimeLastMessage()));

    }

    @Override
    public int getItemCount() {
        return listChatted.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView avtChatted;
        public TextView textViewNameChatted;
        public TextView textViewLastMessage;
        public ViewHolder(View itemView) {
            super(itemView);
            avtChatted = itemView.findViewById(R.id.image_view_avt_chatted);
            textViewNameChatted =itemView.findViewById(R.id.text_view_name_chatted);
            textViewLastMessage = itemView.findViewById(R.id.text_view_lastmessage_chatted);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(listChatted.get(getAdapterPosition()).getUid());
        }
    }
    public interface OnItemListener{
        void onItemClick(String uid);
    }
}
