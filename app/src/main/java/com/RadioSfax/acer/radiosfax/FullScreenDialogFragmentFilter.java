package com.RadioSfax.acer.radiosfax;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.RadioSfax.acer.radiosfax.CustomFonts.FontTextArial;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import at.markushi.ui.CircleButton;
import id.zelory.compressor.Compressor;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

import static android.app.Activity.RESULT_OK;


public class FullScreenDialogFragmentFilter extends DialogFragment {


    //---------------------------------
    LocationManager lm;
    //-------------------------------------------


    private static final int GALLERY_PICK = 1;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int REQUEST_LOCATION = 1;

    byte[] thumb_byte;
    Uri resultUri;
    File thumb_filePath;

    String currentTime = "",currentTimePhoto ="";

    private MaterialSpinner spinnerCategories;
    private ImageView im;
    private boolean record = false;
    private String mFileName = null;

    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer = null;
    private ImageView playBtn;

    private PulsatorLayout mPulsator;
    private PulsatorLayout mPulsatorPlay;

    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    boolean mStartPlaying = true;

    CustomButton envoyer_btn;

    Chronometer simpleChronometer;
    long recordTime = 0;

    private FontTextArial topText, topText2;

    private ImageView camBtn;

    private StorageReference mStorage;

    private ProgressDialog mProgress;
    LocationListener locationListenerGPS;

    //Firebase Auth
    private FirebaseAuth mAuth;
    //Real Time Database
    private DatabaseReference mUserRef;
    private DatabaseReference mRecDatabase;

    String id = "";

    boolean connexion=false;

    EditText title,descrip;

    //-------------------dialog-------------------------------------
    private Dialog MyDialog;
    //------------

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_full_screen_dialog_fragment_filter, container, false);
        (rootView.findViewById(R.id.button_close_filter)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        mAuth= FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //-----------CHECK CONNEXION------------------------------------------------------
        if (currentUser!= null) {
            mUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
            id = mAuth.getCurrentUser().getUid();
        }else{
            Intent i = new Intent(getContext(),LoginPopup.class);
            startActivity(i);
            getActivity().finish();
        }

        mRecDatabase = FirebaseDatabase.getInstance().getReference();

        mStorage = FirebaseStorage.getInstance().getReference();
        mProgress = new ProgressDialog(getActivity());

        camBtn = rootView.findViewById(R.id.camBtn);

        topText = rootView.findViewById(R.id.topText);
        topText2 = rootView.findViewById(R.id.topText2);
        envoyer_btn = rootView.findViewById(R.id.envoyer_btn);

        mPulsator = rootView.findViewById(R.id.pulsator);
        mPulsatorPlay = rootView.findViewById(R.id.pulsator1);
        mPulsator.setDuration(1500);
        mPulsatorPlay.setDuration(1500);


        ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        simpleChronometer = rootView.findViewById(R.id.simpleChronometer);


        MyDialog = new Dialog(getContext());
        camBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "Sélectionner une image"), GALLERY_PICK);
                currentTimePhoto = String.valueOf(System.currentTimeMillis());

            }
        });


        im = rootView.findViewById(R.id.imageaa);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!record) {
                    record = true;

                    mFileName = getActivity().getExternalCacheDir().getAbsolutePath();
                    currentTime = String.valueOf(System.currentTimeMillis());
                    mFileName += currentTime + ".3gp";
                    startRecording();
                    mPulsator.start();

                    //---------START CHRONO-----------------------------
                    simpleChronometer.setBase(SystemClock.elapsedRealtime());
                    simpleChronometer.setVisibility(View.VISIBLE);
                    simpleChronometer.start();
                    topText.setText("Appuyez sur le bouton rouge pour terminer l'enregistrement");
                    topText2.setText("Enregistrement du son ...");
                    //----------------------------------------

                } else {
                    recordTime = SystemClock.elapsedRealtime() - simpleChronometer.getBase();
                    if (recordTime > 4000) {
                        topText.setText("Enregistrement terminé ! ");
                        topText2.setText("Vous pouvez nous envoyer le son !");
                        stopRecording();
                        mPulsator.stop();
                        record = false;
                        simpleChronometer.stop();
                        mPulsatorPlay.setVisibility(View.VISIBLE);
                    }
                }
            }
        });


        playBtn = rootView.findViewById(R.id.playBtn1);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {

                    //---------START CHRONO-----------------------------
                    simpleChronometer.setBase(SystemClock.elapsedRealtime());
                    simpleChronometer.start();
                    //----------------------------------------
                    simpleChronometer.setOnChronometerTickListener(null);

                    playBtn.setBackground((Drawable) getResources().getDrawable(R.drawable.stop));
                    mPulsatorPlay.start();


                } else {
                    playBtn.setBackground((Drawable) getResources().getDrawable(R.drawable.play));
                    mPulsatorPlay.stop();
                }
                mStartPlaying = !mStartPlaying;

            }
        });

        //Spinner of catégories
        spinnerCategories = (MaterialSpinner) rootView.findViewById(R.id.spinnerCategories);
        spinnerCategories.setItems("Choisir une catégorie", "Réclamation", "Suggestion", "Dédicace");


        //---------------------envoyer------------------------------------------------
        envoyer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //-----------------current date --------------------------------
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String currentDateandTime = sdf.format(new Date());
                //--------------------------------------------------------------

                upload(currentDateandTime, "34.34653",
                        "10.23545343"
                        , spinnerCategories.getText().toString());
            }
        });

        return rootView;
    }


   /* @Override
    public void onLocationChanged(Location location) {
      latitude = String.valueOf(location.getLatitude());
      longitude = String.valueOf(location.getLongitude());
    }*/






    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("Recorded_log", "prepare() failed");
        }

        mRecorder.start();



        simpleChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
              if(( SystemClock.elapsedRealtime() - chronometer.getBase()> 10000 )&&record){
                  topText.setText("Enregistrement terminé ! ");
                  topText2.setText("Vous pouvez nous envoyer le son !");
                  stopRecording();
                  mPulsator.stop();
                  record = false;
                  simpleChronometer.stop();
                  mPulsatorPlay.setVisibility(View.VISIBLE);

              }
            }
        });
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        envoyer_btn.setVisibility(View.VISIBLE);

        simpleChronometer.stop();
       // playBtn.setBackground((Drawable)getResources().getDrawable(R.drawable.stop));
        mPulsatorPlay.stop();
        simpleChronometer.setOnChronometerTickListener(null);

    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e("Playing_log", "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;

            case REQUEST_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                }
                return;

        }
        if (!permissionToRecordAccepted ) getActivity().finish();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();


            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(getContext(),this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                resultUri = result.getUri();
                showPopup();

               // thumb_filePath = new File(resultUri.getPath());


                //compressing image to thumb
               /* Bitmap thumb_bitmap = new Compressor(getActivity())
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(75)
                        .compressToBitmap(thumb_filePath);
                //getting thmub
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                thumb_byte = baos.toByteArray();*/
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

            }


        }
     //   showPopup();
    }

    private void upload(final String time,final String longitude,final String latitude,final String descrip){

        FirebaseUser current_user= FirebaseAuth.getInstance().getCurrentUser();
        String uid = current_user.getUid();

        mProgress.setMessage("Envoie des données vers le serveur...");
        mProgress.show();

        StorageReference filepath  = mStorage.child("Audio").child(currentTime+".3gp");
        Uri uri = Uri.fromFile(new File(mFileName));

        filepath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    HashMap<String, Object> recMap = new HashMap<>();
                    recMap.put("time", time);
                    recMap.put("longitude",longitude);
                    recMap.put("latitude",latitude);
                    recMap.put("type","sound");
                    recMap.put("url",currentTime+".3gp");
                    recMap.put("id",id);
                    recMap.put("content",descrip);
                    recMap.put("isSeen","0");
                    recMap.put("isDone","0");

                    mRecDatabase.child("sound").push().setValue(recMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Succès d'upload!", Toast.LENGTH_LONG).show();
                                mProgress.dismiss();
                                topText.setText("Merci pour votre collaboration !");
                            }else {
                                Toast.makeText(getContext(), "Erreur d'upload!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else{
                    Toast.makeText(getContext(), "Erreur d'upload son!", Toast.LENGTH_SHORT).show();
                }

            }
        });



    }


    private Location getLastBestLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }


        Location locationGPS = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }
    }

    public void showPopup(){

        MyDialog.setContentView(R.layout.dialog_account_layout);
        CircleButton cr= (CircleButton) MyDialog.findViewById(R.id.close_dialog_btn);
        cr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDialog.dismiss();
            }
        });

        CircleButton dn= (CircleButton) MyDialog.findViewById(R.id.done);

         title = MyDialog.findViewById(R.id.imageTitle);
         descrip = MyDialog.findViewById(R.id.imageDescrip);

        dn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if(!title.getText().toString().isEmpty()&&!descrip.getText().toString().isEmpty() ){
                  //-----------------current date --------------------------------
                  SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                  String currentDateandTime = sdf.format(new Date());
                  //--------------------------------------------------------------

                  uploadPhoto(currentDateandTime, "34.34653",
                          "10.23545343"
                          , spinnerCategories.getText().toString(),title.getText().toString(),descrip.getText().toString());
              }
            }
        });



        MyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        MyDialog.show();
    }




    private void uploadPhoto(final String time,final String longitude,final String latitude,
                             final String descrip,final String title, final String d){

        FirebaseUser current_user= FirebaseAuth.getInstance().getCurrentUser();
        String uid = current_user.getUid();

        mProgress.setMessage("Envoie des données vers le serveur...");
        mProgress.show();

        StorageReference filepath  = mStorage.child("Images").child(currentTimePhoto+".jpg");
        Uri uri = resultUri;

        filepath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    HashMap<String, Object> recMap = new HashMap<>();
                    recMap.put("time", time);
                    recMap.put("longitude",longitude);
                    recMap.put("latitude",latitude);
                    recMap.put("type","image");
                    recMap.put("url",currentTimePhoto+".jpg");
                    recMap.put("id",id);
                    recMap.put("content",descrip);
                    recMap.put("title",title);
                    recMap.put("descrip",d);
                    recMap.put("isSeen","0");
                    recMap.put("isDone","0");

                    mRecDatabase.child("images").push().setValue(recMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Succès d'upload!", Toast.LENGTH_LONG).show();
                                MyDialog.dismiss();
                                mProgress.dismiss();
                                topText.setText("Merci pour votre collaboration !");
                            }else {
                                Toast.makeText(getContext(), "Erreur d'upload!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else{
                    Toast.makeText(getContext(), "Erreur d'upload image!", Toast.LENGTH_SHORT).show();
                }

            }
        });



    }

}