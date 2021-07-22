package fpt.life.finalproject.service.chat;

import fpt.life.finalproject.dto.chat.ChatRoom;

public interface OnFirebaseListener {
    void onCompleteLoadChatRoomInfo(ChatRoom chatRoom);
    void onChangeChatRoomInfo(ChatRoom chatRoom);
    void onCompleteLoadMessages(ChatRoom chatRoom);
    void onCompleteUnmatched(ChatRoom chatRoom);
}
