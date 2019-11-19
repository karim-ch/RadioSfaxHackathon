package com.RadioSfax.acer.radiosfax;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.facebook.login.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.Calendar;
import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {

    Boolean btn_clicked = false;
    private CustomButton loginBtn,next_button_sub;
    private Toolbar mToolbar;

    String display_localisation = "", display_date ="";
    //progress Dialog
    private ProgressDialog mRegProgress;

    private TextInputLayout name_text_input_sub ;
    private TextInputLayout tel_text_input_sub ;
    private TextInputLayout email_text_input_sub ;
    private TextInputLayout password_text_input_sub ;

    private AwesomeValidation validator;
    private MaterialSpinner spinnerPlace,spinnerDateDebut;
    DatePickerDialog picker;
    //Firebase Auth
    private FirebaseAuth mAuth;
    //Real Time Database
    private DatabaseReference mDatabase;

    RelativeLayout activity_registration;

    private DatabaseReference mUserDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        activity_registration = (RelativeLayout) findViewById(R.id.linear_registration);

        //progressBar
        mRegProgress= new ProgressDialog(this);

        //Firebase instance
        mAuth= FirebaseAuth.getInstance();


        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("users");



        //loose focus edittext
        findViewById(R.id.scroll_sub).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });

        mToolbar =(Toolbar)findViewById(R.id.toolbar_subscribe);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegistrationActivity.this, HomeActivity.class);
                startActivity(i);
            }
        });

        loginBtn = (CustomButton) findViewById(R.id.login_button);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegistrationActivity.this , LoginPopup.class);
                startActivity(i);
            }
        });

        name_text_input_sub = (TextInputLayout)findViewById(R.id.name_text_input_sub);
        tel_text_input_sub = (TextInputLayout)findViewById(R.id.tel_text_input_sub);
        email_text_input_sub = (TextInputLayout)findViewById(R.id.email_text_input_sub);
        password_text_input_sub = (TextInputLayout)findViewById(R.id.password_text_input_sub);
        next_button_sub = (CustomButton)findViewById(R.id.next_button_sub);

        spinnerPlace = (MaterialSpinner) findViewById(R.id.spinnerPlace);
        spinnerPlace.setItems("Emplacement géographique", "Sfax Ville", "El Hencha",
                "Sakiet Eddaier","Manzel Chaker","Kerkenah","Jebeniana","Sakiet Ezzit","Sfax Est","Ghraiba",
                "Agareb","El Amra","Bir Ali Ben Khelifa","Mahras","Esskhira");

        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        validator.addValidation(this, R.id.name_text_input_sub, RegexTemplate.NOT_EMPTY, R.string.err_name);
        validator.addValidation(this, R.id.tel_text_input_sub, RegexTemplate.TELEPHONE, R.string.err_phone);
        validator.addValidation(this, R.id.email_text_input_sub, Patterns.EMAIL_ADDRESS, R.string.err_email);
        validator.addValidation(this, R.id.password_text_input_sub, "[a-zA-Z0-9]{6,}", R.string.err_password);


        /*
        if(tokenManager.getToken().getAccessToken() != null){
            startActivity(new Intent(SubscribeActivity.this, ActivityHome.class));
            finish();
            Toast.makeText(this, "token : "+tokenManager.getToken().getAccessToken(), Toast.LENGTH_SHORT).show();
            Log.w("SubscribeActivity", "token : "+tokenManager.getToken().getAccessToken());

        }*/

        next_button_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String display_nickname = name_text_input_sub.getEditText().getText().toString();
                String display_phone = tel_text_input_sub.getEditText().getText().toString();
                String display_email = email_text_input_sub.getEditText().getText().toString();
                String display_password = password_text_input_sub.getEditText().getText().toString();

                name_text_input_sub.setError(null);
                tel_text_input_sub.setError(null);
                email_text_input_sub.setError(null);
                password_text_input_sub.setError(null);

                if(btn_clicked)
                {
                   Intent i = new Intent(RegistrationActivity.this, LoginPopup.class);
                   startActivity(i);
                }

                validator.clear();
                if(validator.validate()) {
                    mRegProgress.setTitle("Création du compte...");
                    mRegProgress.setMessage("Veuillez patienter pendant que nous créons votre compte !");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();

                    display_localisation= spinnerPlace.getText().toString();
                    register_user(display_email, display_password, display_nickname,display_phone, display_localisation, display_date);
                }
            }
        });

        spinnerDateDebut = (MaterialSpinner) findViewById(R.id.spinnerDateDebut);

        spinnerDateDebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);


                // date picker dialog
                picker = new DatePickerDialog(RegistrationActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                display_date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                spinnerDateDebut.setText(display_date);
                                spinnerDateDebut.collapse();
                            }
                        }, year, month, day);

                picker.updateDate(1990, 0, 1);
                picker.show();
            }
        });



}



    private void register_user(final String display_email,
                               final String display_password,
                               final String display_nickname,
                               final String display_phone ,
                               final String display_localisation,
                               final String display_date
                               ) {

        mAuth.createUserWithEmailAndPassword(display_email, display_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if (task.isSuccessful()) {

                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

                    String device_token = FirebaseInstanceId.getInstance().getToken();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(display_nickname).build();

                    current_user.updateProfile(profileUpdates);


                    HashMap<String, String> userMap = new HashMap<String, String>();
                    userMap.put("name", display_nickname);
                    userMap.put("phone", display_phone);
                    userMap.put("device_token", device_token);
                    userMap.put("position", display_localisation);
                    userMap.put("birthday", display_date);
                    userMap.put("image", "default");
                    userMap.put("thumb_image", "default");
                    userMap.put("points", "0");
                    userMap.put("badge", "Ecouteur normal");

                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                FirebaseAuth auth = FirebaseAuth.getInstance();
                                FirebaseUser user = auth.getCurrentUser();

                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Snackbar snackbar = Snackbar.make(activity_registration,"Nous avons envoyé une confirmation par e-mail à: "+display_email,Snackbar.LENGTH_LONG);
                                                    snackbar.show();

                                                    next_button_sub.setText("Login");
                                                    btn_clicked=true;
                                                    mRegProgress.hide();
                                                }else{
                                                    mRegProgress.hide();
                                                    Toast.makeText(RegistrationActivity.this, "Impossible de s'inscrire S'il vous plaît examiner le formulaire", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }
                    });


                }
            }
        });
    }






}























    /*
    private static final  String TAG = "Registration";
    private Toolbar mToolbar;


    private Spinner spinner;

    private EditText email;
    private EditText cin;
    private EditText phone;
    private EditText nickname;
    private EditText password;
    private EditText confirm_password;
    private Button buttonRegister;


    private LinearLayout activity_registration;


    Boolean cond_name = false;
    Boolean cond_cin = false;

    Boolean cond_email = false;
    Boolean cond_password = false;
    Boolean cond_confirm_password = false;
    Boolean cond_phone = false;

    Boolean btn_clicked = false;


    //progress Dialog
    private ProgressDialog mRegProgress;

    //Firebase Auth
    private FirebaseAuth mAuth;
    //Real Time Database
    private DatabaseReference mDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        activity_registration = (LinearLayout) findViewById(R.id.linear_registration);


        //ToolBar
      //  mToolbar = (Toolbar) findViewById(R.id.registration_toolbar);
     //   setSupportActionBar(mToolbar);
     //   getSupportActionBar().setTitle("Créer un compte");
     //   getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //scroll click
      //  findViewById(R.id.registr).setOnTouchListener(new View.OnTouchListener() {
        //    @Override
        //    public boolean onTouch(View view, MotionEvent motionEvent) {
        //        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        //        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        //        return false;
       //     }
       // });*/

/*

        //progressBar
        mRegProgress= new ProgressDialog(this);

        //Firebase instance
        mAuth= FirebaseAuth.getInstance();


        buttonRegister=(Button) findViewById(R.id.buttonRegister);

        email=(EditText) findViewById(R.id.email);
        cin=(EditText) findViewById(R.id.cin);
        password=(EditText) findViewById(R.id.password);
        confirm_password=(EditText) findViewById(R.id.confirm_password);
        nickname=(EditText) findViewById(R.id.nickname);
        phone=(EditText) findViewById(R.id.phone);



        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String display_email = email.getText().toString();
                String display_cin = cin.getText().toString();
                String display_password = password.getText().toString();
                String display_password_confirm = confirm_password.getText().toString();
                String display_phone = phone.getText().toString();
                String display_nickname = nickname.getText().toString();
                String display_localisation = "";
                if(btn_clicked)
                {
                    Intent MainIntent= new Intent(RegistrationActivity.this,LoginPopup.class);
                    MainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(MainIntent);
                    finish();
                }
                else{
                    if(display_nickname.length()<3 ){
                        cond_name=false;
                        nickname.setError("Votre nom doit contenir aux moin 3 charactères");
                    } else{ cond_name=true;}

                    if(display_cin.length()!=8 ){
                        cond_cin=false;
                        cin.setError("Votre nom doit contenir aux moin 3 charactères");
                    } else{ cond_cin=true;}


                    if(!Patterns.EMAIL_ADDRESS.matcher(display_email).matches()){
                        cond_email=false;
                        email.setError("E-mail invalide");
                    } else{ cond_email=true;}

                    if(display_password.length()<6 ){
                        cond_password=false;

                        password.setError("Votre mot de passe doit contenir aux moin 6 charactères");
                    } else{ cond_password=true;}

                    if(display_phone.length()!= 8){
                        cond_phone=false;
                        phone.setError("Tél invalide");
                    } else{ cond_phone=true;}


                    if(!display_password_confirm.equals(display_password)|| display_password_confirm.equals("")){
                        cond_confirm_password=false;
                        confirm_password.setError("Non concordance des mots de passe");
                    } else{ cond_confirm_password=true;}


                    if(cond_phone && cond_cin && cond_confirm_password && cond_email && cond_password && cond_name){
                        mRegProgress.setTitle("Création du compte...");


                        mRegProgress.setMessage("Veuillez patienter pendant que nous créons votre compte !");
                        mRegProgress.setCanceledOnTouchOutside(false);
                        mRegProgress.show();

                        display_localisation=spinner.getSelectedItem().toString();
                        register_user(display_email, display_password, display_nickname,display_phone,display_cin, display_localisation);
                    }



                }}

            private void register_user(final String display_email, final String display_password, final String display_nickname,final String display_phone ,final String display_cin, final String display_localisation) {

                mAuth.createUserWithEmailAndPassword(display_email, display_password ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser current_user= FirebaseAuth.getInstance().getCurrentUser();
                            String uid = current_user.getUid();
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

                            String device_token= FirebaseInstanceId.getInstance().getToken();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(display_nickname).build();

                            current_user.updateProfile(profileUpdates);


                            HashMap<String, String> userMap = new HashMap<String, String>();
                            userMap.put("name",display_nickname);
                            userMap.put("phone",display_phone);
                            userMap.put("cin",display_cin);
                            userMap.put("device_token",device_token);
                            userMap.put("position",display_localisation);
                            userMap.put("image","default");
                            userMap.put("thumb_image","default");
                            userMap.put("points","0");
                            userMap.put("badge","Citoyen");




                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        FirebaseAuth auth = FirebaseAuth.getInstance();
                                        FirebaseUser user = auth.getCurrentUser();

                                        user.sendEmailVerification()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Snackbar snackbar = Snackbar.make(activity_registration,"Nous avons envoyé une confirmation par e-mail à: "+email.getText().toString(),Snackbar.LENGTH_LONG);
                                                            snackbar.show();

                                                            buttonRegister.setText("Login");
                                                            btn_clicked=true;
                                                            mRegProgress.hide();
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });




                        } else{
                            mRegProgress.hide();
                            Toast.makeText(RegistrationActivity.this, "Impossible de s'inscrire S'il vous plaît examiner le formulaire", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });








        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.localisation, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);





    }


}
*/
