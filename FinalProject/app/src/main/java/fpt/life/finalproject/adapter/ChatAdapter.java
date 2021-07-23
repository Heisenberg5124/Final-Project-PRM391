package fpt.life.finalproject.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import fpt.life.finalproject.R;
import fpt.life.finalproject.dto.MatchedProfile;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<MatchedProfile> listChatted;
    private OnItemListener onItemListener;
    private TextView name;
    private TextView lastMessage;
    private String currentUserName;

    public ChatAdapter(List<MatchedProfile> listChatted, OnItemListener onItemListener, String currentUserName) {
        this.listChatted = listChatted;
        this.onItemListener = onItemListener;
        this.currentUserName = currentUserName;
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
        Picasso.get().load(matchedProfile.getPhotoImageUrl()).fit().into(avtChatted);
        name = holder.textViewNameChatted;
        name.setText(matchedProfile.getOtherUserName());
        lastMessage = holder.textViewLastMessage;
        lastMessage.setText(String.format("%s: %s • %s", setSenderName(matchedProfile.getSender(),matchedProfile),
                checkLengthMessage(setSenderName(matchedProfile.getSender(), matchedProfile),
                        matchedProfile.getLastMessage()), timestampToString(matchedProfile.getTimeLastMessage())));
        ImageView isOnline = holder.imageViewIsOnline;
        String colorStatus = matchedProfile.getOnlineStatus() ? "#00FF66" : "#888888";
        isOnline.setColorFilter(Color.parseColor(colorStatus));
        setColorTextView(matchedProfile);
    }

    @Override
    public int getItemCount() {
        return listChatted.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView avtChatted;
        public TextView textViewNameChatted;
        public TextView textViewLastMessage;
        public ImageView imageViewIsOnline;

        public ViewHolder(View itemView) {
            super(itemView);
            avtChatted = itemView.findViewById(R.id.image_view_avt_chatted);
            imageViewIsOnline = itemView.findViewById(R.id.isOnline_show_chat);
            textViewNameChatted = itemView.findViewById(R.id.text_view_name_chatted);
            textViewLastMessage = itemView.findViewById(R.id.text_view_lastmessage_chatted);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(listChatted.get(getAdapterPosition()).getOtherUid());
        }
    }

    public interface OnItemListener {
        void onItemClick(String uid);
    }

    private String checkLengthMessage(String senderName, String message) {
        if (message == null) return null;
        if ((senderName.length() + message.length()) > 32)
            return message.substring(0, 28 - senderName.length()) + "...";
        else return message;
    }

    private void setColorTextView(MatchedProfile matchedProfile){
        if (!matchedProfile.getIsSeen() && matchedProfile.getOtherUid().equals(matchedProfile.getSender())) {
            name.setTextColor(Color.parseColor("#000000"));
            lastMessage.setTextColor(Color.parseColor("#000000"));
        } else {
            name.setTextColor(Color.parseColor("#808080"));
            lastMessage.setTextColor(Color.parseColor("#808080"));
        }
    }

    private String setSenderName(String sender, MatchedProfile matchedProfile){
        if (!sender.equals(matchedProfile.getOtherUid())) {
            Log.d("CheckName",matchedProfile.getIsSeen()+ " ");
            return currentUserName;
        }
        return matchedProfile.getOtherUserName();
    }

    public String timestampToString(Timestamp timestamp) {
        Long distanceTime = System.currentTimeMillis() / 1000 - timestamp.getSeconds();
        SimpleDateFormat formatter;
        if (distanceTime < 86400) {
            formatter = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        } else {
            formatter = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
        }
        return formatter.format(timestamp.toDate());
    }

}
