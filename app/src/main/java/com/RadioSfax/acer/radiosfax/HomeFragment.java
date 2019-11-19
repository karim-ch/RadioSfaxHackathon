package com.RadioSfax.acer.radiosfax;


import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.RadioSfax.acer.radiosfax.ActualityRecyclerViewClasses.ActualityAdapter;
import com.RadioSfax.acer.radiosfax.ActualityRecyclerViewClasses.ActualityModel;
import com.RadioSfax.acer.radiosfax.CustomFonts.FontTextSourceSansProRegular;
import com.RadioSfax.acer.radiosfax.player.PlaybackStatus;
import com.RadioSfax.acer.radiosfax.player.RadioManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    //--------------------
    RecyclerView recyclerViewActuality;
    ActualityAdapter adapterActuality;
    List<ActualityModel> actualityModelList;

   // RadioManager radioManager;
    //String streamURL;
  //  ImageButton trigger;
  //  View subPlayer;

    ImageView chat,sondage,concours;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;

    boolean connexion = false;
    public HomeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container , false);

        //ft  = (FontTextSourceSansProRegular) view.findViewById(R.id.tool_title);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()!= null) {
            mUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        }
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null) {
            connexion=false;
        }else{
            connexion=true;
        }

        chat = view.findViewById(R.id.chat);
        sondage = view.findViewById(R.id.sondage);
        concours = view.findViewById(R.id.concours);

        concours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(),ConcoursActivity.class);
                startActivity(i);
            }
        });


        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(connexion){
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new ChatFragment()).commit();

                }else{
                    Intent i = new Intent(getContext(), LoginPopup.class);
                    startActivity(i);
                }


            }
        });

        sondage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(),SondageActivity.class);
                startActivity(i);
            }
        });


        //-------------------------------------------------------
        actualityModelList= new ArrayList<>();
        recyclerViewActuality =(RecyclerView) view.findViewById(R.id.actuality_recycle);
        recyclerViewActuality.setHasFixedSize(true);
        recyclerViewActuality.setLayoutManager(new LinearLayoutManager(getContext()));



        actualityModelList.add(
                new ActualityModel(
                        "L'état du climat : dimanche le 02/12/2018",
                        R.drawable.climat
                )
        );

        actualityModelList.add(
                new ActualityModel(
                        "Visite du ministre des transports à sfax ",
                        R.drawable.visite
                )
        );



        adapterActuality = new ActualityAdapter(getContext(), actualityModelList);
        recyclerViewActuality.setAdapter(adapterActuality);


        //------------------------MAKE SPACE BETWEEN ITEMS-----------------------
        SpacesItemDecoration spacesItemDecoration1 = new SpacesItemDecoration(15);
        recyclerViewActuality.addItemDecoration(spacesItemDecoration1);
        //----------------------------------------------------------------------

        //-----------------------LISTNER FOR ITEMS CLICKS--------------------------------------------------
        adapterActuality.setOnItemClickListener(new ActualityAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                //  Toast.makeText(getContext(), "onItemClick position: " + position, Toast.LENGTH_SHORT).show();

               // Intent i = new Intent(getContext(), OpenActualityActivity.class);
               // startActivity(i);
            }

            @Override
            public void onItemLongClick(int position, View v) {
                //    Toast.makeText(getContext(), "onItemLongClick pos: " + position, Toast.LENGTH_SHORT).show();

            }
        });
        //------------------------------------------------------------------------------------------------




        return view;
    }

}
