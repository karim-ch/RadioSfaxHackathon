package com.RadioSfax.acer.radiosfax;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.RadioSfax.acer.radiosfax.CustomFonts.FontTextSourceSansProRegular;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private FontTextSourceSansProRegular ft;
    private Boolean isFilterActive = false;
    private NavigationView navigationView;
    private Menu menu;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;

    boolean connexion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()!= null) {
            mUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        }





        //toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       // ft  = (FontTextSourceSansProRegular) findViewById(R.id.tool_title);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
       // ft.setText("Acceuil");

        //drawer
        drawer = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();

        MenuItem nav_connexion_disconnexion =menu.findItem(R.id.drawer_item_disconnect);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null) {
            connexion=false;
            nav_connexion_disconnexion.setTitle("Connexion");
            nav_connexion_disconnexion.setIcon(R.drawable.ic_person);
        }else{
            connexion=true;
            nav_connexion_disconnexion.setTitle("Deconnexion");
            nav_connexion_disconnexion.setIcon(R.drawable.ic_disconnect);
        }




        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this , drawer , toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close );


        drawer.addDrawerListener(toggle);
        toggle.syncState(); //rotate icon


        //when first time open Acceuil
        if(savedInstanceState==null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_acceuil);

        }

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.nav_acceuil:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                //toolbar.setTitle("");
               // ft.setText("Acceuil");
                menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.cursor));
                isFilterActive=false;
                break;


            case R.id.nav_chat:
                if(connexion){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new ChatFragment()).commit();
                  //  ft.setText("Chat publique");
                    isFilterActive=true;
                }else{
                    Intent i = new Intent(HomeActivity.this, LoginPopup.class);
                    startActivity(i);
                }
                break;

            case R.id.nav_programme:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProgrammeFragment()).commit();
                isFilterActive=true;
                break;

            case R.id.nav_equipe:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new EquipeFragment()).commit();
                isFilterActive=true;
                break;

            case R.id.nav_contact:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ContactFragment()).commit();
                isFilterActive=true;
                break;

            case R.id.nav_account:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AccountFragment()).commit();
                isFilterActive=true;
                break;

            case R.id.nav_params:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ParamsFragment()).commit();
                isFilterActive=true;
                break;


            case R.id.drawer_item_disconnect:

                if(connexion){
                    FirebaseAuth.getInstance().signOut();
                    Intent i = new Intent(HomeActivity.this, HomeActivity.class);
                    startActivity(i);
                }else{
                    Intent i = new Intent(HomeActivity.this, LoginPopup.class);
                    startActivity(i);
                }



                break;





        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override //when back pressed close the drawer
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }


    //----------------------TOOLBAR ACTION--------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;


        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    //-----------------go to reclamations------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(!connexion) {
            Intent Startintent = new Intent(HomeActivity.this, LoginPopup.class);
            startActivity(Startintent);
            finish();
        }
        else{
            FullScreenDialogFragmentFilter fragment = new FullScreenDialogFragmentFilter ();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.add(R.id.drawer_layout, fragment)
                    .addToBackStack(null).commit();
        }


        return super.onOptionsItemSelected(item);
    }
    //---------------------------------------------------------------------------------------





}


