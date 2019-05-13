package com.pro.android.justyle;


import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import static com.pro.android.justyle.FrontPageActivity.isWifiConn;
import static com.pro.android.justyle.FrontPageActivity.userUid;
import static com.pro.android.justyle.WardrobeFragment.mImageUri;


public class CreateArticleActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button mCreateButton;

    private EditText mArticleText;
    private EditText mArticleName;
    private ImageView mArticleImg;
    private ProgressBar mProgressBar;
    private TextView mUserName;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mCurrentUser;
    private String mAction;

    protected StorageReference mStorageRef;
    protected DatabaseReference mDatabaseRef;

    protected StorageTask mUploadTask;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_article);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mCurrentUser = mFirebaseAuth.getCurrentUser();
        mArticleText = findViewById(R.id.ArticleTextId);
        mArticleName = findViewById(R.id.ArticleNameId);
        mArticleImg = findViewById(R.id.articleImageId);
        mUserName = findViewById(R.id.createUserName);
        mUserName.setText(mCurrentUser.getEmail());


        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (this, R.array.action, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        // mArticleImg.setImageURI(mImageUri);
        Picasso.get()
                .load(mImageUri)
                .resize(600,600)
                .centerCrop()
                .into(mArticleImg);

        // get the user name

        mCreateButton = findViewById(R.id.buttonCreateArticleId);
        mProgressBar = findViewById(R.id.progressBarId);

        //use user uid to create a wardrobe for them


        mStorageRef = FirebaseStorage.getInstance().getReference(userUid);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(userUid);// this creates unique wardrobe
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isWifiConn || MoreActivity.mWifiCare) {// if connected to wifi

                    if (mUploadTask != null && mUploadTask.isInProgress()) {
                        // if there is already an upload task on progress we don't want to upload same pictures
                        Toast.makeText(CreateArticleActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                    } else {
                        uploadFile();
                    }
                }
                else {
                    Toast.makeText(CreateArticleActivity.this, "You should be connected to wifi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getFileExtension(Uri uri) {
        //to get file extension from the image

        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 100);

                            Toast.makeText(CreateArticleActivity.this, "Upload successful", Toast.LENGTH_LONG).show();



                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            // FIXME: 2019-05-02 syntax error: while has empty body
                            while (!urlTask.isSuccessful());


                            Uri downloadUrl = urlTask.getResult();


                            Upload upload = new Upload(mArticleName.getText().toString().trim(), downloadUrl.toString(),mArticleText.getText().toString().trim(), mUserName.getText().toString().trim(),mAction);


                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload);
                            openMyWardrobeActivity(); // start new activity

                        }


                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateArticleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
    private void openMyWardrobeActivity(){
        finish();
        Intent intent = new Intent(this, MyWardrobeActivity.class);
        startActivity(intent);
    }

    // spinner

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
         String aPosition = parent.getItemAtPosition(position).toString();
     //  Toast.makeText(parent.getContext(),aPosition, Toast.LENGTH_SHORT).show();

       // int result = Integer.parseInt(aPosition);

        switch (aPosition){
            case "To Sell":

                mAction = "To Sell";
                Toast.makeText(parent.getContext(),mAction, Toast.LENGTH_SHORT).show();

                break;

            case "To Rent":
                mAction = "To Rent";
                Toast.makeText(parent.getContext(),mAction, Toast.LENGTH_SHORT).show();

                break;

            case "To Lend":
                mAction = "To Lend";
                Toast.makeText(parent.getContext(),mAction, Toast.LENGTH_SHORT).show();

                break;

            case "Not Selected":
                mAction = "Not Selected";
                Toast.makeText(parent.getContext(),mAction, Toast.LENGTH_SHORT).show();

                break;

            }




    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}




