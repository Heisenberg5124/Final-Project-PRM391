package fpt.life.finalproject.screen.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aminography.choosephotohelper.ChoosePhotoHelper;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;

import at.markushi.ui.CircleButton;
import fpt.life.finalproject.MainActivity;
import fpt.life.finalproject.R;
import fpt.life.finalproject.adapter.MessageAdapter;
import fpt.life.finalproject.dto.chat.ChatRoom;
import fpt.life.finalproject.screen.viewOtherProfile.ViewOtherProfile_Activity;
import fpt.life.finalproject.service.chat.ChatService;
import fpt.life.finalproject.service.chat.OnFirebaseListener;

public class ChatActivity extends AppCompatActivity implements OnFirebaseListener {

    private ImageView imageViewChatBack, imageViewChatInfo, imageViewChatUnmatched,
            imageViewSendMessage, imageViewChatRoomStatus;
    private ShapeableImageView imageViewChatAvatar;
    private CircleButton buttonChatSendPhoto;
    private EditText editTextChatText;
    private TextView textViewChatName, textViewChatStatus;
    private RecyclerView recyclerViewChatMessages;
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String string = s.toString();
            if (! (string == null || string.trim().isEmpty())) {
                imageViewSendMessage.setClickable(true);
                imageViewSendMessage.setColorFilter(Color.rgb(253, 76, 103));
            } else {
                imageViewSendMessage.setClickable(false);
                imageViewSendMessage.setColorFilter(Color.rgb(243, 138, 138));
            }
        }
    };

    private ChoosePhotoHelper choosePhotoHelper;

    private ChatService chatService;

    private MessageAdapter messageAdapter;

    private String currentUid, otherUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getUid();

        chatService = new ChatService(this, currentUid, otherUid);
        initComponents();
        initRecyclerView();
//        chatService.getChatRoomInfo();
        chatService.onChangeChatRoomInfo();
        chatService.getAllMessages();
        chatService.seenAllMessages();
    }

    private void initComponents() {
        imageViewChatAvatar = findViewById(R.id.image_view_chat_avatar);
        imageViewChatBack = findViewById(R.id.image_view_chat_back);
        imageViewChatInfo = findViewById(R.id.image_view_chat_info);
        imageViewChatUnmatched = findViewById(R.id.image_view_chat_unmatched);
        imageViewSendMessage = findViewById(R.id.image_view_send_message);
        buttonChatSendPhoto = findViewById(R.id.button_chat_send_photo);
        editTextChatText = findViewById(R.id.edit_text_chat_text);
        textViewChatName = findViewById(R.id.text_view_chat_name);
        textViewChatStatus = findViewById(R.id.text_view_chat_status);
        recyclerViewChatMessages = findViewById(R.id.recycler_view_chat_messages);
        imageViewChatRoomStatus = findViewById(R.id.image_view_chat_room_status);

        editTextChatText.addTextChangedListener(textWatcher);

        imageViewSendMessage.setOnClickListener(view -> onButtonSendTextClick());
        buttonChatSendPhoto.setOnClickListener(view -> onButtonSendImageClick());
        imageViewChatBack.setOnClickListener(view -> onBack());
        imageViewChatInfo.setOnClickListener(view -> navigateOtherProfile());
        imageViewChatUnmatched.setOnClickListener(view -> onUnmatchedClick(chatService.getChatRoom()));
    }

    private void initRecyclerView() {
        messageAdapter = new MessageAdapter(chatService.getMessages(), this, chatService.getMyUid());

        recyclerViewChatMessages.setAdapter(messageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewChatMessages.setLayoutManager(linearLayoutManager);
    }

    private void onButtonSendImageClick() {
        choosePhotoHelper = ChoosePhotoHelper.with(this)
                .asUri()
                .build(uri -> {
                    chatService.uploadMessageImage(uri);
                });
        choosePhotoHelper.showChooser();
    }

    private void onButtonSendTextClick() {
        String content = editTextChatText.getText().toString();
        editTextChatText.getText().clear();
        chatService.sendMessage(content, "");
    }

    private void setUIChatRoomInfo(ChatRoom chatRoomInfo) {
        setImage(chatRoomInfo.getOtherAvatarUrl(), imageViewChatAvatar);
        textViewChatName.setText(chatRoomInfo.getOtherName());
        textViewChatStatus.setText(chatRoomInfo.getLastTimeOnline());
        imageViewChatRoomStatus.setColorFilter(chatRoomInfo.isOnline()
                ? Color.parseColor("#00FF66")
                : Color.parseColor("#888888"));
    }

    private void setImage(String url, ImageView imageView) {
        Glide.with(this)
                .load(url)
                .centerCrop()
                .into(imageView);
    }

    private void navigateOtherProfile() {
        Intent i = new Intent(this, ViewOtherProfile_Activity.class);
        i.putExtra("otherUid", chatService.getOtherUid());
        startActivity(i);
    }

    private void onBack() {
        finish();
    }

    private void onUnmatchedClick(ChatRoom chatRoom) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Unmatched")
                .setMessage("Do you want unmatched " + chatRoom.getOtherName() + "?")
                .setNegativeButton(getString(R.string.decline), (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton(getString(R.string.accept), ((dialog, which) -> {
                    chatService.unmatched();
                }))
                .show();
    }

    private void getUid() {
        currentUid = this.getIntent().getStringExtra("currentUid");
        otherUid = this.getIntent().getStringExtra("otherUid");
        /*currentUid = "EqVdSFIZhmbfDdTVJSbXb1hB78l1";
        otherUid = "SQYPZpR4mFOhTe0qdeF2lCHXCk83";*/
    }

    @Override
    public void onCompleteLoadChatRoomInfo(ChatRoom chatRoom) {
        setUIChatRoomInfo(chatRoom);
    }

    @Override
    public void onChangeChatRoomInfo(ChatRoom chatRoom) {
        setUIChatRoomInfo(chatRoom);
    }

    @Override
    public void onCompleteLoadMessages(ChatRoom chatRoom) {
        Log.d("Message", "seenAllMessagesUI: ");
        messageAdapter.notifyDataSetChanged();
        recyclerViewChatMessages.scrollToPosition(chatService.getMessages().size() - 1);
    }

    @Override
    public void onCompleteUnmatched(ChatRoom chatRoom) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("unmatchedUid", chatRoom.getOtherUid());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onOtherUnmatched(ChatRoom chatRoom) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Unmatched")
                .setMessage(chatRoom.getOtherName() + " was unmatched you.\n" +
                        "Do you want to come to other matched?")
                .setNegativeButton(getString(R.string.decline), (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton(getString(R.string.accept), ((dialog, which) -> {
                    finish();
                }))
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        choosePhotoHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        choosePhotoHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}