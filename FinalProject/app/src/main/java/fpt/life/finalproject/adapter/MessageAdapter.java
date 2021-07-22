package fpt.life.finalproject.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.util.ArrayList;

import fpt.life.finalproject.R;
import fpt.life.finalproject.dto.chat.Message;
import fpt.life.finalproject.screen.viewOtherProfile.FullAva;
import fpt.life.finalproject.screen.viewOtherProfile.ViewOtherProfile_Activity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MessageAdapter extends RecyclerView.Adapter {

    private ArrayList<Message> messages;
    private Activity activity;

    private String currentUserUid;
    private int width;

    private static int positionShowSendTime = -1;

    public MessageAdapter(ArrayList<Message> messages, Activity activity, String currentUserUid) {
        this.messages = messages;
        this.activity = activity;
        this.currentUserUid = currentUserUid;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View messageView;
        width = (int) (parent.getMeasuredWidth() * 0.6);

        if (viewType == 0) {
            messageView = inflater.inflate(R.layout.item_recycler_view_chat_my_text, parent, false);
            return new ViewHolderMyMessage(messageView);
        }

        messageView = inflater.inflate(R.layout.item_recycler_view_chat_other_text, parent, false);
        return new ViewHolderOtherMessage(messageView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        Log.d("Adapter", "CheckAdapter: " + message.getMessageUid() + " _ " + position);
        if (message.getSenderUid().equals(currentUserUid)) {
            ViewHolderMyMessage viewHolderMyMessage = (ViewHolderMyMessage) holder;
            LinearLayout layout = viewHolderMyMessage.itemTextChat;
            ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
            layoutParams.width = width;
            layout.setLayoutParams(layoutParams);

            viewHolderMyMessage.imageViewMyMessage.setMaxWidth(width);
            setMessageBubble(message, viewHolderMyMessage.textViewMyMessage,
                    viewHolderMyMessage.imageViewMyMessage);
            setMessageShape(position, viewHolderMyMessage.textViewMyMessage,
                    viewHolderMyMessage.imageViewMyMessage, viewHolderMyMessage.item);
//            viewHolderMyMessage.imageViewSeenStatus.setVisibility(View.VISIBLE);
            setSeenStatus(position, viewHolderMyMessage.imageViewSeenStatus);
            setShowTime(position, viewHolderMyMessage.textViewMyTimeSend);

            viewHolderMyMessage.textViewMyMessage.setOnClickListener(view -> onClickTextMessage(position));
            viewHolderMyMessage.imageViewMyMessage.setOnClickListener(view -> onClickImageMessage(message.getImageUrl()));
        } else {
            ViewHolderOtherMessage viewHolderOtherMessage = (ViewHolderOtherMessage) holder;
            LinearLayout layout = viewHolderOtherMessage.itemTextChat;
            ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
            layoutParams.width = width;
            layout.setLayoutParams(layoutParams);

            viewHolderOtherMessage.imageViewOtherMessage.setMaxWidth(width);
            setMessageBubble(message, viewHolderOtherMessage.textViewOtherMessage,
                    viewHolderOtherMessage.imageViewOtherMessage);
            setMessageShape(position, viewHolderOtherMessage.textViewOtherMessage,
                    viewHolderOtherMessage.imageViewOtherMessage, viewHolderOtherMessage.item);
            setAvatar(position, viewHolderOtherMessage.imageViewSenderAvatar);
            setShowTime(position, viewHolderOtherMessage.textViewOtherTimeSend);

            viewHolderOtherMessage.textViewOtherMessage.setOnClickListener(view -> onClickTextMessage(position));

            viewHolderOtherMessage.imageViewOtherMessage.setOnClickListener(view -> onClickImageMessage(message.getImageUrl()));
        }
    }

    private void onClickImageMessage(String imageUrl) {
        Intent i  = new Intent(activity, FullAva.class);
        i.putExtra("avaUrl", imageUrl);
        activity.startActivity(i);
    }

    private void onClickTextMessage(int position) {
        if (positionShowSendTime > -1) {
            messages.get(positionShowSendTime).setShowSendTime(false);
            notifyItemChanged(positionShowSendTime);
        }

        if (positionShowSendTime != position) {
            Message message = messages.get(position);
            message.setShowSendTime(!message.isShowSendTime());

            positionShowSendTime = message.isShowSendTime() ? position : -1;

            notifyItemChanged(position);
        } else positionShowSendTime = -1;
    }

    private void setShowTime(int position, TextView textView) {
        if (messages.get(position).isShowSendTime()) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(messages.get(position).getSendTimeShow());
        } else
            textView.setVisibility(View.GONE);
    }

    /*
        Text: true
        Image: false
     */
    private boolean isImageOrTextMessage(Message message) {
        return message.getContent().length() > 0;
    }

    private void setSeenStatus(int position, ShapeableImageView imageViewSeenStatus) {
        Message message = messages.get(position);
        boolean isCurrentUser = message.getSenderUid().equals(currentUserUid);
        boolean isLastMessage = position == messages.size() - 1;

        if (isCurrentUser) {
            if (!isLastSender(position))
                imageViewSeenStatus.setVisibility(View.GONE);
            else if (message.isSeen()) {
                if (isLastMessage) {
                    setImage(message.getAvatarUrl(), imageViewSeenStatus);
                    imageViewSeenStatus.setVisibility(View.VISIBLE);
                } else {
                    if (messages.get(position + 1).isSeen())
                        imageViewSeenStatus.setVisibility(View.GONE);
                    else {
                        setImage(message.getAvatarUrl(), imageViewSeenStatus);
                        imageViewSeenStatus.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                imageViewSeenStatus.setBackgroundResource(R.drawable.ic_check_circle);
                imageViewSeenStatus.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean isLastSender(int position) {
        for (int i = position; i < messages.size() - 1; i++) {
            if (!messages.get(i + 1).getSenderUid().equals(currentUserUid))
                return false;
        }
        return true;
    }

    private void setMessageBubble(Message message, TextView textView, ShapeableImageView imageView) {
        boolean isImageOrTextMessage = isImageOrTextMessage(message);
        if (isImageOrTextMessage) {
            textView.setText(message.getContent());
            textView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        } else {
            setImageMessageBubble(message.getImageUrl(), imageView);
            textView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    private void setAvatar(int position, ShapeableImageView shapeableImageView) {
        String type = getTypeMessageBubble(position);
        Message message = messages.get(position);
        boolean isCurrentUser = message.getSenderUid().equals(currentUserUid);
        if (!isCurrentUser)
            if (type.equalsIgnoreCase("single") || type.equalsIgnoreCase("last")) {
                shapeableImageView.setVisibility(View.VISIBLE);
                setImage(message.getAvatarUrl(), shapeableImageView);
            } else
                shapeableImageView.setVisibility(View.GONE);
    }

    private void setMessageShape(int position, TextView textView,
                                 ShapeableImageView shapeableImageViewMessage,
                                 ConstraintLayout item) {
        int backgroundResource = -1;
        String type = getTypeMessageBubble(position);
        boolean isCurrentUser = messages.get(position).getSenderUid().equals(currentUserUid);
        /*ShapeAppearanceModel shapeAppearanceModel = shapeableImageViewMessage.getShapeAppearanceModel();
        float smallCornerRadius = 5, largeCornerRadius = 20;*/

        switch (type) {
            case "first":
                backgroundResource = isCurrentUser
                        ? R.drawable.background_chat_my_message_first
                        : R.drawable.background_chat_other_message_first;
                item.setPadding(0, 20, 0, 0);
                break;
            case "middle":
                backgroundResource = isCurrentUser
                        ? R.drawable.background_chat_my_message_middle
                        : R.drawable.background_chat_other_message_middle;
                break;
            case "last":
                backgroundResource = isCurrentUser
                        ? R.drawable.background_chat_my_message_last
                        : R.drawable.background_chat_other_message_last;
                item.setPadding(0, 0, 0, 20);
                break;
            case "single":
                backgroundResource = isCurrentUser
                        ? R.drawable.background_chat_my_message_single
                        : R.drawable.background_chat_other_message_single;
                item.setPadding(0, 20, 0, 20);
                break;
        }

        textView.setBackgroundResource(backgroundResource);
    }

    private String getTypeMessageBubble(int position) {
        int size = messages.size();

        String senderUid = messages.get(position).getSenderUid();
        String senderUidBefore = position == 0 ? "" : messages.get(position - 1).getSenderUid();
        String senderUidAfter = position == size - 1 ? "" : messages.get(position + 1).getSenderUid();

        if (senderUidBefore.equals("")) {
            if (senderUidAfter.equals(senderUid)) return "first";
            return "single";
        }

        if (!senderUidBefore.equals(senderUid)) {
            if (senderUidAfter.equals(senderUid)) return "first";
            return "single";
        }

        if (senderUidAfter.equals(senderUid)) return "middle";
        return "last";
    }

    private void setImageMessageBubble(String url, ShapeableImageView imageView) {

        Glide.with(activity)
                .load(url)
//                .fitCenter()
                .override(width, Target.SIZE_ORIGINAL)
                .into(imageView);
    }

    private void setImage(String url, ShapeableImageView imageView) {
        Glide.with(activity)
                .load(url)
                .centerCrop()
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        /*
            My Message: 0
            Other Message : 1
         */
        return message.getSenderUid().equals(currentUserUid) ? 0 : 1;
    }

    public class ViewHolderOtherMessage extends RecyclerView.ViewHolder {

        public ConstraintLayout item;
        public LinearLayout itemTextChat;
        public ShapeableImageView imageViewSenderAvatar, imageViewOtherMessage;
        public TextView textViewOtherMessage, textViewOtherTimeSend;

        public ViewHolderOtherMessage(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.layout_other_chat);

            itemTextChat = itemView.findViewById(R.id.layout_other_text_chat);

            imageViewSenderAvatar = itemView.findViewById(R.id.image_view_sender_avatar);
            imageViewOtherMessage = itemView.findViewById(R.id.image_view_other_message);

            textViewOtherMessage = itemView.findViewById(R.id.text_view_other_message);
            textViewOtherTimeSend = itemView.findViewById(R.id.text_view_other_time_send);
        }
    }

    public class ViewHolderMyMessage extends RecyclerView.ViewHolder {

        public ConstraintLayout item;
        public LinearLayout itemTextChat;
        public ShapeableImageView imageViewSeenStatus, imageViewMyMessage;
        public TextView textViewMyMessage, textViewMyTimeSend;

        public ViewHolderMyMessage(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.layout_my_chat);

            itemTextChat = itemView.findViewById(R.id.layout_my_text_chat);

            imageViewSeenStatus = itemView.findViewById(R.id.image_view_seen_status);
            imageViewMyMessage = itemView.findViewById(R.id.image_view_my_message);

            textViewMyMessage = itemView.findViewById(R.id.text_view_my_message);
            textViewMyTimeSend = itemView.findViewById(R.id.text_view_my_time_send);
        }
    }
}
