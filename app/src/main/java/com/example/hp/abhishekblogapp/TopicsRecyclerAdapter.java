package com.example.hp.abhishekblogapp;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TopicsRecyclerAdapter extends RecyclerView.Adapter<TopicsRecyclerAdapter.ViewHolder> {

    public List<TopicsDocument> topicList;
    private Context context;
    private String user_id;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    String currentUser;

    public TopicsRecyclerAdapter(List<TopicsDocument> topicList){
        this.topicList = topicList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //INFLATE LAYOUT WITH CREATED LAYOUT ie blost_list_item
        firebaseFirestore =FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_list_item,parent,false);
//        forGlide
        context = parent.getContext();


        return new ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        //Start Updating
        //Message
        final String Topic = topicList.get(position).getTopicName();
        holder.setTopicName(Topic);

        holder.topicName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,MainActivity.class);
                intent.putExtra("GotTopicName",Topic);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mview;
        private TextView topicName;

        public ViewHolder(View itemView) {
            super(itemView);
            mview = itemView;
            topicName = mview.findViewById(R.id.topicName);
        }

        private void setTopicName(String message){
            topicName.setText(message);
            if(message==null){
            Toast.makeText(itemView.getContext(),message, Toast.LENGTH_SHORT).show();}
        }


    }
}
