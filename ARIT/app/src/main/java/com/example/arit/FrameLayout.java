package com.example.arit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;


public class FrameLayout extends AppCompatActivity implements View.OnClickListener{

    private final int Frag1 = 1;
    private final int Frag2 = 2;
    private final int Frag3 = 3;
    private final int Frag4 = 4;

    ImageButton btn_tab1, btn_tab2, btn_tab3, btn_tab4;

    Intent intent;
    String currentId;
    String currentName;

    Bundle bundle;

    ArrayAdapter adapter;
    ListView category_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_layout);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar);
        actionBar.setElevation(0);

        btn_tab1 = findViewById(R.id.homebtn);
        btn_tab2 = findViewById(R.id.postbtn);
        btn_tab3 = findViewById(R.id.searchbtn);
        btn_tab4 = findViewById(R.id.mypagebtn);

        btn_tab1.setOnClickListener(this);
        btn_tab2.setOnClickListener(this);
        btn_tab3.setOnClickListener(this);
        btn_tab4.setOnClickListener(this);


        intent = getIntent();
        currentId = intent.getStringExtra("currentId");
        currentName = intent.getStringExtra("currentName");

        bundle = new Bundle();
        bundle.putString("currentId", currentId);
        bundle.putString("currentName", currentName);

        callFragment(Frag1);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.homebtn :
                callFragment(Frag1);
                break;
            case R.id.postbtn :
                callFragment(Frag2);
                break;
            case R.id.searchbtn :
                callFragment(Frag3);
                break;
            case R.id.mypagebtn :
                callFragment(Frag4);
                break;
        }
    }

    private void callFragment(int fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (fragment) {
            case 1:

                HomeFrag homeFrag = new HomeFrag();
                transaction.replace(R.id.fragmentContainer, homeFrag);
                transaction.commit();
                homeFrag.setArguments(bundle);
                break;

            case 2 :

                PostFrag postFrag = new PostFrag();
                transaction.replace(R.id.fragmentContainer, postFrag);
                transaction.commit();
                postFrag.setArguments(bundle);
                break;

            case 3 :

                SearchFrag searchFrag = new SearchFrag();
                transaction.replace(R.id.fragmentContainer, searchFrag);
                transaction.commit();
                searchFrag.setArguments(bundle);
                break;

            case 4 :

                MypageFrag mypageFrag = new MypageFrag();
                transaction.replace(R.id.fragmentContainer, mypageFrag);
                transaction.commit();
                mypageFrag.setArguments(bundle);
                break;

        }
    }
    public void changeFragment(Fragment fragment, Bundle bundle){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
        fragment.setArguments(bundle);

    }
}
