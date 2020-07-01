package com.example.hp.abhishekblogapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageMetadata;

import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class AddNewDoctor extends AppCompatActivity {
    private static final int MAX_LENGTH = 400 ;
    private Toolbar toolbarNewDoctor;
    private Button AddBtn;
    private EditText newDoctorName;
    private EditText newDoctorSpeciality;
    private EditText newDoctorContact;
    private EditText newDoctorAddress;
    private String user_id;
    private FirebaseAuth mauth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private ImageView newDoctorImg;
    private Uri main_uri = null;
    private ProgressBar progressBar;
    private  String doctorName;
    private  String doctorSpeciality;
    private  String doctorContact;
    private  String doctorAddress;
    private Bitmap compressedImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_doctor);

        //Toolbar
        toolbarNewDoctor = findViewById(R.id.toolbarAddDoctor);
        setSupportActionBar(toolbarNewDoctor);
        getSupportActionBar().setTitle("Add New Doctor");
        //add back button to parent activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mauth = FirebaseAuth.getInstance();
        user_id = mauth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        newDoctorImg = findViewById(R.id.doctorProfile);
        newDoctorSpeciality = findViewById(R.id.doctorSpeciality);
        newDoctorAddress = findViewById(R.id.doctorAddress);
        newDoctorContact = findViewById(R.id.doctorContactNum);
        newDoctorName = findViewById(R.id.doctorname);
        AddBtn = findViewById(R.id.AddDoctor);
        progressBar = findViewById(R.id.newDoctorProgress);

        newDoctorImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(AddNewDoctor.this);
            }
        });

        AddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doctorName = newDoctorName.getText().toString();
                doctorAddress = newDoctorAddress.getText().toString();
                doctorSpeciality = newDoctorSpeciality.getText().toString();
                doctorContact = newDoctorContact.getText().toString();

                final String randomName = UUID.randomUUID().toString();

                if(main_uri != null && !TextUtils.isEmpty(doctorAddress) && !TextUtils.isEmpty(doctorName) && !TextUtils.isEmpty(doctorSpeciality)&& !TextUtils.isEmpty(doctorContact)){
                    progressBar.setVisibility(View.VISIBLE);
                    AddBtn.setEnabled(false);

                    File imageFile = new File(main_uri.getPath());

                    try {
                        compressedImageBitmap = new Compressor(AddNewDoctor.this)
                                .setMaxHeight(100)
                                .setMaxWidth(100)
                                .setQuality(10)
                                .compressToBitmap(imageFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //Uploadinf bitmap to firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] thumbBitmap = baos.toByteArray();

                    final StorageReference fikepath3 = storageReference.child("/doctor_Photos/thumbs").child(randomName+".jpg");
                    UploadTask thumbImage = fikepath3.putBytes(thumbBitmap);
                    thumbImage.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {

                                fikepath3.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        saveToFirestore(uri,doctorName,doctorAddress,doctorSpeciality,doctorContact);
                                    }
                                });

                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(AddNewDoctor.this, " Image Error" + error, Toast.LENGTH_LONG).show();
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                            AddBtn.setEnabled(true);

                        }
                    });
//
                }
                else {
                    Toast.makeText(AddNewDoctor.this, "No image", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    AddBtn.setEnabled(true);
                }

            }
        });

    }

    private void saveToFirestore(Uri uri, final String doctorName,final String doctorAddress,final String doctorSpeciality,final String doctorContact) {

        Uri downloadUri;
        //If task is not null that is change occured. So get new URI for image also change ThumbNail
        if(uri != null) {
            // downloadUri = task.getResult().getMetadata().getReference().getDownloadUrl().getResult();
            downloadUri=uri;
        }

        else {
            downloadUri = main_uri;
        }
        //Create map..with keys common and values
        Map<String,Object> userMap= new HashMap<>();

        userMap.put("Address",doctorAddress);
        userMap.put("D_imageURL",downloadUri.toString());
        userMap.put("D_name",doctorName);
        userMap.put("Speciality",doctorSpeciality);
        userMap.put("D_contact",doctorContact);

        firebaseFirestore.collection("Doctors").document(doctorName+doctorContact).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(AddNewDoctor.this, "New Doctor Saved Succesfully", Toast.LENGTH_LONG).show();
                    Intent main = new Intent(AddNewDoctor.this,MainActivity.class);
                    startActivity(main);
                }
                else {
                    String error = task.getException().getMessage();
                    Toast.makeText(AddNewDoctor.this, " FireStore Error" + error, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                main_uri = result.getUri();
                newDoctorImg.setImageURI(main_uri);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

            }
        }
    }


}
