package com.pro.android.justyle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class FrontPageActivity extends AppCompatActivity implements FragmentActionListener {

    private FirebaseAuth mFirebaseAuth;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    private Button mProfileButton, mActivityButton, mMoreButton;
    private ImageButton mCameraButton;
    public static String userUid;

    @SuppressLint("StaticFieldLeak")
    protected static View frontView;
    static boolean isWifiConn = false;
    static int level;
    static int scale;
    static double bpt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);
        mFragmentManager = getSupportFragmentManager();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mActivityButton =  findViewById(R.id.ActivityButton);
        mProfileButton =  findViewById(R.id.ProfileButton);
        mMoreButton =  findViewById(R.id.MoreButton);
        mCameraButton =  findViewById(R.id.CameraButton);
        frontView = (View) findViewById(R.id.fpView);
        frontView.setBackgroundColor(getResources().getColor(ProfileActivity.mBackgroundColor));
        updateView();


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userUid = currentUser.getUid();


        //wifi
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        for (Network network : connMgr.getAllNetworks()){
            NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
                isWifiConn |= networkInfo.isConnected();
            } else{
                isWifiConn = false;
            }
        }
        //battery
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, ifilter);
        assert batteryStatus != null;
        level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        bpt = (level / (double) scale)*100; //battery percentage

        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FrontPageActivity.this,ProfileActivity.class));
            }
        });

        //front page button
        mActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(FrontPageActivity.this,FrontPageActivity.class));

            }
        });
        mMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FrontPageActivity.this,MoreActivity.class));
            }
        });
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   startActivity(new Intent(FrontPageActivity.this, CameraActivity.class));

                addWardrobeFragment();
            }
        });


        if (mFirebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        addFrontPageFragment();



    }
    private void addFrontPageFragment(){
        mFragmentTransaction = mFragmentManager.beginTransaction();

        FrontPageFragment frontPageFragment = new FrontPageFragment();
        frontPageFragment.setFragmentActionListener(this);

        mFragmentTransaction.add(R.id.fragment_container, frontPageFragment);
        mFragmentTransaction.commit();

    }

    private void addWardrobeFragment(){
        mFragmentTransaction = mFragmentManager.beginTransaction();

        WardrobeFragment wardrobeFragment = new WardrobeFragment();

        Bundle bundle = new Bundle();
        wardrobeFragment.setArguments(bundle);

        mFragmentTransaction.replace(R.id.fragment_container, wardrobeFragment);
        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.commit();
    }

    private void addMarketplaceFragment() {
        mFragmentTransaction = mFragmentManager.beginTransaction();

        WardrobeFragment wardrobeFragment = new WardrobeFragment();

        Bundle bundle = new Bundle();
        wardrobeFragment.setArguments(bundle);

        mFragmentTransaction.replace(R.id.fragment_container, wardrobeFragment);
        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.commit();

    }

    public void updateView(){
        frontView.setBackgroundColor(getResources().getColor(ProfileActivity.mBackgroundColor));
    }

    @Override
    public void onWardrobeFragmentClicked() {

       // addWardrobeFragment();
        startActivity(new Intent(FrontPageActivity.this,MyWardrobeActivity.class));


    }
    @Override
    public void onMarketplaceFragmentClicked(){
       // addMarketplaceFragment();

        startActivity(new Intent(FrontPageActivity.this,MarketplaceActivity.class));


    }
}
