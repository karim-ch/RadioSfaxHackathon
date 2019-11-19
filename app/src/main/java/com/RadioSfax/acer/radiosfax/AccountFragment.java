package com.RadioSfax.acer.radiosfax;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.RadioSfax.acer.radiosfax.CustomFonts.FontTextViewLight;
import com.RadioSfax.acer.radiosfax.CustomFonts.FontTextViewRegular;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import at.markushi.ui.CircleButton;


public class AccountFragment extends Fragment {
    private MaterialButton EditButton, lire_suite_btn;
    private Dialog MyDialog;

    //Firebase Auth
    private FirebaseAuth mAuth;
    //Real Time Database
    private DatabaseReference mUserRef;
    private DatabaseReference mRecDatabase;
    //--------------------
    private RecyclerView mSuggList;
    private DatabaseReference mSuggestDatabase;
// --------------------------


    View view;
    String id = "";
    boolean connexion = false;

    FontTextViewRegular name_text;
    FontTextViewLight location_text;
    FontTextViewRegular points_text;

    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //-----------CHECK CONNEXION------------------------------------------------------
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {



            mUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
            id = mAuth.getCurrentUser().getUid();
            view = inflater.inflate(R.layout.fragment_account, container, false);

            name_text = view.findViewById(R.id.name_text);
            location_text = view.findViewById(R.id.location_text);
            points_text = view.findViewById(R.id.points_text);


            mUserRef.child("name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    name_text.setText(dataSnapshot.getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mUserRef.child("position").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    location_text.setText(dataSnapshot.getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mUserRef.child("points").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    points_text.setText(dataSnapshot.getValue().toString() + " Points de fidélités");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            MyDialog = new Dialog(getContext());
            EditButton = (MaterialButton) view.findViewById(R.id.edit_button);
            EditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowPopup(view);
                }
            });

            lire_suite_btn = view.findViewById(R.id.lire_suite_btn);
            lire_suite_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowPopupBadge(v);
                }
            });


            //--------------------------------------------------------------
            mSuggList = view.findViewById(R.id.news_list);

            mSuggestDatabase = FirebaseDatabase.getInstance().getReference().child("images");
            mSuggestDatabase.keepSynced(true);

            mSuggList.setHasFixedSize(true);
            mSuggList.setLayoutManager(new LinearLayoutManager(getContext()));
            //--------------------------------------------------------------


        } else {
            view = inflater.inflate(R.layout.no_connexion_layout, container, false);
            CustomButton loginBtn = (CustomButton) view.findViewById(R.id.no_internet);
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(), LoginPopup.class);
                    startActivity(i);
                }
            });

        }


        //------------------------------------------------------------------------------------------


        return view;
    }

    public void ShowPopup(View v) {

        MyDialog.setContentView(R.layout.dialog_account_layout1);
        CircleButton cr = (CircleButton) MyDialog.findViewById(R.id.close_dialog_btn1);
        cr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDialog.dismiss();
            }
        });

        MyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        MyDialog.show();
    }

    public void ShowPopupBadge(View v) {

        MyDialog.setContentView(R.layout.dialog_account_layout_badge);
        CircleButton cr = (CircleButton) MyDialog.findViewById(R.id.close_dialog_btn2);
        cr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDialog.dismiss();
            }
        });

        MyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        MyDialog.show();
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<NewsValues, NewsViewHolder> firebaseRecylerAdapter = new FirebaseRecyclerAdapter<NewsValues, NewsViewHolder>(

                NewsValues.class,
                R.layout.reclamations_single_layout,
                NewsViewHolder.class,
                mSuggestDatabase
        ) {
            @Override
            protected void populateViewHolder(final NewsViewHolder viewHolder, NewsValues model, final int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setIsDone(model.getIsDone());
                viewHolder.setImage(model.getImage(), getContext());


            }
        };

        mSuggList.setAdapter(firebaseRecylerAdapter);

    }



    public static class NewsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public NewsViewHolder(View itemView) {
            super(itemView);
            mView= itemView;
        }


        public void setIsDone(String date){
            TextView suggestion_single_date =(TextView) mView.findViewById(R.id.reclamations_single_status);

            if(date.equals("0")){
                suggestion_single_date.setText("En attente d'apporovation");
            }else if(date.equals("1")){
                suggestion_single_date.setText("Approvée");
            }



        }
        public void setTitle(String name){

            TextView suggestionname =(TextView) mView.findViewById(R.id.reclamations_single_title);
            suggestionname.setText(name);

        }

        public void setImage(String image, Context ctx){

            ImageView SuggestionImg =(ImageView) mView.findViewById(R.id.reclamations_single_image);
            Picasso.with(ctx).load(image).placeholder(R.drawable.user_icon).into(SuggestionImg);


        }



    }
}