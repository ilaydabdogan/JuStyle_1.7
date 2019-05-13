package com.pro.android.justyle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pro.android.justyle.FrontPageActivity.userUid;


public class MyWardrobeActivity extends AppCompatActivity implements ImageAdapter.OnItemClickListener{
    private  ImageAdapter mAdapter;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    ImageButton btnSearch;
    EditText edtKeyword;
    String Keyword;
    private String mPostKey;


    private List<Upload> mUploads;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_wardrobe);

        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUploads = new ArrayList<>();

        mAdapter = new ImageAdapter(MyWardrobeActivity.this, mUploads);

    //    wardrobeClassName =  this.getLocalClassName();


        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(MyWardrobeActivity.this);


        edtKeyword = (EditText) findViewById(R.id.edtKeyword);
        btnSearch = (ImageButton) findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // FIXME: 2019-05-02 create a different class for this


                // get keyword and send it to server
                try {
                    Keyword = URLEncoder.encode(edtKeyword.getText().toString(), "utf-8");
                    Toast.makeText(MyWardrobeActivity.this, "Keyword: "+ Keyword, Toast.LENGTH_SHORT).show();

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });




        mStorage = FirebaseStorage.getInstance();


        mDatabaseRef = FirebaseDatabase.getInstance().getReference(userUid);

        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUploads.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);

                    upload.setKey(postSnapshot.getKey());
                    mUploads.add(upload);
                }
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(MyWardrobeActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



        @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "View article "+ position, Toast.LENGTH_SHORT).show();

            //Gets the article Key from Firebase
            mPostKey = mUploads.get(position).getKey();
            //Sends the article Key to the viewArticle activity
            Intent viewActivityIntent = new Intent(MyWardrobeActivity.this, ViewMyWardrobeArticleActivity.class);
            viewActivityIntent.putExtra("item_wardrobe_key", mPostKey);
            startActivity(viewActivityIntent);


        }




    @Override
    public void sendToMarketClick(int position) {
        Toast.makeText(this, "Send to market, click at position "+ position, Toast.LENGTH_SHORT).show();
// FIXME: 2019-05-02  should copy it to marketplace database in firebase

        Upload selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();

        // copy to Firebase database under marketplace folder

        Firebase.setAndroidContext(this);
        Firebase ref = new Firebase("https://justyle-1.firebaseio.com/");
// Generate a new push ID for the new post
    //    Firebase newPostRef = ref.child("marketplace").push();
     //   String newPostKey = newPostRef.getKey();
// Create the data we want to update
        Map newPost = new HashMap();


        newPost.put("name", selectedItem.getName());
        newPost.put("imageUrl", selectedItem.getImageUrl());
        newPost.put("description", selectedItem.getDescription());
        newPost.put("action", selectedItem.getAction());


        Map updatedUserData = new HashMap();
        //updatedUserData.put("marketplace/posts/" + newPostKey, true);
      //  updatedUserData.put("marketplace/" + newPostKey, newPost);
        updatedUserData.put("marketplace/" + selectedKey, newPost);


// Do a deep-path update
        ref.updateChildren(updatedUserData, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    System.out.println("Error updating data: " + firebaseError.getMessage());
                }
            }
        });
    }

    @Override
    public void onDeleteClick(int position) {
        Upload selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseRef.child(selectedKey).removeValue();
                Toast.makeText(MyWardrobeActivity.this, "Article deleted", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }


}
