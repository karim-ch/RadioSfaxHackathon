package com.RadioSfax.acer.radiosfax;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

/*import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.internal.Utils;
import okhttp3.ResponseBody;*/

public class LoginPopup extends AppCompatActivity {

    Button buttonRegister;
    Button buttonLogin;
    TextView pwdHelp;

    private ProgressDialog mLogProgress;

    //Firebase Auth
    private FirebaseAuth mAuth;

    //to login
    private EditText emailLogin;
    private EditText passwordLogin;
    private DatabaseReference mUserDatabase;
    private ImageView mLoginError;

    private boolean emailVerified =false;




/*
    //-------FACEBOOK-----------
    private CallbackManager callbackManager;
    private String FBtxtEmail,FBtxtBirthday,FBtxtFriends,FBtxtName,FBtxtImage;
    private ProgressDialog mDialog;
    private LoginButton Facebook_login;
    //------------------------


    //-------GMAIL--------------------
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton signInButton;
    private static final int RC_SIGN_IN = 9001;
    //---------------------------------
*/


    private Toolbar mToolbar;
    private TextInputLayout emailfield;
    private TextInputLayout passwordfield;
    private CustomButton loginBtn;
    private AwesomeValidation validator;
    private CustomButton subscribeBtn;
    private static final String TAG = "LoginPopup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_popup);

        //prog bar
        mLogProgress= new ProgressDialog(this);
        //Firebase instance
        mAuth= FirebaseAuth.getInstance();
        //Firebase reference (for token id louta)
        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("users");


        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        setupRules();

        //loose focus edittext
        findViewById(R.id.scroll_pop).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });

        mToolbar =(Toolbar)findViewById(R.id.toolbar_login);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginPopup.this, HomeActivity.class);
                startActivity(i);
            }
        });

        emailfield =(TextInputLayout)findViewById(R.id.email_text_input);
        passwordfield =(TextInputLayout)findViewById(R.id.password_text_input);

        subscribeBtn= (CustomButton) findViewById(R.id.subscribe_button);
        subscribeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginPopup.this , RegistrationActivity.class);
                startActivity(i);

            }
        });

        /* // you are login deja
         if(tokenManager.getToken().getAccessToken() != null){
            startActivity(new Intent(LoginPopup.this, ActivityHome.class));
            finish();
            Toast.makeText(this, "token : "+tokenManager.getToken().getAccessToken(), Toast.LENGTH_SHORT).show();
            Log.w("LoginPopup", "token : "+tokenManager.getToken().getAccessToken());

        }
         */
        loginBtn =(CustomButton)findViewById(R.id.next_button);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailfield.getEditText().getText().toString();
                String password = passwordfield.getEditText().getText().toString();

                emailfield.setError(null);
                passwordfield.setError(null);

                validator.clear();


                if (validator.validate()) {
                    mLogProgress.setTitle("Logging In");
                    mLogProgress.setMessage("Please wait while we check your credentials..");
                    mLogProgress.setCanceledOnTouchOutside(false);
                    mLogProgress.show();
                    loginUser(email, password);
                }
            }
        });

        //-----------------------FACEBOOK LOGIN------------------------------------------
      /*  Facebook_login =(LoginButton) findViewById(R.id.facebook_login);
        callbackManager = CallbackManager.Factory.create();
        Facebook_login.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));
        Facebook_login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mDialog = new ProgressDialog(LoginPopup.this);
                mDialog.setMessage("Retrieving Data");
                mDialog.show();

            //    String accesstoken = loginResult.getAccessToken().getToken();
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        mDialog.dismiss();
                        Log.d("response",response.toString());
                        getData(object);


                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,email,birthday,friends,name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });*/
        //------------------------------------------------------------------------------------------

        //----------------GMAIL--------------------------------------------------------------------------

      /*  // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton=(SignInButton)findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();

            }
        });*/
        //-----------------------------------------------------------------------------------------------




/*

        //scroll click
        findViewById(R.id.mainac).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });

        //prog bar
        mLogProgress= new ProgressDialog(this);
        //Firebase instance
        mAuth= FirebaseAuth.getInstance();
        //Firebase reference (for token id louta)
        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("users");




        mLoginError=(ImageView) findViewById(R.id.login_error);

        //forget password
        pwdHelp = (TextView) findViewById(R.id.pwd_help);
        //login
        emailLogin=(EditText) findViewById(R.id.userEmailLogin);
        passwordLogin=(EditText) findViewById(R.id.userPasswordLogin);
        buttonLogin= (Button) findViewById(R.id.buttonLogin);




        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailLogin.getText().toString();
                String password = passwordLogin.getText().toString();

                if(!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password)){


                    mLogProgress.setTitle("Logging In");
                    mLogProgress.setMessage("Please wait while we check your credentials..");
                    mLogProgress.setCanceledOnTouchOutside(false);
                    mLogProgress.show();


                    loginUser(email, password);

                }

            }
        });

        buttonRegister= (Button) findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regist = new Intent(LoginPopup.this, RegistrationActivity.class);
                startActivity(regist);
            }
        });




        pwdHelp = (TextView) findViewById(R.id.pwd_help);
        pwdHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent pwd_help = new Intent(LoginPopup.this,ForgotPassword.class);
                startActivity(pwd_help);
            }
        });

    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    mLogProgress.dismiss();

                    //Token ID
                    String current_user_id=mAuth.getCurrentUser().getUid();
                    String device_token = FirebaseInstanceId.getInstance().getToken();

                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    emailVerified = currentUser.isEmailVerified();

                    if(emailVerified) {
                        mUserDatabase.child(current_user_id).child("device_token").setValue(device_token)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent login = new Intent(LoginPopup.this, HomeActivity.class);
                                        login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(login);
                                        finish();
                                    }
                                });
                    }else{
                        Toast.makeText(LoginPopup.this, "Please verify your Email Address",Toast.LENGTH_LONG).show();
                    }


                }else{
                    mLogProgress.hide();
                    Toast.makeText(LoginPopup.this, "Cannot Sign In. Please review the form", Toast.LENGTH_SHORT).show();
                    mLoginError.setBackgroundColor(Color.parseColor("#cc1512"));

                }

            }
        });
*/
    }
   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
                case 0: //Facebook
                callbackManager.onActivityResult(requestCode,resultCode,data);
                break;


                case 9001: //GMAIL
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    handleSignInResult(task);
                break;

        }

    }*/

    public void setupRules() {

        validator.addValidation(this, R.id.email_text_input, Patterns.EMAIL_ADDRESS, R.string.err_email);
        validator.addValidation(this, R.id.password_text_input, RegexTemplate.NOT_EMPTY, R.string.err_password);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

/*
//---------------GMAIL SIGN IN---------------------------------------------
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account); //get user data
        } catch (ApiException e) {

            updateUI(null);
        }
    }

    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            String email =   account.getEmail();
            String displayName =  account.getDisplayName();
            String photoUrl =  account.getPhotoUrl().toString();
            String familyName =  account.getFamilyName();

        } else {

        }
    }
    //-------------------------------------------------------------------------
    //------------FACEBOOK GET DATA---------------------------------
   private void getData(JSONObject object) {
        try{
            URL profile_picture = new URL("https://graph.facebook.com/"+object.getString("id")+"/picture?width=250&height=250");

           // Picasso.with(this).load(profile_picture.toString()).into(imgAvatar);

            FBtxtImage= profile_picture.toString();
            FBtxtEmail= object.getString("email");
            FBtxtBirthday=object.getString("birthday");
            FBtxtName =object.getString("name");
            FBtxtFriends = "Friends: "+object.getJSONObject("friends").getJSONObject("summary").getString("total_count");

            //make Facebook as connexion mode into sharedpref
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LoginMode", "fb").apply();

            //Intent to main page
            Intent i = new Intent(LoginPopup.this, HomeActivity.class);
            startActivity(i);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }*/
    //---------------------------------------------------------------

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mLogProgress.dismiss();

                    //Token ID
                    String current_user_id = mAuth.getCurrentUser().getUid();
                    String device_token = FirebaseInstanceId.getInstance().getToken();

                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    emailVerified = currentUser.isEmailVerified();

                    if (emailVerified) {
                        mUserDatabase.child(current_user_id).child("device_token").setValue(device_token)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent login = new Intent(LoginPopup.this, SplashActivity.class);
                                        login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(login);
                                        finish();
                                    }
                                });
                    } else {
                        Toast.makeText(LoginPopup.this, "Please verify your Email Address", Toast.LENGTH_LONG).show();
                    }


                } else {
                    mLogProgress.hide();
                    Toast.makeText(LoginPopup.this, "Cannot Sign In. Please review the form", Toast.LENGTH_SHORT).show();


                }

            }
        });
    }

}