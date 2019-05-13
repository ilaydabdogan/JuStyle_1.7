package com.pro.android.justyle;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.EditText;

import android.widget.Toast;

// import android.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;


public class MarketplaceActivity extends AppCompatActivity implements ImageAdapter.OnItemClickListener {

    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private FirebaseStorage mStorage;
    private DatabaseReference LikeRef;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private List<Upload> mUploads;
    MaterialSearchView searchView;
    EditText editText;
    private String mPostKey;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marketplace);



        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        editText =findViewById(R.id.searchView);

      // Search bar, TextWatcher allow us to instantly update data on other views
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {    // The count characters beginning at start are about to be replaced

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {      //  The characters beginning at start have just replaced old text

            }

            @Override
            public void afterTextChanged(Editable s) {                              // The text has been changed 
                if (!s.toString().isEmpty()) {
                    search(s.toString());


                } else {
                    search("");

                }
            } 
            

        });


        mUploads = new ArrayList<>();
        mAdapter = new ImageAdapter(MarketplaceActivity.this, mUploads);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(MarketplaceActivity.this);
        mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("marketplace");
        LikeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {

            {

            }

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

                Toast.makeText(MarketplaceActivity.this, databaseError.getMessage(),  Toast.LENGTH_SHORT).show();
            }
        });                                                                
    }
// Search bar, query is linked with the database and 
    private void search(String toString) {
 Query query = mDatabaseRef.orderByChild("name")
         .startAt(toString)
         .endAt(toString+"\uf8ff");

 query.addValueEventListener(new ValueEventListener() {
     @Override
     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
         if(dataSnapshot.hasChildren()) {
             mUploads.clear();
             for(DataSnapshot dss: dataSnapshot.getChildren())
             {
               final Upload upload = dss.getValue(Upload.class);
               mUploads.add(upload);
             }
                 ImageAdapter imageAdapter = new ImageAdapter(getApplicationContext(), mUploads);
                  mRecyclerView.setAdapter(imageAdapter);
                  imageAdapter.notifyDataSetChanged();
         }

     }

     @Override
     public void onCancelled(@NonNull DatabaseError databaseError) {

     }
 });

    }


    @Override
    public void onItemClick(int position) {
        //Gets the article Key from Firebase
        mPostKey = mUploads.get(position).getKey();
        //Sends the article Key to the viewArticle activity
        Intent viewActivityIntent = new Intent(MarketplaceActivity.this, ViewArticleActivity.class);
        viewActivityIntent.putExtra("item_key", mPostKey);
        startActivity(viewActivityIntent);
    }
   /* @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select Action");


        MenuItem sendToMarketplace = menu.add(Menu.NONE, 1, 1, "I am marketplace");
        MenuItem delete = menu.add(Menu.NONE, 2, 2, "Delete");

    }*/

    @Override
    public void sendToMarketClick(int position) {

    }

    @Override
    public void onDeleteClick(int position) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_marketplace, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }



    }









