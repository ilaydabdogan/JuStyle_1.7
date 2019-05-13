package com.pro.android.justyle;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class ViewArticleActivity extends AppCompatActivity {

    private TextView mArticleName;
    private TextView mArticleDescription;
    private TextView mArticleUserName;
    private ImageView mArticleImage;
    private TextView mAction;


    private  ImageAdapter mAdapter;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private FirebaseDatabase mDatabase;
    private String mPostKey;
    private String mArticleNameString;
    private String mArticleDescriptionString;
    private String mArticleImageString;
    private String mUserName;


    private String mActionString;



    @Override
    public void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_article);

        mArticleName = (TextView) findViewById(R.id.nameViewId);
        mArticleDescription = (TextView) findViewById(R.id.descriptionViewId);
        mArticleImage = (ImageView) findViewById(R.id.articleView);
        mArticleUserName = (TextView) findViewById(R.id.userNameViewId);
        mAction = (TextView) findViewById(R.id.actionId);

        mStorage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getInstance().getReference().child("marketplace");
        mPostKey = getIntent().getExtras().getString("item_key");

        mDatabaseRef.child(mPostKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mArticleNameString = (String) dataSnapshot.child("name").getValue();
                mArticleDescriptionString = (String) dataSnapshot.child("description").getValue();
                mArticleImageString = (String) dataSnapshot.child("imageUrl").getValue();
                mUserName = (String) dataSnapshot.child("userName").getValue();
                mActionString= (String) dataSnapshot.child("action").getValue();



                mArticleName.setText(mArticleNameString);
                mArticleDescription.setText(mArticleDescriptionString);
                mArticleUserName.setText(mUserName);
                mAction.setText(mActionString);


                Picasso.get()
                        .load(mArticleImageString)
                        .placeholder(R.mipmap.ic_launcher)
                        .resize(500,500)
                        .centerCrop()
                        .into(mArticleImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
