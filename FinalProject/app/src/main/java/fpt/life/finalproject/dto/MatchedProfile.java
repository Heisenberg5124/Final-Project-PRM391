package fpt.life.finalproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

public class MatchedProfile {
    private String uid;
    private String name;
    private String photoImageUrl;
    private String lastMessage;
    private String timeLastMessage;
    private String onlineStatus;
    private String sender;
}
