package com.RadioSfax.acer.radiosfax.ActualityRecyclerViewClasses;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.RadioSfax.acer.radiosfax.R;

import java.util.List;

public class ActualityAdapter extends RecyclerView.Adapter<ActualityAdapter.ActualityViewHolder> {
    private static ClickListener clickListener;

    private Context mCtx;
    private List<ActualityModel> actualityList;

    public ActualityAdapter(Context mCtx, List<ActualityModel> actualityList) {
        this.mCtx = mCtx;
        this.actualityList = actualityList;
    }



    @NonNull
    @Override
    public ActualityViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater layoutInflater = LayoutInflater.from(mCtx);
        View view = layoutInflater.inflate(R.layout.actualite_single_layout, null);
        ActualityViewHolder holder = new ActualityViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ActualityViewHolder formationsViewHolder, int i) {
        ActualityModel actualityModel= actualityList.get(i);

        formationsViewHolder.textViewtitle.setText(actualityModel.getTitle());
        formationsViewHolder.imageView.setImageDrawable(mCtx.getResources().getDrawable(actualityModel.getImage()));

    }

    @Override
    public int getItemCount() {
        return actualityList.size();
    }


    class ActualityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        ImageView imageView;
        TextView textViewtitle;

        public ActualityViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.actuality_single_image);
            textViewtitle = itemView.findViewById(R.id.actuality_single_title);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onItemLongClick(getAdapterPosition(), view);
            return false;
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        ActualityAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View view);
        void onItemLongClick(int position, View view);
    }


}
