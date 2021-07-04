package fpt.life.finalproject.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fpt.life.finalproject.R;
import fpt.life.finalproject.model.Hobby;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class HobbyAdapter extends RecyclerView.Adapter<HobbyAdapter.ViewHolder> {

    private ArrayList<Hobby> hobbies;
    private RecyclerItemSelectedListener recyclerItemSelectedListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View hobbyView = inflater.inflate(R.layout.item_recycler_view_register_hobbies, parent, false);

        return new ViewHolder(hobbyView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hobby hobby = hobbies.get(position);

        TextView itemTextViewHobby = holder.itemTextViewHobby;
        ImageView itemImageViewCheck = holder.itemImageViewCheck;
        LinearLayout itemLinearLayout = holder.linearLayout;

        itemTextViewHobby.setText(hobby.getContent());

        if (hobby.isSelected()) {
            itemTextViewHobby.setTextColor(Color.parseColor("#FD4C67"));
            itemImageViewCheck.setVisibility(View.VISIBLE);
        } else {
            itemTextViewHobby.setTextColor(Color.parseColor("#292929"));
            itemImageViewCheck.setVisibility(View.GONE);
        }
    }

    public void filterList(ArrayList<Hobby> filteredList) {
        hobbies = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return hobbies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public LinearLayout linearLayout;
        public TextView itemTextViewHobby;
        public ImageView itemImageViewCheck;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.item_linear_layout_hobby);
            itemTextViewHobby = itemView.findViewById(R.id.item_text_view_hobby);
            itemImageViewCheck = itemView.findViewById(R.id.item_image_view_check);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            recyclerItemSelectedListener.onItemClick(hobbies.get(getAdapterPosition()));
        }
    }
}
