package com.example.hp.abhishekblogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TopicsActivity extends AppCompatActivity {

    private Toolbar toolbarTopics;
    private Button alltopicsbutton;
    private String BlogPostId;
    private EditText topicTopicName;
    private RecyclerView topic_list_view;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    public List<TopicsDocument> topicList;
    private TopicsRecyclerAdapter topicsRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_list);

        toolbarTopics = findViewById(R.id.toolbarTopics);
        setSupportActionBar(toolbarTopics);
        getSupportActionBar().setTitle("Topics");
        //add back button to parent activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //Get the recycler View
        topic_list_view = findViewById(R.id.recycler_view_topics);
        topicList = new ArrayList<>();
        topicsRecyclerAdapter = new TopicsRecyclerAdapter(topicList);

        //Set adapter to Recycler View
        topic_list_view.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        topic_list_view.setAdapter(topicsRecyclerAdapter);



        alltopicsbutton=findViewById(R.id.AllTopicsBut);
        alltopicsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main = new Intent(TopicsActivity.this, MainActivity.class);
                startActivity(main);
                finish();
            }
        });

        //        Retrieve Comments into Recycler view
        Query mainQuery = firebaseFirestore.collection("NewTopic")
                .orderBy("TopicName", Query.Direction.ASCENDING);

        if(mAuth.getCurrentUser()!= null){
        mainQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {


                if(mAuth.getCurrentUser()!= null) {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            //USE MODEL CLASS and save one object obtained into Model class list
                            TopicsDocument topicData = doc.getDocument().toObject(TopicsDocument.class);
                            topicList.add(topicData);
                            topicsRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                }


            }
        });}


    }
}
