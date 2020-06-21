package com.example.hp.abhishekblogapp;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ContactsRecyclerAdapter extends RecyclerView.Adapter<ContactsRecyclerAdapter.ViewHolder>{

    public List<ContactModelClass> contactList;
    private Context context;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    String currentUser;

    public ContactsRecyclerAdapter(List<ContactModelClass> contactList){
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public ContactsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //INFLATE LAYOUT WITH CREATED LAYOUT ie blost_list_item
        firebaseFirestore =FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item,parent,false);
//        forGlide
        context = parent.getContext();


        return new ContactsRecyclerAdapter.ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull final ContactsRecyclerAdapter.ViewHolder holder, int position) {

        //Start Updating
        //Message
       // String CommentMessage = commentList.get(position).getMessage();
        //String user_id_comment = commentList.get(position).getUser_id();
       // holder.setCommentMessage(CommentMessage);
        String D_name= contactList.get(position).getD_name();
        final String D_contact= contactList.get(position).getD_contact();
        String D_imageURL= contactList.get(position).getD_imageURL();
        String Address= contactList.get(position).getAddress();
        String Speciality= contactList.get(position).getSpeciality();

        //userImage and Name of comments
       holder.setDoctorName(D_name);
       holder.setDoctorAddress(Address);
       holder.setProfilePic(D_imageURL);
       holder.setSpeciality(Speciality);
       holder.setContactNumber(D_contact);
       holder.dial_but.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Uri number = Uri.parse("tel:"+D_contact);
               Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
               context.startActivity(callIntent);
           }
       });

    }



    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mview;
        private ImageView dial_but;
        private ImageView profilePic;
        private TextView DoctorName;
        private TextView Speciality;
        private TextView DoctorAddress;
        private TextView ContactNumber;

        public ViewHolder(View itemView) {
            super(itemView);
            mview = itemView;
            dial_but=mview.findViewById(R.id.Dial_image);
            profilePic = mview.findViewById(R.id.contactProfilePic);
            DoctorName = mview.findViewById(R.id.contactUsername);
            Speciality = mview.findViewById(R.id.Speciality);
            DoctorAddress = mview.findViewById(R.id.Address);
            ContactNumber = mview.findViewById(R.id.contactNumber);
            dial_but.requestFocus();


        }

        private void setDoctorName(String DocNmae){
            DoctorName.setText(DocNmae);
        }
        private void setSpeciality(String message){
            Speciality.setText(message);
        }
        private void setContactNumber(String message){
            ContactNumber.setText(message);
        }
        private void setDoctorAddress(String message){
            DoctorAddress.setText(message);
        }


        private void setProfilePic(String pic){

            Glide.with(context).load(pic).into(profilePic);

        }


    }

}
