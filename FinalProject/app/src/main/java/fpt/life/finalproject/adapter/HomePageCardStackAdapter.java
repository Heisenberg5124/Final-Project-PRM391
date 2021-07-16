package fpt.life.finalproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import fpt.life.finalproject.MainActivity;
import fpt.life.finalproject.R;
import fpt.life.finalproject.dto.HomePageProfile;
import fpt.life.finalproject.screen.Login.Login_Activity;
import fpt.life.finalproject.screen.register.ui.RegisterActivity;

public class HomePageCardStackAdapter extends RecyclerView.Adapter<HomePageCardStackAdapter.ViewHolder> {

    private List<HomePageProfile> homePageProfileList;
    private InformationHomePageClickedListener informationHomePageClickedListener;

    public HomePageCardStackAdapter(List<HomePageProfile> homePageProfileList,
                                    InformationHomePageClickedListener informationHomePageClickedListener ) {
        this.homePageProfileList = homePageProfileList;
        this.informationHomePageClickedListener = informationHomePageClickedListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycler_view_item_profile_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HomePageCardStackAdapter.ViewHolder holder, int position) {
        holder.setData(homePageProfileList.get(position));
    }

    @Override
    public int getItemCount() {
        return homePageProfileList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name, age, city, distance;
        ImageView infoButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.item_name);
            age = itemView.findViewById(R.id.item_age);
            city = itemView.findViewById(R.id.item_city);
            distance = itemView.findViewById(R.id.item_distance);
            infoButton = itemView.findViewById(R.id.info_btn);
        }

        void setData(HomePageProfile data) {
            Picasso.get()
                    .load(data.getImage())
                    .fit()
                    .centerCrop()
                    .into(image);
            name.setText(data.getName());
            age.setText(String.valueOf(data.getAge()));
            city.setText(data.getCity());
            distance.setText(data.getDistance()+" km");
            //info button
            infoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    informationHomePageClickedListener.onInformationHomePageClickListener(data.getUid());
                }
            });
        }
    }

    public List<HomePageProfile> getHomePageProfileList(){
        return homePageProfileList;
    }

    public void setHomePageProfileList(List<HomePageProfile> items){
        this.homePageProfileList = items;
    }
}
