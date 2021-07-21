package fpt.life.finalproject.screen.matched;

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

import java.util.List;

import fpt.life.finalproject.R;
import fpt.life.finalproject.dto.MatchedProfile;
import fpt.life.finalproject.service.MatchedService;

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
        name.setText(matchedProfile.getOtherUserName());
        TextView lastMessage = holder.textViewLastMessage;
        lastMessage.setText(String.format("%s: %s • %s",matchedProfile.getSender(), checkLengthMessage(matchedProfile.getSender(),matchedProfile.getLastMessage()) , matchedProfile.getTimeLastMessage()));
        ImageView isOnline = holder.imageViewIsOnline;
        String colorStatus = matchedProfile.getOnlineStatus() ? "#99ffbb" : "#cccccc";
        isOnline.setColorFilter(Color.parseColor(colorStatus));
    }

    @Override
    public int getItemCount() {
        return listChatted.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView avtChatted;
        public TextView textViewNameChatted;
        public TextView textViewLastMessage;
        public ImageView imageViewIsOnline;
        public ViewHolder(View itemView) {
            super(itemView);
            avtChatted = itemView.findViewById(R.id.image_view_avt_chatted);
            imageViewIsOnline = itemView.findViewById(R.id.isOnline_show_chat);
            textViewNameChatted =itemView.findViewById(R.id.text_view_name_chatted);
            textViewLastMessage = itemView.findViewById(R.id.text_view_lastmessage_chatted);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(listChatted.get(getAdapterPosition()).getOtherUid());
        }
    }
    public interface OnItemListener{
        void onItemClick(String uid);
    }
    private String checkLengthMessage(String senderName, String message){
        if (message == null) return null;
        if ((senderName.length() + message.length())> 32) return message.substring(0,28-senderName.length())+"...";
        else return message;
    }
}
