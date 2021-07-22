package fpt.life.finalproject.screen.viewOtherProfile;


import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.graphics.Point;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import fpt.life.finalproject.R;
import fpt.life.finalproject.dto.OtherUser;
import fpt.life.finalproject.model.User;

public class ViewOtherProfile_Activity extends AppCompatActivity{

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView nameAndAge, location, bio;
    private ImageView imageAva, imageBack;
    private ChipGroup chipGroup;
    private GridLayout gridLayout;
    private OtherUser otherUser;
    private ArrayList<String> images;
    private ArrayList<String> hobbies;
    private String color;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_other_profile);
        color = "#FD4C67";
        nameAndAge = (TextView) findViewById(R.id.txt_name);
        location = (TextView) findViewById(R.id.txt_Location);
        bio = (TextView) findViewById(R.id.txt_introduce);
        imageAva = (ImageView) findViewById(R.id.img_avata);
        imageBack = (ImageView) findViewById(R.id.image_view_back) ;
        Intent i = getIntent();
        getOtherUser(i.getStringExtra("otherUid"));
//        getOtherUser("SQYPZpR4mFOhTe0qdeF2lCHXCk83");

    }

    private void addGroupHobby(ArrayList<String> hobbies){
        chipGroup = (ChipGroup) findViewById(R.id.chip_group1);
        for (int i = 0; i < hobbies.size(); i++) {
            Chip chip = new Chip(this);
            chip.setTextSize(14);
            chip.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor(color)));
            chip.setChipStrokeWidth(1.5f);
            chip.setText(hobbies.get(i));
            chipGroup.addView(chip);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void addGroupImage(ArrayList<String> images)  {
        gridLayout = findViewById(R.id.grid_layout);
        gridLayout.setColumnCount(2);
        gridLayout.setRowCount((int) Math.ceil((double) images.size() / 2));

        for (int i = 1, c = 0, r = 0; i < images.size(); i++, c++) {
            if (c == 2) {
                c = 0;
                r++;
            }
            ShapeableImageView oImageView = new ShapeableImageView(this);
            oImageView.setImageResource(R.drawable.logo);
            String url = images.get(i);
            Picasso.get().load(url).fit().into(oImageView);
            oImageView.setShapeAppearanceModel(oImageView.getShapeAppearanceModel().toBuilder().setAllCornerSizes(15).build());
            oImageView.setStrokeColor(ColorStateList.valueOf(Color.parseColor(color)));
            oImageView.setStrokeWidth(1.5f);

            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            Point size = new Point();
            getWindowManager().getDefaultDisplay().getSize(size);
            int screenWidth = size.x;
            int halfScreenWidth = (int)(screenWidth *0.5);
            param.width = halfScreenWidth-50;
            param.height = (int) (param.width *1.3f);
            param.leftMargin = 20;
            param.rightMargin = 20;
            param.bottomMargin = 20;
            param.topMargin = 20;
            param.setGravity(Gravity.CENTER);
            param.columnSpec = GridLayout.spec(c);
            param.rowSpec = GridLayout.spec(r);
            oImageView.setLayoutParams(param);
            gridLayout.addView(oImageView);

            int pos = i;
            oImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i  = new Intent(ViewOtherProfile_Activity.this, FullScreen.class);
                    i.putExtra("images", images);
                    i.putExtra("position", pos-1);
                    startActivity(i);
                }
            });
        }
    }

    private void getOtherUser(String userId){
        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        User user = task.getResult().toObject(User.class);
                        otherUser = OtherUser.builder().userID(user.getUid())
                                .age(calculateAge(user.getBirthday()))
                                .bio(user.getBio())
                                .hobbies(user.getHobbies())
                                .listImage(user.getPhotoUrls())
                                .name(user.getName())
                                .city(user.getCity())
                                .build();
//                        progressDialog.dismiss();
                        setOtherUserData();

                    }
                });
    }

    private int calculateAge(Date anotherUserBirthday){
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Calendar anotherUserBirthdayCalendar = Calendar.getInstance();
        anotherUserBirthdayCalendar.setTime(anotherUserBirthday);
        int anotherUserYear = anotherUserBirthdayCalendar.get(Calendar.YEAR);
        return currentYear - anotherUserYear;
    }

    private void setAvaImage(ImageView image){
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int screenWidth = size.x;
        image.getLayoutParams().height = (int) (screenWidth*1.2f);
        image.requestLayout();
        Picasso.get().load(images.get(0))
                .fit().centerCrop()
                .into(image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i  = new Intent(ViewOtherProfile_Activity.this, FullAva.class);
                i.putExtra("avaUrl", images.get(0));
                startActivity(i);
            }
        });
    }

    private void clickBack(ImageView imageView){
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setOtherUserData(){
        nameAndAge.setText(otherUser.getName()+", "+otherUser.getAge());
        location.setText(otherUser.getCity());
        bio.setText(otherUser.getBio());
        hobbies = otherUser.getHobbies();
        images = otherUser.getListImage();
        setAvaImage(imageAva);
        addGroupHobby(hobbies);
        addGroupImage(images);
        clickBack(imageBack);
    }

}
