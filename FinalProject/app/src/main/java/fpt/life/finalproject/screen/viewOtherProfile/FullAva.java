package fpt.life.finalproject.screen.viewOtherProfile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import fpt.life.finalproject.R;

public class FullAva extends AppCompatActivity {

    private String avaUrl;
    private ImageView avaFullSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_ava);

        if (savedInstanceState==null) {
            Intent i = getIntent();
            avaUrl = i.getStringExtra("avaUrl");
        }

        avaFullSize = (ImageView) findViewById(R.id.image_ava_full);
        Picasso.get()
                .load(avaUrl)
                .into(avaFullSize);

    }
}