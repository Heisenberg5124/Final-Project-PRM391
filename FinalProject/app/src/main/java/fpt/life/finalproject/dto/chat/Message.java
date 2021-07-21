package fpt.life.finalproject.dto.chat;

import com.google.firebase.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Message {

    private String messageUid;
    private String senderUid;
    private String avatarUrl;

    private String content;
    private String imageUrl;

    private String sendTimeShow;
    private Timestamp sendTime;
    private boolean isSeen;
    private boolean isShowSendTime;
}