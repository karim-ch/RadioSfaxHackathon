package com.RadioSfax.acer.radiosfax;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;



    public MessageAdapter(List<Messages> mMessageList) {
        this.mMessageList = mMessageList;
    }



    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout ,parent, false);
        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView messageText;
        public TextView displayName;
        public CircleImageView profileImage;
        public ImageView messageImage;


        public MessageViewHolder(View view){
            super(view);
            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            displayName = (TextView) view.findViewById(R.id.name_layout);
            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);


        }

    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {



        Messages c = mMessageList.get(position);
        String from_user = c.getFrom();
        String message_type = c.getType();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();

                holder.displayName.setText(name);

                Picasso.with(holder.profileImage.getContext()).load(image)
                        .placeholder(R.drawable.usericon).into(holder.profileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        if (message_type.equals("text")){

            holder.messageImage.setVisibility(View.GONE);
            holder.messageText.setText(c.getMessage());
        }else{
            holder.messageText.setVisibility(View.GONE);
            Picasso.with(holder.messageImage.getContext()).load(c.getMessage())
                    .placeholder(R.drawable.default_image).into(holder.messageImage);

        }




    }


    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


}


