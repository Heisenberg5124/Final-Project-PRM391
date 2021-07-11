package fpt.life.finalproject.screen.viewOtherProfile;


import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
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
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.squareup.picasso.Picasso;

import fpt.life.finalproject.R;

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


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void addGroupImage(String[] images)  {
        /*DisplayMetrics displayMetrics = getBaseContext().getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        Log.d("Width", "addGroupImage: " + dpWidth);*/

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = (int) (displayMetrics.heightPixels / 3.0 * 4);
        int dpWidth = (int) (displayMetrics.widthPixels);
        Log.d("Width", "addGroupImage: " + dpWidth);

        gridLayout = findViewById(R.id.grid_layout);
        gridLayout.setColumnCount(2);
        gridLayout.setRowCount((int) Math.ceil((double) images.length / 2));
//        gridLayout.setPadding(10,0,10,0);

        for (int i = 1, c = 0, r = 0; i < images.length; i++, c++) {
            if (c == 2) {
                c = 0;
                r++;
            }
            ImageView oImageView = new ImageView(this);
            oImageView.setImageResource(R.drawable.logo);
            String url = images[i];
            Picasso.get().load(url).fit().into(oImageView);

            //oImageView.setPadding((int) ((dpWidth - 10) * 0.075f), 0, (int) ((dpWidth - 10) * 0.075f), 0);

            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            Point size = new Point();
            getWindowManager().getDefaultDisplay().getSize(size);
            int screenWidth = size.x;
            int screenHeight = size.y;
            int halfScreenWidth = (int)(screenWidth *0.5);
            int quarterScreenWidth = (int)(halfScreenWidth * 0.5);
            param.width = halfScreenWidth-50;
            param.height = (int) (halfScreenWidth * 1.5);
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
                    i.putExtra("position", pos);
                    startActivity(i);

                }
            });
        }
    }


}
