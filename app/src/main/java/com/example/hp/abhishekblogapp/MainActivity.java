package com.example.hp.abhishekblogapp;

import android.app.Activity;
import android.net.Uri;
import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import 	com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolBar;
    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    FirebaseFirestore firebaseFirestore;
    FloatingActionButton add_post_btn;
    FloatingActionButton add_doctor_btn;
    private  String user_id;
    public static final int RC_SIGN_IN = 1;


    private String GotTopicName;
    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment;
    private AccountFragment accountFragment;
    private ContactFragment contactFragment;
    private FirebaseFirestore fireStore = FirebaseFirestore.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




//        toolBar =  findViewById(R.id.toolbar);
//        setSupportActionBar(toolBar);
//
//        getSupportActionBar().setTitle("Photo Blog");

        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("Health Companion");

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();





         bottomNavigationView = findViewById(R.id.mainBottomNav);

         //fragments
         homeFragment = new HomeFragment();
         accountFragment = new AccountFragment();
         contactFragment = new ContactFragment();

       replaceFragment(homeFragment);

         //Onclick on Bottom Nav Bar
         bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
             @Override
             public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                 switch (item.getItemId()) {

                     case R.id.bottomAccount:
                         replaceFragment(accountFragment);
                         return true;


                     case R.id.bottomHome:
                         replaceFragment(homeFragment);
                         return true;


                     case R.id.bottomContact:
                         replaceFragment(contactFragment);
                         return true;
                 }
                 return false;
             }
         });




         add_post_btn = findViewById(R.id.addPostButton);
         add_doctor_btn = findViewById(R.id.addDoctorButton);

         /*if(FirebaseAuth.getInstance().getCurrentUser().isAnonymous()){
             add_post_btn.setVisibility(View.INVISIBLE);
         }*/

         add_post_btn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                 if (currentUser != null) {
                     user_id = mAuth.getCurrentUser().getUid();
                     firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                         @Override
                         public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                             if (task.getResult().exists()) {
                                 Intent addPost = new Intent(MainActivity.this, NewPost.class);
                                 startActivity(addPost);
                             } else {
                                 Toast.makeText(MainActivity.this, "Please choose profile photo and name", Toast.LENGTH_LONG).show();
                                 Intent main = new Intent(MainActivity.this, AccounrSetup.class);
                                 startActivity(main);
                             }
                         }
                     });
                 } else {
                     Toast.makeText(MainActivity.this, "Please choose profile photo and name", Toast.LENGTH_LONG).show();
                     Intent main = new Intent(MainActivity.this, AccounrSetup.class);
                     startActivity(main);
                 }


             }
         });

         add_doctor_btn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                 if (currentUser != null) {
                     Intent acS = new Intent(MainActivity.this,AddNewDoctor.class);
                     startActivity(acS);


                 }
             }
         });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                   homeFragment =new HomeFragment();
                 replaceFragment(homeFragment);
                    // User is signed in
                    add_doctor_btn.setVisibility(View.INVISIBLE);
                   // onSignedInInitialize(user.getDisplayName());

                    user_id = mAuth.getCurrentUser().getUid();

                    //check if user is admin
                   fireStore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                if(task.getResult().exists()) {
                                    boolean isadmin=task.getResult().getBoolean("isAdmin");
                                    if(isadmin){
                                        add_doctor_btn.setVisibility(View.VISIBLE);
                                       // Toast.makeText(MainActivity.this, "Admin is present", Toast.LENGTH_LONG).show();
                                    }

                                }
                                }
                        }
                    });

                    //Check abhishek below code can create problem
                     if(FirebaseAuth.getInstance().getCurrentUser().isAnonymous()){
             add_post_btn.setVisibility(View.INVISIBLE);
             add_doctor_btn.setVisibility(View.INVISIBLE);
         }else{
                         add_post_btn.setVisibility(View.VISIBLE);
                     }





                } else {
                    //onSignedOutCleanup();
                    // User is signed out
                    //change theme statement if upload error
                    androidx.fragment.app.FragmentTransaction fg=getSupportFragmentManager().beginTransaction();
                    fg.remove(homeFragment);
                    fg.detach(homeFragment);
                    fg.commit();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.AnonymousBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .setTheme(R.style.GreenTheme)
                                    .build(),
                            RC_SIGN_IN);


                }
            }
        };

    }

    @Override
    public void onActivityResult(int requestCode , int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
              //  homeFragment = new HomeFragment();
            // replaceFragment(homeFragment);

                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Signed in cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
   /* @Override
    protected void onStart() {
        super.onStart();
        //Check if user logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            sendLogin();
            finish();
        }

    }*/
  /* @Override
   protected void onStart(){
       super.onStart();
       mAuth.addAuthStateListener(mAuthStateListener);
   }*/
   @Override
   protected void onResume() {
       super.onResume();
       mAuth.addAuthStateListener(mAuthStateListener);
   }
    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            androidx.fragment.app.FragmentTransaction fg=getSupportFragmentManager().beginTransaction();
            fg.remove(homeFragment);
            fg.commitAllowingStateLoss();
            mAuth.removeAuthStateListener(mAuthStateListener);

        }
    }


    //add menu drawable resource to action bar

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_logout:
                //replaceFragment(notificationFragment);
                androidx.fragment.app.FragmentTransaction fg=getSupportFragmentManager().beginTransaction();
                fg.remove(homeFragment);
                fg.detach(homeFragment);
                fg.commit();
               //recreate();
                if(!fg.isEmpty()){
                  //  Toast.makeText(this, "Empty fg!", Toast.LENGTH_SHORT).show();
                }
                AuthUI.getInstance().signOut(this);
                return true;


            case R.id.action_settings:
                Intent acS = new Intent(MainActivity.this,AccounrSetup.class);
                startActivity(acS);

                return true;

            case R.id.action_search:
                Intent bcS = new Intent(MainActivity.this,TopicsActivity.class);
                startActivity(bcS);


                default:
                    return false;
        }
    }

    private void logOut() {
        mAuth.signOut();
    }

    //Fragment transition to change fragment when pressed
    private void replaceFragment(androidx.fragment.app.Fragment fragment){
        androidx.fragment.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container,fragment);
        fragmentTransaction.commit();
    }

}
