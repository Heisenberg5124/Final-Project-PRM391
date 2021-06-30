package fpt.life.finalproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import fpt.life.finalproject.R;
import fpt.life.finalproject.dto.HomePageProfile;

public class HomePageCardStackAdapter extends RecyclerView.Adapter<HomePageCardStackAdapter.ViewHolder> {

    private List<HomePageProfile> homePageProfileList;

    public HomePageCardStackAdapter(List<HomePageProfile> homePageProfileList) {
        this.homePageProfileList = homePageProfileList;
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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.item_name);
            age = itemView.findViewById(R.id.item_age);
            city = itemView.findViewById(R.id.item_city);
            distance = itemView.findViewById(R.id.item_distance);
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
        }
    }

    public List<HomePageProfile> getHomePageProfileList(){
        return homePageProfileList;
    }

    public void setHomePageProfileList(List<HomePageProfile> items){
        this.homePageProfileList = items;
    }
}
