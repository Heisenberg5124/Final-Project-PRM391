package fpt.life.finalproject.dto;

import com.google.firebase.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

public class MatchedProfile{
    private String otherUid;
    private String otherUserName;
    private String photoImageUrl;
    private String lastMessage;
    private String lastMessageID;
    private Timestamp timeLastMessage;
    private Boolean onlineStatus;
    private Boolean isSeen;
    private String sender;

}
