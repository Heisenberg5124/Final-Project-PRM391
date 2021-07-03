package fpt.life.finalproject.screen.viewOtherProfile;


import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aminography.choosephotohelper.ChoosePhotoHelper;
import com.aminography.choosephotohelper.callback.ChoosePhotoCallback;
import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.squareup.picasso.Picasso;


import java.util.Random;

import fpt.life.finalproject.R;
import fpt.life.finalproject.editPhoto.EditPhoto_Activity;

public class ViewOtherProfile_Activity extends AppCompatActivity{

    TextView nameAndAge, location, bio;
    ImageView imageView;
    ChipGroup chipGroup;
    GridLayout gridLayout;
    String[] images = {"https://i.pinimg.com/564x/3c/dd/56/3cdd56556aaf558988e225c312d34e97.jpg",
                        "https://ruthamcauquan2.info/wp-content/uploads/2020/07/anh-gai-xinh-hap-dan-nhieu-nam-gioi-16.jpg",
                        "https://ruthamcauquan2.info/wp-content/uploads/2020/07/anh-gai-xinh-hap-dan-nhieu-nam-gioi-16.jpg",
                        "https://ruthamcauquan2.info/wp-content/uploads/2020/07/anh-gai-xinh-hap-dan-nhieu-nam-gioi-16.jpg",
                        "https://ruthamcauquan2.info/wp-content/uploads/2020/07/anh-gai-xinh-hap-dan-nhieu-nam-gioi-16.jpg",
                        "https://ruthamcauquan2.info/wp-content/uploads/2020/07/anh-gai-xinh-hap-dan-nhieu-nam-gioi-16.jpg",
                        "https://ruthamcauquan2.info/wp-content/uploads/2020/07/anh-gai-xinh-hap-dan-nhieu-nam-gioi-16.jpg",
                        "https://ruthamcauquan2.info/wp-content/uploads/2020/07/anh-gai-xinh-hap-dan-nhieu-nam-gioi-16.jpg",
                        "https://itcafe.vn/wp-content/uploads/2021/01/anh-gai-xinh-4.jpg",
                        "https://itcafe.vn/wp-content/uploads/2021/01/anh-gai-xinh-4.jpg"};
    String[] hobbies = {"Reading", "Travel", "Movie", "Music", "Game", "Gym"};


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_other_profile);

        nameAndAge = (TextView) findViewById(R.id.txt_name);
        nameAndAge.setText("Thanh Tâm, 21");

        location = (TextView) findViewById(R.id.txt_Location);
        location.setText("Quảng Bình");

        bio = (TextView) findViewById(R.id.txt_introduce);
        bio.setText("Cô gái dễ mến, yêu màu tím, thích sự chung thủy, ghét sự giả dối");


        imageView = (ImageView) findViewById(R.id.img_avata);
        String url = images[0];
        Picasso.get().load(url)
                .fit().centerCrop()
                .into(imageView);

        addGroupHobby(hobbies);
        addGroupImage(images);

    }

    private void addGroupHobby(String[] hobbies){
        String color = "#FD4C67";
        chipGroup = (ChipGroup) findViewById(R.id.chip_group1);
        for (int i = 0; i < hobbies.length; i++) {
            Chip chip = new Chip(this);
            chip.setTextSize(14);
            chip.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor(color)));
            chip.setChipStrokeWidth(1.5f);
            chip.setText(hobbies[i]);
            chipGroup.addView(chip);
        }

    }


    private void addGroupImage(String[] images)  {
        DisplayMetrics displayMetrics = getBaseContext().getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;


        gridLayout = findViewById(R.id.grid_layout);
        gridLayout.setColumnCount(2);
        gridLayout.setRowCount((int) Math.ceil((double) images.length / 2));
        for (int i = 1, c = 0, r = 0; i < images.length; i++, c++) {
            if (c == 2) {
                c = 0;
                r++;
            }
            ImageView oImageView = new ImageView(this);
            oImageView.setImageResource(R.drawable.logo);
            String url = images[i];
            Picasso.get().load(url).fit().into(oImageView);

            oImageView.setPadding((int) (dpWidth/11.85f), 0, (int) (dpWidth/64), 0);

            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.width = (int) (dpWidth*0.72f);
            param.height = (int) (dpWidth*0.72f*1.18f);
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
                    i.putExtra("position", pos);
                    startActivity(i);

                }
            });
        }
    }


}
