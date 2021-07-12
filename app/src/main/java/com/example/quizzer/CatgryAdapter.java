package com.example.quizzer;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CatgryAdapter extends RecyclerView.Adapter<CatgryAdapter.viewholder> {

    private List<CatgryModel> catgryModelList;

    public CatgryAdapter(List<CatgryModel> catgryModelList) {
        this.catgryModelList = catgryModelList;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatgryAdapter.viewholder holder, int position) {

        holder.setData(catgryModelList.get(position).getUrl(),catgryModelList.get(position).getName(),catgryModelList.get(position).getSets());

    }

    @Override
    public int getItemCount() {
        return catgryModelList.size();
    }

    class viewholder extends RecyclerView.ViewHolder{

        private CircleImageView imageView;
        private TextView title;

        public viewholder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            title = itemView.findViewById(R.id.title);

        }

        private void setData(String url, final String title,final int sets){
            Glide.with(itemView.getContext()).load(url).into(imageView);
            this.title.setText(title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent setIntent = new Intent(itemView.getContext(),SetsActivity.class);
                    setIntent.putExtra("title",title);
                    setIntent.putExtra("sets",sets);
                    itemView.getContext().startActivity(setIntent);
                }
            });
        }
    }
}
