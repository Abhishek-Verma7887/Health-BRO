package com.example.hp.abhishekblogapp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class ContactFragment extends Fragment {

    private RecyclerView contact_list_View;
    //Create list of model class type
    private List<ContactModelClass> contactList;
    private DocumentSnapshot lastVisible;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mauth;
    private ContactsRecyclerAdapter contactsRecyclerAdapter;
    private boolean firstPageLoaded = true;

    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        //Get the recycler View
        contact_list_View = view.findViewById(R.id.contact_list_view);
        contactList = new ArrayList<>();
        contactsRecyclerAdapter = new ContactsRecyclerAdapter(contactList);

        //Set adapter to Recycler View
        contact_list_View.setLayoutManager(new LinearLayoutManager(getActivity()));
        contact_list_View.setAdapter(contactsRecyclerAdapter);

        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            contactList.clear();
        }
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();
            mauth = FirebaseAuth.getInstance();

            //Get Last item Scrolled in REcyclerView
            contact_list_View.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    boolean lastItem = !recyclerView.canScrollVertically(1);

                    if (lastItem) {
//                Toast.makeText(getContext(),"end of 3 posts",Toast.LENGTH_SHORT).show();
                        loadMorePosts();
                    }
                }
            });


            if (mauth.getCurrentUser() != null) {
                //Order according to date
                Query firstQuery = firebaseFirestore.collection("Doctors")
                        .orderBy("D_name", Query.Direction.DESCENDING)
                        .limit(5);


                //getActivity bcoz to stop the on scroll listener after page closed bcause it will still call load more post
                firstQuery.addSnapshotListener( new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {


                        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                            //get lastVisibile iff first page not loaded at starting
                            if (firstPageLoaded) {
                                // Get the last visible documentSnapshot
                                lastVisible = documentSnapshots.getDocuments()
                                        .get(documentSnapshots.size()-1);
                            }


                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    //Blog Id ..name same as that is Extender class
                                   // String BlogPostId = doc.getDocument().getId();
                                    //USE MODEL CLASS and save one object obtained into Model class list
                                    ContactModelClass contactModelClass = doc.getDocument().toObject(ContactModelClass.class);


                                    if (firstPageLoaded) {
                                        contactList.add(contactModelClass);
                                    }
                                    //Add new post to top
                                    else {
                                        contactList.add(0, contactModelClass);
                                    }


                                    contactsRecyclerAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                    }


                });

            }
        }


        return view;
    }

    private void loadMorePosts() {

        if (mauth.getCurrentUser() != null) {
            Query nextQuery = firebaseFirestore.collection("Doctors")
                    .orderBy("D_name", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(5);

            nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if(mauth.getCurrentUser()!= null){
                    //If no more posts than docSnaps will be empty leading to crash
                    if (!documentSnapshots.isEmpty()) {

                        // Get the last visible documentSnapshot
                        lastVisible = documentSnapshots.getDocuments()
                                .get(documentSnapshots.size() - 1);


                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                //USE MODEL CLASS and save one object obtained into Model class list
                              //  String BlogPostId = doc.getDocument().getId();
                                //USE MODEL CLASS and save one object obtained into Model class list
                                ContactModelClass contactModelClass = doc.getDocument().toObject(ContactModelClass.class);
                                contactList.add(contactModelClass);

                                contactsRecyclerAdapter.notifyDataSetChanged();
                            }
                        }

                    }
                    }

                }
            });
        }
    }



}

