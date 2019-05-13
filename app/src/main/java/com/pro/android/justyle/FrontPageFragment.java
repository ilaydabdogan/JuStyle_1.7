package com.pro.android.justyle;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

public class FrontPageFragment  extends Fragment implements View.OnClickListener {
    private ImageView mTitleView;
    private ImageView mMarketplaceView;
    private ImageView mWardrobeView;
    View rootView;
    Context context;



    FragmentActionListener mFragmentActionListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        rootView = inflater.inflate(R.layout.fragment_front_page, container, false);
        mTitleView = (ImageView) rootView.findViewById(R.id.TitleView);
        mMarketplaceView = (ImageView) rootView.findViewById(R.id.MarketPlaceView);
        mWardrobeView = (ImageView) rootView.findViewById(R.id.WardrobeView);

        mMarketplaceView.setOnClickListener(this);
        mWardrobeView.setOnClickListener(this);

        initUI();

        return rootView;
    }
    public void setFragmentActionListener(FragmentActionListener fragmentActionListener){
        this.mFragmentActionListener = fragmentActionListener;
    }

    private void initUI(){
        context = getContext();
        mWardrobeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentActionListener.onWardrobeFragmentClicked();
            }
        });
        mMarketplaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentActionListener.onMarketplaceFragmentClicked();
            }

        });

    }

    @Override
    public void onClick(View v) {

    }
}
