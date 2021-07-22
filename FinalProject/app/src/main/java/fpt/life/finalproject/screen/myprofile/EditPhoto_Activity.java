package fpt.life.finalproject.screen.myprofile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.aminography.choosephotohelper.ChoosePhotoHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import fpt.life.finalproject.MainActivity;
import fpt.life.finalproject.R;
import fpt.life.finalproject.adapter.PhotoAdapter;
import fpt.life.finalproject.adapter.RecyclerItemSelectedListener;
import fpt.life.finalproject.adapter.drapdrop.ItemTouchHelperCallBack;
import fpt.life.finalproject.dto.MyProfile;
import fpt.life.finalproject.model.Photo;
import fpt.life.finalproject.model.User;
import fpt.life.finalproject.service.EditPhotoService;
import fpt.life.finalproject.util.ButtonUtil;

public class EditPhoto_Activity extends AppCompatActivity implements RecyclerItemSelectedListener {

    private static final int MAX_NUM_OF_PHOTOS = 9;
    private static final int NUM_OF_COLUMNS = 3;


    private RecyclerView recyclerViewEditPhotos;
    private Button buttonDone;
    private PhotoAdapter photoAdapter;
    private ItemTouchHelper itemTouchHelper;
    private ProgressDialog progressDialog;

    private ChoosePhotoHelper choosePhotoHelper;

    private ArrayList<Photo> dbPhotoList = new ArrayList<>();
    private ArrayList<Photo> photos = new ArrayList<>();
    private EditPhotoService editPhotoService;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MyProfile myProfile;

    public EditPhoto_Activity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_photo);


//        getImageList("SQYPZpR4mFOhTe0qdeF2lCHXCk83");
////        getImageList(FirebaseAuth.getInstance().getUid());

        convertToPhotoList((ArrayList<String>) this.getIntent().getSerializableExtra("imagesListDB"));
        initComponents();
        initRecyclerView();
        initFireBase();
        onClickButtonDone();

    }


    private void initFireBase() {
//        editPhotoService = new EditPhotoService(FirebaseAuth.getInstance().getUid());
        editPhotoService = new EditPhotoService("1YyOVbEZ9nbclrT9iX5GIRTCboA3");
    }

    private void onClickButtonDone() {  //TODO: list hiển thị khi drag drop khác với list thật trong db
        buttonDone.setOnClickListener(v -> {
            ArrayList<Photo> updateList = new ArrayList<>();
            String url;
            updateList = updatePhotos();
            Log.d("check", updateList.toString());
            loadProgressDialog();
            editPhotoService.uploadEditedImages(updateList, progressDialog);
            backToMyProfile(updateList);
        });
    }

    private void backToMyProfile(ArrayList<Photo> imagePhoto){
        Intent intent = new Intent(EditPhoto_Activity.this, MainActivity.class);
        intent.putExtra("avatarUrl", convertToStringList(imagePhoto));
//        Log.d("rrrrr", avaUrl);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void loadProgressDialog() {
        progressDialog = new ProgressDialog(EditPhoto_Activity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private ArrayList<Photo> updatePhotos() {
        ArrayList<Photo> updatePhotos = new ArrayList<>();
        for (Photo photo : photos)
            if (!photo.isEmpty())
                updatePhotos.add(photo);
        return updatePhotos;
    }

    private void initRecyclerView() {
        photoAdapter = new PhotoAdapter(photos, EditPhoto_Activity.this, viewHolder -> {
            itemTouchHelper.startDrag(viewHolder);
        }, this, buttonDone);
        recyclerViewEditPhotos.setAdapter(photoAdapter);
        recyclerViewEditPhotos.setLayoutManager(new GridLayoutManager(EditPhoto_Activity.this, NUM_OF_COLUMNS));
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallBack(photoAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerViewEditPhotos);
    }

    private void initComponents() {
        createViewPhoto();
        recyclerViewEditPhotos = findViewById(R.id.recycler_view_edit_photos);
        buttonDone = findViewById(R.id.button_edit_done);
    }

    private void createViewPhoto(){
        Log.d("ppppp", dbPhotoList.size()+"");
        photos.clear();
        for (int i = 0; i < MAX_NUM_OF_PHOTOS; i++) {
            if (i<dbPhotoList.size()){
                Photo photo = Photo.builder()
                        .photoUri(dbPhotoList.get(i).getPhotoUri())
                        .isEmpty(false)
                        .isExistFireBase(true)
                        .build();
                photos.add(photo);
            } else {
                Photo photo = Photo.builder()
                        .isEmpty(true)
                        .isExistFireBase(false)
                        .build();
                photos.add(photo);
            }
        }
        Log.d("check", dbPhotoList.toString());

    }

    @Override
    public void onItemClick(Object object) {
        onPlusClick(getFirstIndexOfNull());
    }

    private void onPlusClick(int position) {
        Photo photo = photos.get(position);
        ButtonUtil buttonUtil = ButtonUtil.builder().button(buttonDone).build();
        choosePhotoHelper = ChoosePhotoHelper.with(EditPhoto_Activity.this)
                .asUri()
                .build(uri -> {
                    photo.setPhotoUri(uri.toString());
                    photo.setEmpty(false);
                    photoAdapter.notifyItemChanged(position);
                    Log.d("CheckPhotoPlus", photos.toString() + " " + position);
                    setButtonDone(buttonUtil);
                });
        choosePhotoHelper.showChooser();
        setButtonDone(buttonUtil);
    }

    private void setButtonDone(ButtonUtil buttonUtil) {
        buttonUtil.setFilled(!photos.get(0).isEmpty());
        buttonUtil.setButtonWhenFilled();
    }

    private int getFirstIndexOfNull() {
        for (int i = 0; i < photos.size(); i++) {
            Photo photo = photos.get(i);
            if (photo.isEmpty())
                return i;
        }
        return -1;
    }

    /*private void getImageList(String userId){
        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        User user = task.getResult().toObject(User.class);
                        myProfile = MyProfile.builder().uid(user.getUid())
                                .listImage(user.getPhotoUrls())
                                .build();
//                        progressDialog.dismiss();
                        convertToPhotoList(myProfile.getListImage());
                        Log.d("ppp", dbPhotoList.size()+"");
                        initComponents();
                        initRecyclerView();
                        initFireBase();
                        onClickButtonDone();
                    }
                });
    }*/
    private void convertToPhotoList(ArrayList<String> imageList){
        for (int i = 0; i < imageList.size(); i++) {
            Photo photo = new Photo();
            photo.setPhotoUri(imageList.get(i));
            photo.setEmpty(false);
            dbPhotoList.add(photo);
        }
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
    private ArrayList<String> convertToStringList(ArrayList<Photo> imageList){
        ArrayList<String> imageListToSendBack = new ArrayList<>();
        for ( Photo photo : imageList){
            imageListToSendBack.add(photo.getPhotoUri());
        }
        return imageListToSendBack;
    }
}